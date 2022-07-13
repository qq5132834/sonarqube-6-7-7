package com.zuk.cdt.binding;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;

import java.util.*;

/***
 * 变量函数和函数的调用关系
 */
public class MethodCallIBinding {

    private static Set<Class> CLASS_SET = new HashSet<>();

    private static List<IASTNode> FUNCTION_DEFINITION = new ArrayList<>();
    private static List<IASTNode> FUNCTION_CALL = new ArrayList<>();
    private static List<IASTNode> FUNCTION_DECLARATOR = new ArrayList<>();

    public static void funcationCall(IASTNode iastNode){
        //CLASS_SET.add(iastNode.getClass());
        checkIASTNode(iastNode);
    }

    public static void printResultAndClearSet(){
        //输出节点类型
        CLASS_SET.stream().forEach(e->{
            System.out.println(e.getName());
        });
        //
        /***
         * 输出函数调用行号和内容
         * 1、函数调用org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression节点类型，
         * 通过getChildren递归至 org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName节点，无法继续递归。
         * 推断CPPASTName为叶子节点。
         */
        FUNCTION_CALL.stream().forEach(e->{
            System.out.println(e.getFileLocation().getStartingLineNumber() + "," + e.getRawSignature());
            Arrays.stream(e.getChildren()).forEach(e1->{
                System.out.println("--" + e1.getRawSignature() + ",class:" + e1.getClass().getName());
                Arrays.stream(e1.getChildren()).forEach(e2->{
                    System.out.println("----" + e2.getRawSignature() + ",class:" + e2.getClass().getName());
                    Arrays.stream(e2.getChildren()).forEach(e3->{
                        System.out.println("------" + e3.getRawSignature() + ",class:" + e3.getClass().getName());
                        Arrays.stream(e3.getChildren()).forEach(e4->{
                            System.out.println("--------" + e4.getRawSignature() + ",class:" + e4.getClass().getName());
                        });
                    });
                });
            });
        });
        //函数相关节点
        FUNCTION_CALL.stream().forEach(e->{
            System.out.println(e.getFileLocation().getStartingLineNumber() + "," + e.getRawSignature());

            if(!(e.getRawSignature().contains("(") && e.getRawSignature().contains(")"))){
                System.out.println("宏不处理");
                return;
            }

            IASTNode[] iastNodeChildren = e.getChildren();
            if(iastNodeChildren != null && iastNodeChildren.length > 0){
                IASTNode iastNodeFirst = iastNodeChildren[0];

                if (iastNodeFirst instanceof CPPASTIdExpression) {
                    //内部调用，例如：lock(mutex_)
                    CPPASTIdExpression cppastIdExpression = (CPPASTIdExpression) iastNodeFirst;
                    IASTNode iastNode = cppastIdExpression.getChildren()[0];
                    String functionCallName = iastNode.getRawSignature(); //方法调用名称；
                    int functionCallLineNumber = iastNode.getFileLocation().getStartingLineNumber(); //方法调用行号
                    System.out.println();
                }

                if (iastNodeFirst instanceof CPPASTFieldReference) {
                    //类调用，例如：cache_pool_.get(host_port)
                    CPPASTFieldReference cppastFieldReference = (CPPASTFieldReference) iastNodeFirst;
                    int functionCallLineNumber = cppastFieldReference.getChildren()[0].getFileLocation().getStartingLineNumber(); //方法调用行号
                    String reference = cppastFieldReference.getChildren()[0].getRawSignature(); //引用类变量名称
                    String functionCallName = cppastFieldReference.getChildren()[1].getRawSignature(); //类变量方法调用名称
                    System.out.println();
                }
            }

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
            /***
             * 函数调用
             *
             */
            FUNCTION_CALL.add(iastNode);
            return true;
        }
        else if (iastNode instanceof CPPASTFunctionDefinition) {
            /***
             * 函数定义:
             * 1. 文件的函数定义，没有iBinding、和iscope
             */
            FUNCTION_DEFINITION.add(iastNode);
            return true;
        }
        else if (iastNode instanceof CPPASTFunctionDeclarator){
            /***
             * 函数申明:
             * 1. 文件的函数定义，没有iBinding、和iscope
             */
            FUNCTION_DECLARATOR.add(iastNode);
            return true;
        }
        return false;
    }

}
