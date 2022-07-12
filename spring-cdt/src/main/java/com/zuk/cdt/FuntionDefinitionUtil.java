package com.zuk.cdt;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;

import java.util.*;

public class FuntionDefinitionUtil {
    public static List<IASTFunctionDefinition> getFuncationDefinistion(IASTTranslationUnit unit, String filePath){
        List<IASTFunctionDefinition> fds = new ArrayList<>();
        IASTDeclaration[] iastDeclarations = unit.getDeclarations();
        Arrays.stream(iastDeclarations).forEach(
                e->{
                    System.out.println(e.getClass().getName());
                    if(e instanceof IASTFunctionDefinition){
                        fds.add((IASTFunctionDefinition) e);
                    }

                    getIBinding(e);
                }
        );
//        unit.accept(new ASTVisitor() {
//            @Override
//            public int visit(IASTDeclarator declarator) {
//                if(declarator instanceof IASTFunctionDefinition){
//                    fds.add((IASTFunctionDefinition) declarator);
//                }
//                return 1;
//            }
//        });
        printlnResult();
        return fds;
    }

    //存储文件的ibinding
    public static Set<IASTNode> IAST_NODE_SET = new HashSet<>();
    private static void getIBinding(IASTNode iastNode){
        if(iastNode instanceof IASTName){
            IAST_NODE_SET.add(iastNode);
        }
        if(iastNode != null && iastNode.getChildren() != null){
            Arrays.stream(iastNode.getChildren()).forEach(FuntionDefinitionUtil::getIBinding);
        }
    }

    public static void printlnResult(){
        System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
        IAST_NODE_SET.stream().forEach(ins->{
            //System.out.println("IASTNodeClass:" + ins.getClass().getName());
            IASTName iastName = (IASTName) ins;
            //System.out.println("IASTNameClass:" + iastName.getClass().getName());
            IBinding iBinding = iastName.resolveBinding();
            //System.out.println("IBindingClass:" + iBinding.getClass().getName());
            if (!(iBinding instanceof ProblemBinding)) {
                try {
                    IScope iScope = iBinding.getScope();
                    EScopeKind kind = iScope.getKind();
                    IName iName = iScope.getScopeName();
                    IASTFileLocation location = iName.getFileLocation();
                    String simpleName = new String(iName.getSimpleID());
                    System.out.println("kind:" + kind.toString()
                                    + ",simpleName:" + simpleName
                                    + ",lineNumber:" + ins.getFileLocation().getStartingLineNumber()
                                    + ",file:" + ins.getFileLocation().getFileName()
                                    + ",rawSignature:" + ins.getRawSignature().replaceAll("\n" , ""));
                }catch (Exception e) {}

            }
        });
    }

}
