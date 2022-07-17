package com.zuk.cdt;

import com.zuk.cdt.file.CxxFileFrame;
import com.zuk.cdt.file.function.CFileFunctionUtil;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.runtime.CoreException;

import java.io.IOException;
import java.util.List;

public class ZukMainForC {

    public static void main(String[] args) throws Exception {
        String filepath = "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring-cdt\\src\\main\\resources\\c\\src\\btree.c";
        analyzeFile(filepath);
    }


    public static CxxFileFrame analyzeFile(String filepath) throws IOException, CoreException {
        IASTTranslationUnit iastTranslationUnit = CDTParser.parse(filepath, CDTParser.Language.C);
        List<IASTFunctionDefinition>  iastFunctionDefinitionList = CFileFunctionUtil.getFuncationDefinistion(iastTranslationUnit);
        iastFunctionDefinitionList.stream().forEach(fun -> {
            IASTFunctionDeclarator iastFunctionDeclarator = fun.getDeclarator();
            IASTName iastName = iastFunctionDeclarator.getName();
            System.out.println(iastName.getRawSignature()); //C语言函数名称输出
        });
        return null;
    }

}
