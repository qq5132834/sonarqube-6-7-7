package com.zuk.cdt;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String file = "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring-cdt\\src\\main\\resources\\DnsCache.cc";
        IASTTranslationUnit iastTranslationUnit = CDTParser.parse(file, CDTParser.Language.CPP);
        List<IASTFunctionDefinition> functionDefinitions = FuntionDefinitionUtil.getFuncationDefinistion(iastTranslationUnit, file);
        functionDefinitions.stream().forEach(e->{
            int startLine = e.getFileLocation().getStartingLineNumber();
            int endLine = e.getFileLocation().getEndingLineNumber();
            String funName = e.getDeclarator().getName().toString();

            System.out.println(funName + "," + startLine + "," + endLine);
        });
        System.out.println("");
    }
}
