package com.zuk.cdt.binding;

import com.zuk.cdt.FuntionDefinitionUtil;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFieldReference;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;

import java.util.*;

/***
 * 变量函数和函数的调用关系
 */
public class MethodCallIBinding {

    private static Set<Class> CLASS_SET = new HashSet<>();
    private static List<IASTNode> FUNCTION_IASTNODE = new ArrayList<>();

    public static void funcationCall(IASTNode iastNode){
        CLASS_SET.add(iastNode.getClass());
        if (checkIASTNode(iastNode)) {
            FUNCTION_IASTNODE.add(iastNode);
        }
    }

    public static void printResultAndClearSet(){
        //
        CLASS_SET.stream().forEach(e->{
            System.out.println(e.getName());
        });
        //函数相关节点
        FUNCTION_IASTNODE.stream().forEach(e->{

            IASTFileLocation iastFileLocation = e.getFileLocation();
            String content = e.getRawSignature();
            int startLineNumber = iastFileLocation.getStartingLineNumber();
            int endLineNumber = iastFileLocation.getEndingLineNumber();
            System.out.println(startLineNumber + "->" + content);

            //checkIBinding(e);

            //checkIBinding(e.getParent());

//            Arrays.stream(e.getParent().getChildren()).forEach(child->{
//                checkIBinding(e);
//            });

            IASTNode[] iastNodeChildren = e.getChildren();
            Arrays.stream(iastNodeChildren).forEach(child->{
                if (child instanceof CPPASTFieldReference) {
                    CPPASTFieldReference cppastFieldReference = (CPPASTFieldReference) child;

//                    checkIBinding(child);
//
//
//                    IASTName iastName = cppastFieldReference.getFieldName();
//
//                    checkIBinding(iastName);
                    IASTNode[] fieldChildren = cppastFieldReference.getChildren();
                    Arrays.stream(fieldChildren).forEach(f->{
                        checkIBinding(f);
//                        IASTNode iastNode = FuntionDefinitionUtil.IAST_NODE_SET.get(f);
//                        if(iastNode != null){
//                            IASTName iastName1 = (IASTName) iastNode;
//                            IBinding iBinding = iastName1.resolveBinding();
//                            try {
//                                IScope iScope = iBinding.getScope();
//                                System.out.println();
//                            } catch (Exception e11 ) {}
//                        }

                    });
                }
            });

//            System.out.println();

        });
    }

    private static void checkIBinding(IASTNode iastNode){
        System.out.println(iastNode.getClass().getName());
        System.out.println(iastNode.getRawSignature());
        if(iastNode instanceof IASTName){
            IASTName iastName = (IASTName) iastNode;
            IBinding iBinding = iastName.getBinding();
            if (iBinding instanceof ProblemBinding) {
                System.out.println(iBinding.toString());
            }
            else {
                try {
                    IScope iScope = iBinding.getScope();
                    EScopeKind kind = iScope.getKind();
                    IName iName = iScope.getScopeName();
                    IASTFileLocation location = iName.getFileLocation();
                    System.out.println();
                }catch (Exception ex) { }
            }
        }
    }

    private static boolean checkIASTNode(IASTNode iastNode){
        if(iastNode instanceof CPPASTFunctionCallExpression){
            //函数调用
            return true;
        }
        else if (iastNode instanceof CPPASTFunctionDefinition) {
            /***
             * 函数定义:
             * 1. 文件的函数定义，没有iBinding、和iscope
             */
            return false;
        }
        else if (iastNode instanceof CPPASTFunctionDeclarator){
            /***
             * 函数申明:
             * 1. 文件的函数定义，没有iBinding、和iscope
             */
            return false;
        }
        return false;
    }

}
