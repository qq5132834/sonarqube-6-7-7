package com.zuk.cdt.file.function.call;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;

import java.util.*;

/***
 * C++
 * 函数和函数的调用关系
 */
public class CxxFunctionCallUtil {

    private Set<Class> CLASS_SET = new HashSet<>();

    private List<IASTNode> FUNCTION_DEFINITION = new ArrayList<>();
    private List<IASTNode> FUNCTION_CALL = new ArrayList<>();
    private List<IASTNode> FUNCTION_DECLARATOR = new ArrayList<>();
    private List<FunctionCallDto> DECLARE_VARIABLE_LIST = new ArrayList<>();

    public void funcationCall(IASTNode iastNode){
        //CLASS_SET.add(iastNode.getClass());
        checkIASTNode(iastNode);
    }

    private void printResultAndClearSet(){
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
            System.out.println(e.getFileLocation().getStartingLineNumber() + "," + e.getRawSignature()); checkIBinding(e);
            Arrays.stream(e.getChildren()).forEach(e1->{
                System.out.println("--" + e1.getRawSignature() + ",class:" + e1.getClass().getName()); checkIBinding(e1);
                Arrays.stream(e1.getChildren()).forEach(e2->{
                    System.out.println("----" + e2.getRawSignature() + ",class:" + e2.getClass().getName()); checkIBinding(e2);
                    Arrays.stream(e2.getChildren()).forEach(e3->{
                        System.out.println("------" + e3.getRawSignature() + ",class:" + e3.getClass().getName()); checkIBinding(e3);
                        Arrays.stream(e3.getChildren()).forEach(e4->{
                            System.out.println("--------" + e4.getRawSignature() + ",class:" + e4.getClass().getName()); checkIBinding(e4);
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
                    int functionCallLineNumber = iastNode.getFileLocation().getStartingLineNumber(); //
                    // 方法调用行号
                    //多态暂时采用入参的个数进行区别，将来再优化
                    FunctionCallDto dto = null;
                    CPPASTName cppastName = new GetCPPASTName(iastNode).getCPPASTName();
                    if(cppastName != null){
                        dto = FunctionCallDto.createInstanceByIASTName((IASTName) cppastName);
                        if(dto == null){
                            dto = FunctionCallDto.builder()
                                                .setCallFunctionName(functionCallName)
                                                .setCallFunctionLineNumber(functionCallLineNumber)
                                                .build();
                        }
                        DECLARE_VARIABLE_LIST.add(dto);
                        System.out.println();
                    }
                    System.out.println();
                }

                if (iastNodeFirst instanceof CPPASTFieldReference) {
                    //类调用，例如：cache_pool_.get(host_port)
                    CPPASTFieldReference cppastFieldReference = (CPPASTFieldReference) iastNodeFirst;
                    int functionCallLineNumber = cppastFieldReference.getChildren()[0].getFileLocation().getStartingLineNumber(); //方法调用行号
                    String reference = cppastFieldReference.getChildren()[0].getRawSignature(); //引用类变量名称
                    CPPASTName cppastName = new GetCPPASTName(cppastFieldReference.getChildren()[0]).getCPPASTName();
                    FunctionCallDto dto = null;
                    if(cppastName != null){
                        //获取作用域，根据作用域
                        dto = FunctionCallDto.createInstanceByIASTName((IASTName) cppastName);
                        if(dto != null){
                            EScopeKind eScopeKind = dto.getBuilder().geteScopeKind();
                            String simpleName = dto.getBuilder().getScopeSimpleName();
                            IASTFileLocation iastFileLocation = dto.getBuilder().getIastFileLocation();
                            String rawSignature = dto.getBuilder().getVariableName();
                            System.out.println();
                        }
                        else {
                            dto = FunctionCallDto.builder().build();
                        }
                        System.out.println();
                    }

                    //
                    String callFunctionName = cppastFieldReference.getChildren()[1].getRawSignature();
                    dto.getBuilder().setCallFunctionName(callFunctionName)
                            .setCallFunctionLineNumber(cppastFieldReference.getChildren()[1].getFileLocation().getStartingLineNumber());;
                    DECLARE_VARIABLE_LIST.add(dto);

                    System.out.println();
                }
            }

        });
    }

    private void checkIBinding(IASTNode iastNode){
//        System.out.println(iastNode.getClass().getName());
//        System.out.println(iastNode.getRawSignature());
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

    public List<FunctionCallDto> getFunctionCall(){
        try {
            printResultAndClearSet();
            List<FunctionCallDto> list = new ArrayList<>();
            list.addAll(DECLARE_VARIABLE_LIST);
            return list;
        }
        finally {
            cleanAll();
        }
    }

    private void cleanAll(){
        CLASS_SET.clear();
        FUNCTION_DEFINITION.clear();
        FUNCTION_CALL.clear();
        FUNCTION_DECLARATOR.clear();
        DECLARE_VARIABLE_LIST.clear();
    }

    private boolean checkIASTNode(IASTNode iastNode){
        if(iastNode instanceof CPPASTFunctionCallExpression){
            /***
             * 函数调用
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


    /***
     * 获取作用域
     */
    private static class GetCPPASTName{
        private  CPPASTName cppastName;
        private GetCPPASTName(IASTNode iastNode) {
            this.searchCPPASTName(iastNode);
        }
        private void searchCPPASTName(IASTNode iastNode){
            if(iastNode instanceof CPPASTName){
                this.cppastName = (CPPASTName)iastNode;
            }
            for (IASTNode n : iastNode.getChildren()) {
                this.searchCPPASTName(n);
            }
        }
        private CPPASTName getCPPASTName(){
            return this.cppastName;
        }
    }

}
