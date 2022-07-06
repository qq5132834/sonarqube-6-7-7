package com.zuk.cdt;

import com.google.common.collect.ImmutableMap;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.dom.parser.c.ANSICParserExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.ParserFactory;
import org.eclipse.cdt.internal.core.parser.InternalParserUtil;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContentProvider;
import org.eclipse.core.runtime.CoreException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class CDTParser {
    public enum Language {C, CPP}
    static ILanguage CLang;
    static ILanguage CPPLang;
    static Integer PARSER_OPTIONS = ILanguage.OPTION_IS_SOURCE_UNIT | ILanguage.OPTION_NO_IMAGE_LOCATIONS;
    static IParserLogService parserLog = ParserFactory.createDefaultLogService();
    static {
        ICParserExtensionConfiguration parserConfig = new ANSICParserExtensionConfiguration();
        CLang = new GCCLanguage(){
            @Override
            protected ICParserExtensionConfiguration getParserExtensionConfiguration() {
                return parserConfig;
            }
        };
        CPPLang = GPPLanguage.getDefault();
    }

    public static IASTTranslationUnit parse(String file, Language lang) throws IOException, CoreException {
        byte[] bytes = java.nio.file.Files.readAllBytes(Paths.get(file));
        String code = new String(bytes);
        FileContent fileContent = FileContent.create(file, code.toCharArray());
        ILanguage language;
        if (lang == Language.C) {
            language = CLang;
        }
        else {
            language = CPPLang;
        }
        return language.getASTTranslationUnit(fileContent, new IScannerInfo() {
            ImmutableMap<String, String> MACROS;
            {
                ImmutableMap.Builder<String, String> macrosBuilder = new ImmutableMap.Builder();
                macrosBuilder.put("_Static_assert(c, m)", "");
                macrosBuilder.put("__builtin_constant_p", "__builtin_constant_p");
                macrosBuilder.put("__builtin_types_compatible_p(t1, t2)", "__builtin_types_compatible_p(({t1 arg1; arg1;}),({t2 arg2; arg2;}))");
                macrosBuilder.put("__offsetof__", "__offsetof__");
                macrosBuilder.put("__func__", "\"__func__\"");
                macrosBuilder.put("__FUNCTION__", "\"__FUNCTION__\"");
                macrosBuilder.put("__PRETTY_FUNCTION__", "\"__PRETTY_FUNCTION__\"");
                macrosBuilder.put("__attribute__(a)", "");
                macrosBuilder.put("_INTSIZEOF(n)", "((sizeof(n) + sizeof(int) - 1) & ~(sizeof(int) - 1))");
                macrosBuilder.put("__builtin_va_arg(ap, t)", "*(t *)((ap += _INTSIZEOF(t)) - _INTSIZEOF(t))");
                MACROS = macrosBuilder.build();
            }


            @Override
            public Map<String, String> getDefinedSymbols() {
                return MACROS;
            }

            @Override
            public String[] getIncludePaths() {
                return new String[0];
            }
        }, new InternalFileContentProvider() {
            @Override
            public InternalFileContent getContentForInclusion(String filePath) {
                return InternalParserUtil.createExternalFileContent(filePath, InternalParserUtil.SYSTEM_DEFAULT_ENCODING);
            }

            @Override
            public InternalFileContent getContentForInclusion(IIndexFileLocation ifl, String s) {
                return InternalParserUtil.createFileContent(ifl);
            }
        }, null, PARSER_OPTIONS, parserLog);

    }

}
