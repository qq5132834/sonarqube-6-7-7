/*
 * SonarQube
 * Copyright (C) 2009-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.rule.ws;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Languages;
import org.sonar.api.server.debt.DebtRemediationFunction;
import org.sonar.api.server.debt.internal.DefaultDebtRemediationFunction;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.rule.RuleMetadataDto;
import org.sonar.db.rule.RuleParamDto;
import org.sonar.markdown.Markdown;
import org.sonar.server.rule.ws.SearchAction.SearchResult;
import org.sonar.server.text.MacroInterpreter;
import org.sonarqube.ws.Common;
import org.sonarqube.ws.Rules;

import static java.lang.String.format;
import static org.sonar.api.utils.DateUtils.formatDateTime;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_CREATED_AT;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_DEBT_OVERLOADED;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_DEBT_REM_FUNCTION;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_DEFAULT_DEBT_REM_FUNCTION;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_DEFAULT_REM_FUNCTION;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_EFFORT_TO_FIX_DESCRIPTION;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_GAP_DESCRIPTION;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_HTML_DESCRIPTION;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_INTERNAL_KEY;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_IS_TEMPLATE;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_LANGUAGE;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_LANGUAGE_NAME;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_MARKDOWN_DESCRIPTION;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_NAME;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_NOTE_LOGIN;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_PARAMS;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_REM_FUNCTION;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_REM_FUNCTION_OVERLOADED;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_REPO;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_SEVERITY;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_STATUS;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_SYSTEM_TAGS;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_TAGS;
import static org.sonarqube.ws.client.rule.RulesWsParameters.FIELD_TEMPLATE_KEY;

/**
 * Conversion of {@link org.sonar.db.rule.RuleDto} to {@link org.sonarqube.ws.Rules.Rule}
 */
public class RuleMapper {

  private final Languages languages;
  private final MacroInterpreter macroInterpreter;

  public RuleMapper(final Languages languages, final MacroInterpreter macroInterpreter) {
    this.languages = languages;
    this.macroInterpreter = macroInterpreter;
  }

  public Rules.Rule toWsRule(RuleDefinitionDto ruleDefinitionDto, SearchResult result, Set<String> fieldsToReturn) {
    Rules.Rule.Builder ruleResponse = Rules.Rule.newBuilder();
    applyRuleDefinition(ruleResponse, ruleDefinitionDto, result, fieldsToReturn);
    return ruleResponse.build();
  }

  public Rules.Rule toWsRule(RuleDefinitionDto ruleDefinitionDto, SearchResult result, Set<String> fieldsToReturn, RuleMetadataDto metadata) {
    Rules.Rule.Builder ruleResponse = Rules.Rule.newBuilder();
    applyRuleDefinition(ruleResponse, ruleDefinitionDto, result, fieldsToReturn);
    applyRuleMetadata(ruleResponse, metadata, fieldsToReturn);
    setDebtRemediationFunctionFields(ruleResponse, ruleDefinitionDto, metadata, fieldsToReturn);
    return ruleResponse.build();
  }

  public Rules.Rule.Builder applyRuleDefinition(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDefinitionDto, SearchResult result, Set<String> fieldsToReturn) {

    // Mandatory fields
    ruleResponse.setKey(ruleDefinitionDto.getKey().toString());
    ruleResponse.setType(Common.RuleType.valueOf(ruleDefinitionDto.getType()));

    // Optional fields
    setRepository(ruleResponse, ruleDefinitionDto, fieldsToReturn);
    setName(ruleResponse, ruleDefinitionDto, fieldsToReturn);
    setStatus(ruleResponse, ruleDefinitionDto, fieldsToReturn);
    setSysTags(ruleResponse, ruleDefinitionDto, fieldsToReturn);
    setParams(ruleResponse, ruleDefinitionDto, result, fieldsToReturn);
    setCreatedAt(ruleResponse, ruleDefinitionDto, fieldsToReturn);
    setDescriptionFields(ruleResponse, ruleDefinitionDto, fieldsToReturn);
    setSeverity(ruleResponse, ruleDefinitionDto, fieldsToReturn);
    setInternalKey(ruleResponse, ruleDefinitionDto, fieldsToReturn);
    setLanguage(ruleResponse, ruleDefinitionDto, fieldsToReturn);
    setLanguageName(ruleResponse, ruleDefinitionDto, fieldsToReturn);
    setIsTemplate(ruleResponse, ruleDefinitionDto, fieldsToReturn);
    setTemplateKey(ruleResponse, ruleDefinitionDto, result, fieldsToReturn);
    setDefaultDebtRemediationFunctionFields(ruleResponse, ruleDefinitionDto, fieldsToReturn);
    setEffortToFixDescription(ruleResponse, ruleDefinitionDto, fieldsToReturn);
    return ruleResponse;
  }

  private void applyRuleMetadata(Rules.Rule.Builder ruleResponse, RuleMetadataDto metadata, Set<String> fieldsToReturn) {
    setTags(ruleResponse, metadata, fieldsToReturn);
    setNotesFields(ruleResponse, metadata, fieldsToReturn);
    setIsRemediationFunctionOverloaded(ruleResponse, metadata, fieldsToReturn);
  }

  private static void setRepository(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_REPO)) {
      ruleResponse.setRepo(ruleDto.getKey().repository());
    }
  }

  private static void setEffortToFixDescription(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, Set<String> fieldsToReturn) {
    if ((shouldReturnField(fieldsToReturn, FIELD_EFFORT_TO_FIX_DESCRIPTION) || shouldReturnField(fieldsToReturn, FIELD_GAP_DESCRIPTION))
      && ruleDto.getGapDescription() != null) {
      ruleResponse.setEffortToFixDescription(ruleDto.getGapDescription());
      ruleResponse.setGapDescription(ruleDto.getGapDescription());
    }
  }

  private static void setIsRemediationFunctionOverloaded(Rules.Rule.Builder ruleResponse, RuleMetadataDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_DEBT_OVERLOADED) || shouldReturnField(fieldsToReturn, FIELD_REM_FUNCTION_OVERLOADED)) {
      ruleResponse.setDebtOverloaded(isRemediationFunctionOverloaded(ruleDto));
      ruleResponse.setRemFnOverloaded(isRemediationFunctionOverloaded(ruleDto));
    }
  }

  private static void setDefaultDebtRemediationFunctionFields(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_DEFAULT_DEBT_REM_FUNCTION) || shouldReturnField(fieldsToReturn, FIELD_DEFAULT_REM_FUNCTION)) {
      DebtRemediationFunction defaultDebtRemediationFunction = defaultDebtRemediationFunction(ruleDto);
      if (defaultDebtRemediationFunction != null) {
        String gapMultiplier = defaultDebtRemediationFunction.gapMultiplier();
        if (gapMultiplier != null) {
          ruleResponse.setDefaultRemFnGapMultiplier(gapMultiplier);
          // Set deprecated field
          ruleResponse.setDefaultDebtRemFnCoeff(gapMultiplier);
        }
        String baseEffort = defaultDebtRemediationFunction.baseEffort();
        if (baseEffort != null) {
          ruleResponse.setDefaultRemFnBaseEffort(baseEffort);
          // Set deprecated field
          ruleResponse.setDefaultDebtRemFnOffset(baseEffort);
        }
        if (defaultDebtRemediationFunction.type() != null) {
          ruleResponse.setDefaultRemFnType(defaultDebtRemediationFunction.type().name());
          // Set deprecated field
          ruleResponse.setDefaultDebtRemFnType(defaultDebtRemediationFunction.type().name());
        }
      }
    }
  }

  private static void setDebtRemediationFunctionFields(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDefinitionDto, RuleMetadataDto ruleMetadataDto,
    Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_DEBT_REM_FUNCTION) || shouldReturnField(fieldsToReturn, FIELD_REM_FUNCTION)) {
      DebtRemediationFunction debtRemediationFunction = debtRemediationFunction(ruleDefinitionDto, ruleMetadataDto);
      if (debtRemediationFunction != null) {
        if (debtRemediationFunction.type() != null) {
          ruleResponse.setRemFnType(debtRemediationFunction.type().name());
          // Set deprecated field
          ruleResponse.setDebtRemFnType(debtRemediationFunction.type().name());
        }
        String gapMultiplier = debtRemediationFunction.gapMultiplier();
        if (gapMultiplier != null) {
          ruleResponse.setRemFnGapMultiplier(gapMultiplier);
          // Set deprecated field
          ruleResponse.setDebtRemFnCoeff(gapMultiplier);
        }
        String baseEffort = debtRemediationFunction.baseEffort();
        if (baseEffort != null) {
          ruleResponse.setRemFnBaseEffort(baseEffort);
          // Set deprecated field
          ruleResponse.setDebtRemFnOffset(baseEffort);
        }
      }
    }
  }

  private static void setName(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_NAME) && ruleDto.getName() != null) {
      ruleResponse.setName(ruleDto.getName());
    }
  }

  private static void setStatus(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_STATUS) && ruleDto.getStatus() != null) {
      ruleResponse.setStatus(Common.RuleStatus.valueOf(ruleDto.getStatus().toString()));
    }
  }

  private static void setTags(Rules.Rule.Builder ruleResponse, RuleMetadataDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_TAGS)) {
      ruleResponse.getTagsBuilder().addAllTags(ruleDto.getTags());
    }
  }

  private static void setSysTags(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_SYSTEM_TAGS)) {
      ruleResponse.getSysTagsBuilder().addAllSysTags(ruleDto.getSystemTags());
    }
  }

  private static void setParams(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, SearchResult searchResult, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_PARAMS)) {
      List<RuleParamDto> ruleParameters = searchResult.getRuleParamsByRuleId().get(ruleDto.getId());
      ruleResponse.getParamsBuilder().addAllParams(FluentIterable.from(ruleParameters)
        .transform(RuleParamDtoToWsRuleParam.INSTANCE)
        .toList());
    }
  }

  private static void setCreatedAt(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_CREATED_AT)) {
      ruleResponse.setCreatedAt(formatDateTime(ruleDto.getCreatedAt()));
    }
  }

  private void setDescriptionFields(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_HTML_DESCRIPTION)) {
      String description = ruleDto.getDescription();
      if (description != null) {
        switch (ruleDto.getDescriptionFormat()) {
          case MARKDOWN:
            ruleResponse.setHtmlDesc(macroInterpreter.interpret(Markdown.convertToHtml(description)));
            break;
          case HTML:
            ruleResponse.setHtmlDesc(macroInterpreter.interpret(description));
            break;
          default:
            throw new IllegalStateException(format("Rule description format '%s' is unknown for key '%s'", ruleDto.getDescriptionFormat(), ruleDto.getKey().toString()));
        }
      }
    }

    if (shouldReturnField(fieldsToReturn, FIELD_MARKDOWN_DESCRIPTION) && ruleDto.getDescription() != null) {
      ruleResponse.setMdDesc(ruleDto.getDescription());
    }
  }

  private void setNotesFields(Rules.Rule.Builder ruleResponse, RuleMetadataDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, "htmlNote") && ruleDto.getNoteData() != null) {
      ruleResponse.setHtmlNote(macroInterpreter.interpret(Markdown.convertToHtml(ruleDto.getNoteData())));
    }
    if (shouldReturnField(fieldsToReturn, "mdNote") && ruleDto.getNoteData() != null) {
      ruleResponse.setMdNote(ruleDto.getNoteData());
    }
    if (shouldReturnField(fieldsToReturn, FIELD_NOTE_LOGIN) && ruleDto.getNoteUserLogin() != null) {
      ruleResponse.setNoteLogin(ruleDto.getNoteUserLogin());
    }
  }

  private static void setSeverity(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_SEVERITY) && ruleDto.getSeverityString() != null) {
      ruleResponse.setSeverity(ruleDto.getSeverityString());
    }
  }

  private static void setInternalKey(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_INTERNAL_KEY) && ruleDto.getConfigKey() != null) {
      ruleResponse.setInternalKey(ruleDto.getConfigKey());
    }
  }

  private static void setLanguage(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_LANGUAGE) && ruleDto.getLanguage() != null) {
      ruleResponse.setLang(ruleDto.getLanguage());
    }
  }

  private void setLanguageName(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_LANGUAGE_NAME) && ruleDto.getLanguage() != null) {
      String languageKey = ruleDto.getLanguage();
      Language language = languages.get(languageKey);
      ruleResponse.setLangName(language == null ? languageKey : language.getName());
    }
  }

  private static void setIsTemplate(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_IS_TEMPLATE)) {
      ruleResponse.setIsTemplate(ruleDto.isTemplate());
    }
  }

  private static void setTemplateKey(Rules.Rule.Builder ruleResponse, RuleDefinitionDto ruleDto, SearchResult result, Set<String> fieldsToReturn) {
    if (shouldReturnField(fieldsToReturn, FIELD_TEMPLATE_KEY) && ruleDto.isCustomRule()) {
      RuleDefinitionDto templateRule = result.getTemplateRulesByRuleId().get(ruleDto.getTemplateId());
      if (templateRule != null) {
        ruleResponse.setTemplateKey(templateRule.getKey().toString());
      }
    }
  }

  private static boolean shouldReturnField(Set<String> fieldsToReturn, String fieldName) {
    return fieldsToReturn.isEmpty() || fieldsToReturn.contains(fieldName);
  }

  private static boolean isRemediationFunctionOverloaded(RuleMetadataDto rule) {
    return rule.getRemediationFunction() != null;
  }

  @CheckForNull
  private static DebtRemediationFunction defaultDebtRemediationFunction(final RuleDefinitionDto ruleDto) {
    final String function = ruleDto.getDefRemediationFunction();
    if (function == null || function.isEmpty()) {
      return null;
    } else {
      return new DefaultDebtRemediationFunction(
        DebtRemediationFunction.Type.valueOf(function.toUpperCase(Locale.ENGLISH)),
        ruleDto.getDefRemediationGapMultiplier(),
        ruleDto.getDefRemediationBaseEffort());
    }
  }

  @CheckForNull
  private static DebtRemediationFunction debtRemediationFunction(RuleDefinitionDto ruleDefinitionDto, RuleMetadataDto ruleMetadataDto) {
    final String function = ruleMetadataDto.getRemediationFunction();
    if (function == null || function.isEmpty()) {
      return defaultDebtRemediationFunction(ruleDefinitionDto);
    } else {
      return new DefaultDebtRemediationFunction(
        DebtRemediationFunction.Type.valueOf(function.toUpperCase(Locale.ENGLISH)),
        ruleMetadataDto.getRemediationGapMultiplier(),
        ruleMetadataDto.getRemediationBaseEffort());
    }
  }

  private enum RuleParamDtoToWsRuleParam implements Function<RuleParamDto, Rules.Rule.Param> {
    INSTANCE;

    @Override
    public Rules.Rule.Param apply(@Nonnull RuleParamDto param) {
      Rules.Rule.Param.Builder paramResponse = Rules.Rule.Param.newBuilder();
      paramResponse.setKey(param.getName());
      if (param.getDescription() != null) {
        paramResponse.setHtmlDesc(Markdown.convertToHtml(param.getDescription()));
      }
      if (param.getDefaultValue() != null) {
        paramResponse.setDefaultValue(param.getDefaultValue());
      }
      if (param.getType() != null) {
        paramResponse.setType(param.getType());
      }

      return paramResponse.build();
    }
  }
}
