package com.zuk.cdt;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IField;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.framework.internal.core.BundleContextImpl;
import org.eclipse.osgi.framework.internal.core.BundleHost;
import org.osgi.framework.BundleContext;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String file = "D:\\development\\java\\eclipse-cpp-indigo-SR2-incubation-win32-x86_64\\workspace\\helloworld\\DnsCache.cc";
        IASTTranslationUnit iastTranslationUnit = CDTParser.parse(file, CDTParser.Language.CPP);

        //文件函数输出
        List<IASTFunctionDefinition> functionDefinitions = FuntionDefinitionUtil.getFuncationDefinistion(iastTranslationUnit, file);
        functionDefinitions.stream().forEach(e->{
            int startLine = e.getFileLocation().getStartingLineNumber();
            int endLine = e.getFileLocation().getEndingLineNumber();
            String funName = e.getDeclarator().getName().toString();

            System.out.println(funName + "," + startLine + "," + endLine);
        });


//        IProgressMonitor progressMonitor = new NullProgressMonitor();
//        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//        IProject project = root.getProject("DesiredProjectName");
//        project.create(progressMonitor);
//        project.open(progressMonitor);

//        ITranslationUnit tu = (ITranslationUnit) CDTUITools.getEditorInputCElement(editor.getEditorInput());
//        ITranslationUnit tu= (ITranslationUnit) CoreModel.getDefault().create(file);

        /**
         * 从workspace开始
         * */
//        IProgressMonitor progressMonitor = new NullProgressMonitor();
//        IPath path = new Path(file);
//        IWorkspace iWorkspace = ResourcesPlugin.getWorkspace();
//        IWorkspaceRoot iWorkspaceRoot = iWorkspace.getRoot();
//        IFile iFile = iWorkspaceRoot.getFile(path);
//        ITranslationUnit tu = (ITranslationUnit) CoreModel.getDefault().create(iFile);
//        IASTTranslationUnit ast = tu.getAST();

        //
//        ICProject project = CoreModel.getDefault().getCModel().getCProject("D:\\development\\java\\eclipse-cpp-indigo-SR2-incubation-win32-x86_64\\workspace\\helloworld");
//        IIndex index =CCorePlugin.getIndexManager().getIndex(project);

        System.out.println("");
    }
}
