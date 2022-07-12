package com.zuk.cdt.binding;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/***
 * 变量函数和函数的调用关系
 */
public class MethodCallIBinding {

    private static Set<Class> CLASS_SET = new HashSet<>();
    private static Set<IASTNode> FUNCTION_IASTNODE = new HashSet<>();

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

            checkIBinding(e);

            checkIBinding(e.getParent());

            IASTNode[] iastNodeChildren = e.getParent().getChildren();
            Arrays.stream(iastNodeChildren).forEach(child->{
                checkIBinding(e);
            });

            Arrays.stream(e.getChildren()).forEach(child->{
                checkIBinding(e);
            });

            System.out.println();

        });
    }

    private static void checkIBinding(IASTNode iastNode){
        System.out.println(iastNode.getClass().getName());
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
