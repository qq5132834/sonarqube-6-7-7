package com.zuk.cdt;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.internal.core.dom.parser.ASTAmbiguousNode;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNamespaceScope;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ClassTypeHelper;
import org.eclipse.core.runtime.adaptor.EclipseStarter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ZukMain {
    public static void main(String[] args) throws Exception {
        String file = "D:\\development\\java\\eclipse-cpp-indigo-SR2-incubation-win32-x86_64\\workspace\\helloworld\\DnsCache.cc";
        IASTTranslationUnit iastTranslationUnit = CDTParser.parse(file, CDTParser.Language.CPP);

        //文件函数输出
        List<IASTFunctionDefinition> functionDefinitions = FuntionDefinitionUtil.getFuncationDefinistion(iastTranslationUnit, file);
        functionDefinitions.stream().forEach(e->{
            int startLine = e.getFileLocation().getStartingLineNumber();
            int endLine = e.getFileLocation().getEndingLineNumber();
            String funName = e.getDeclarator().getName().toString();

            System.out.println("函数：" + funName + "," + startLine + "," + endLine);



            IScope scope = e.getScope();
            System.out.println(scope.getScopeName());
            IBinding [] bindings = scope.find("uls_l1_ue_context_config_req_t");
            Arrays.stream(bindings).forEach(bindingEle->{
                System.out.println(bindingEle.getName());
            });
            IASTName[] name = iastTranslationUnit.getDeclarationsInAST(bindings[0]);
            System.out.println();
        });


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

        System.out.println("");
    }



//    public static ISet getMethodOverrides(IASTTranslationUnit tu) {
//        ASTAmbiguousNode.NameCollector anc = new ASTAmbiguousNode.NameCollector();
//        tu.accept(anc);
//        Set<IBinding> bindings = new HashSet<>();
//        Stream.of(anc.getNames()).forEach(it -> bindings.add(it.resolveBinding()));
//        ISetWriter methodOverrides = vf.setWriter();
//        bindings.stream().filter(ICPPMethod.class::isInstance).forEach(override -> {
//            Stream.of(ClassTypeHelper.findOverridden((ICPPMethod) override, tu)).forEach(base -> {
//                try {
//                    methodOverrides.insert(vf.tuple(br.resolveBinding(base), br.resolveBinding(override)));
//                } catch (FactTypeUseException | URISyntaxException e) {
//                    err("Got FactTypeUseException\n" + e.getMessage());
//                }
//            });
//        });
//        return methodOverrides.done();
//    }
}
