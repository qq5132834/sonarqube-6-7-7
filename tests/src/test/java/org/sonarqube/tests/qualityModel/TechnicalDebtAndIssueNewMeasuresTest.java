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
package org.sonarqube.tests.qualityModel;

import com.sonar.orchestrator.Orchestrator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonarqube.tests.Category2Suite;
import util.ItUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static util.ItUtils.getLeakPeriodValue;
import static util.ItUtils.setServerProperty;

/**
 * SONAR-4776, SONAR-9534
 */
public class TechnicalDebtAndIssueNewMeasuresTest {

  private static final String DATE_31_DAYS_AGO = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now().minusDays(31));

  @ClassRule
  public static Orchestrator orchestrator = Category2Suite.ORCHESTRATOR;

  @AfterClass
  public static void resetPeriod() throws Exception {
    ItUtils.resetPeriod(orchestrator);
  }

  @Before
  public void cleanUpAnalysisData() {
    orchestrator.resetData();
  }

  @Test
  public void since_30_days_with_constant_effort() throws Exception {
    setServerProperty(orchestrator, "sonar.leak.period", "30");
    defineQualityProfile("one-issue-per-line");
    provisionSampleProject();

    // Execute an analysis in the past to have a past snapshot without any issues
    setSampleProjectQualityProfile("empty");
    runSampleProjectAnalysis("v1", "sonar.projectDate", DATE_31_DAYS_AGO);

    // Second analysis issues will be created -> new issues and new technical debt
    setSampleProjectQualityProfile("one-issue-per-line");
    runSampleProjectAnalysis("v1");
    assertLeakPeriodForComponent("sample", 26, 26);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 13, 13);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 13, 13);

    // Third analysis, with exactly the same profile -> no new issues but still the same on leak => same values as before
    runSampleProjectAnalysis("v1");
    assertLeakPeriodForComponent("sample", 26, 26);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 13, 13);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 13, 13);

    // Fourth analysis, with new files and modified files -> new issues and new technical debt
    runSampleProjectAnalysis("v2");
    assertLeakPeriodForComponent("sample", 43, 43);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 13, 13);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 17, 17);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassAdded.xoo", 13, 13);

    // Fifth analysis, no change -> no new issues but still the same on leak => same values as before
    runSampleProjectAnalysis("v2");
    assertLeakPeriodForComponent("sample", 43, 43);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 13, 13);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 17, 17);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassAdded.xoo", 13, 13);
  }

  @Test
  public void since_30_days_with_effort_change() throws Exception {
    setServerProperty(orchestrator, "sonar.leak.period", "30");
    defineQualityProfile("one-issue-per-line");
    provisionSampleProject();

    // Execute an analysis in the past to have a past snapshot without any issues
    setSampleProjectQualityProfile("empty");
    runSampleProjectAnalysis("v1", "sonar.projectDate", DATE_31_DAYS_AGO);

    // Second analysis issues will be created -> new issues and new technical debt
    setSampleProjectQualityProfile("one-issue-per-line");
    runSampleProjectAnalysis("v1");
    assertLeakPeriodForComponent("sample", 26, 26);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 13, 13);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 13, 13);

    // Third analysis, with exactly the same profile -> no new issues but still the same on leak => same values as before
    runSampleProjectAnalysis("v1", "sonar.oneIssuePerLine.effortToFix", "10");
    assertLeakPeriodForComponent("sample", 260, 26);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 130, 13);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 130, 13);

    // Fourth analysis, with new files, modified files and increased effort -> new issues and new technical debt
    runSampleProjectAnalysis("v2", "sonar.oneIssuePerLine.effortToFix", "10");
    assertLeakPeriodForComponent("sample", 430, 43);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 130, 13);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 170, 17);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassAdded.xoo", 130, 13);

    // Fifth analysis, effort divided by 2 -> no new issues but still the same on leak => same values as before
    runSampleProjectAnalysis("v2", "sonar.oneIssuePerLine.effortToFix", "5");
    assertLeakPeriodForComponent("sample", 215, 43);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 65, 13);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 85, 17);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassAdded.xoo", 65, 13);
  }

  @Test
  public void since_previous_version_with_constant_effort() throws Exception {
    setServerProperty(orchestrator, "sonar.leak.period", "previous_version");
    defineQualityProfile("one-issue-per-line");
    provisionSampleProject();

    // Execute an analysis in the past to have a past snapshot without any issues
    setSampleProjectQualityProfile("empty");
    runSampleProjectAnalysis("v1", "sonar.projectDate", DATE_31_DAYS_AGO);

    // Second analysis issues will be created -> new issues and new technical debt
    setSampleProjectQualityProfile("one-issue-per-line");
    runSampleProjectAnalysis("v1");
    assertLeakPeriodForComponent("sample", 26, 26);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 13, 13);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 13, 13);

    // Third analysis, with exactly the same profile -> no new issues but still the same on leak => same values as before
    runSampleProjectAnalysis("v1");
    assertLeakPeriodForComponent("sample", 26, 26);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 13, 13);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 13, 13);

    // Fourth analysis, with new files and modified files -> new issues and new technical debt
    runSampleProjectAnalysis("v2");
    assertLeakPeriodForComponent("sample", 17, 17);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 0, 0);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 4, 4);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassAdded.xoo", 13, 13);

    // Fifth analysis, no change -> no new issues but still the same on leak => same values as before
    runSampleProjectAnalysis("v2");
    assertLeakPeriodForComponent("sample", 17, 17);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 0, 0);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 4, 4);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassAdded.xoo", 13, 13);
  }

  @Test
  public void since_previous_version_with_effort_change() throws Exception {
    setServerProperty(orchestrator, "sonar.leak.period", "previous_version");
    defineQualityProfile("one-issue-per-line");
    provisionSampleProject();

    // Execute an analysis in the past to have a past snapshot without any issues
    setSampleProjectQualityProfile("empty");
    runSampleProjectAnalysis("v1", "sonar.projectDate", DATE_31_DAYS_AGO);

    // Second analysis issues will be created -> new issues and new technical debt
    setSampleProjectQualityProfile("one-issue-per-line");
    runSampleProjectAnalysis("v1");
    assertLeakPeriodForComponent("sample", 26, 26);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 13, 13);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 13, 13);

    // Third analysis, with exactly the same profile -> no new issues but still the same on leak => same values as before
    runSampleProjectAnalysis("v1", "sonar.oneIssuePerLine.effortToFix", "10");
    assertLeakPeriodForComponent("sample", 260, 26);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 130, 13);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 130, 13);

    // Fourth analysis, with new files, modified files and increased effort -> new issues and new technical debt
    runSampleProjectAnalysis("v2", "sonar.oneIssuePerLine.effortToFix", "10");
    assertLeakPeriodForComponent("sample", 170, 17);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 0, 0);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 40, 4);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassAdded.xoo", 130, 13);

    // Fifth analysis, no change -> no new issues but still the same on leak => same values as before
    runSampleProjectAnalysis("v2", "sonar.oneIssuePerLine.effortToFix", "10");
    assertLeakPeriodForComponent("sample", 170, 17);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/UnchangedClass.xoo", 0, 0);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassToModify.xoo", 40, 4);
    assertLeakPeriodForComponent("sample:src/main/xoo/sample/ClassAdded.xoo", 130, 13);
  }

  private void assertLeakPeriodForComponent(String componentKey, int expectedDebt, int expectedIssues) {
    assertThat(getLeakPeriodValue(orchestrator, componentKey, "new_technical_debt")).isEqualTo(expectedDebt);
    assertThat(getLeakPeriodValue(orchestrator, componentKey, "new_violations")).isEqualTo(expectedIssues);
    assertThat(getLeakPeriodValue(orchestrator, componentKey, "new_code_smells")).isEqualTo(expectedIssues);
    assertThat(getLeakPeriodValue(orchestrator, componentKey, "new_bugs")).isZero();
    assertThat(getLeakPeriodValue(orchestrator, componentKey, "new_vulnerabilities")).isZero();
    assertThat(getLeakPeriodValue(orchestrator, componentKey, "new_blocker_violations")).isZero();
    assertThat(getLeakPeriodValue(orchestrator, componentKey, "new_critical_violations")).isZero();
    assertThat(getLeakPeriodValue(orchestrator, componentKey, "new_major_violations")).isZero();
    assertThat(getLeakPeriodValue(orchestrator, componentKey, "new_minor_violations")).isEqualTo(expectedIssues);
    assertThat(getLeakPeriodValue(orchestrator, componentKey, "new_info_violations")).isZero();
  }

  private void setSampleProjectQualityProfile(String qualityProfileKey) {
    orchestrator.getServer().associateProjectToQualityProfile("sample", "xoo", qualityProfileKey);
  }

  private void provisionSampleProject() {
    orchestrator.getServer().provisionProject("sample", "sample");
  }

  private void defineQualityProfile(String qualityProfileKey) {
    ItUtils.restoreProfile(orchestrator, getClass().getResource("/measure/" + qualityProfileKey + ".xml"));
  }

  private void runSampleProjectAnalysis(String version, String... properties) {
    ItUtils.runVerboseProjectAnalysis(orchestrator, "shared/xoo-history-" + version, properties);
  }

}
