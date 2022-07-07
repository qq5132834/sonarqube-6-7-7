package com.zuk.cdt;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String file = "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring-cdt\\src\\main\\resources\\DnsCache.cc";
        IASTTranslationUnit iastTranslationUnit = CDTParser.parse(file, CDTParser.Language.CPP);

        //文件函数输出
        List<IASTFunctionDefinition> functionDefinitions = FuntionDefinitionUtil.getFuncationDefinistion(iastTranslationUnit, file);
        functionDefinitions.stream().forEach(e->{
            int startLine = e.getFileLocation().getStartingLineNumber();
            int endLine = e.getFileLocation().getEndingLineNumber();
            String funName = e.getDeclarator().getName().toString();

            System.out.println(funName + "," + startLine + "," + endLine);
        });

        /**
         * 1. org.eclipse.core.resources.xxxxx.jar
         *
         * */
//        IPath path = new Path(file);
//        IWorkspace iWorkspace = ResourcesPlugin.getWorkspace();
//        IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
//        ITranslationUnit tu = (ITranslationUnit) CoreModel.getDefault().create(iFile);
//        IASTTranslationUnit ast = tu.getAST();
        //
        ICProject project = CoreModel.getDefault().getCModel().getCProject("C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring-cdt\\src\\main\\resources");
        IIndex index =CCorePlugin.getIndexManager().getIndex(project);
//
//
//        org.eclipse.core.runtime.Plugin a;

        System.out.println("");
    }
}
