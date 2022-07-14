package com.zuk.test;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.adaptor.EclipseStarter;


public class ZukMainForWorkspace {
    public static void main(String[] args) {

//        IScope scope = iastTranslationUnit.getScope();
//        if(scope instanceof CPPNamespaceScope){
//            CPPNamespaceScope cppNamespaceScope = (CPPNamespaceScope) scope;
//            IBinding [] bindings = cppNamespaceScope.find("uls_l1_ue_context_config_req_t");
//        }
//        IBinding [] bindings = scope.find("uls_l1_ue_context_config_req_t");
//        IASTName[] name = iastTranslationUnit.getDeclarationsInAST(bindings[0]);
//        IASTNode ast = name[0].getParent().getParent();

//        IProgressMonitor progressMonitor = new NullProgressMonitor();
//        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//        IProject project = root.getProject("DesiredProjectName");
//        project.create(progressMonitor);
//        project.open(progressMonitor);

//        ITranslationUnit tu = (ITranslationUnit) CDTUITools.getEditorInputCElement(editor.getEditorInput());
//        ITranslationUnit tu= (ITranslationUnit) CoreModel.getDefault().create(file);

        EclipseStarter eclipseStarter = new EclipseStarter();
        //EclipseStarter.startup();
        /**
         * 从workspace开始
         * */

        String workspacePath = "D:\\development\\java\\eclipse-cpp-indigo-SR2-incubation-win32-x86_64\\workspace";
        IWorkspace iWorkspace1 = new Workspace();

//        IProgressMonitor progressMonitor = new NullProgressMonitor();
        IPath path = new org.eclipse.core.runtime.Path("project/folder/file.cpp");
        IWorkspace iWorkspace = ResourcesPlugin.getWorkspace();
//        IWorkspaceRoot iWorkspaceRoot = iWorkspace.getRoot();
//        IFile iFile = iWorkspaceRoot.getFile(path);
//        ITranslationUnit tu = (ITranslationUnit) CoreModel.getDefault().create(iFile);
//        IASTTranslationUnit ast = tu.getAST();
//        IConfigurationElement a;
        //
//        ICProject project = CoreModel.getDefault().getCModel().getCProject("D:\\development\\java\\eclipse-cpp-indigo-SR2-incubation-win32-x86_64\\workspace\\helloworld");
//        IIndex index =CCorePlugin.getIndexManager().getIndex(project);

    }
}
