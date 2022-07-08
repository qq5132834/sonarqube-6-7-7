package com.zuk.cdt;

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

    }
}
