package com.zuk.cdt;

import com.zuk.cdt.file.CxxFileFrame;
import com.zuk.cdt.file.function.CFileFunctionUtil;
import com.zuk.cdt.file.function.call.CFunctionCallUtil;
import com.zuk.cdt.file.function.call.FunctionCallDto;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.core.runtime.CoreException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ZukMainForC {

    public static void main(String[] args) throws Exception {
        String filepath = "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring-cdt\\src\\main\\resources\\c\\src\\btree.c";
        analyzeFile(filepath);
    }


    public static CxxFileFrame analyzeFile(String filepath) throws IOException, CoreException {
        IASTTranslationUnit iastTranslationUnit = CDTParser.parse(filepath, CDTParser.Language.C);
        List<IASTFunctionDefinition>  iastFunctionDefinitionList = CFileFunctionUtil.getFuncationDefinistion(iastTranslationUnit);
        iastFunctionDefinitionList.stream().forEach(fun -> {

            System.out.println(fun.getRawSignature());

            IASTFunctionDeclarator iastFunctionDeclarator = fun.getDeclarator();
            IASTName iastName = iastFunctionDeclarator.getName();
            //C语言函数名称输出
            System.out.println("函数名称：" + iastName.getRawSignature());

            recur(fun);

            List<FunctionCallDto> cxxFunctionCallDtoList = CFunctionCallUtil.getFunctionCall();
            System.out.println();
        });
        return null;
    }

    /***
     * 遍历AST
     */
    public static void recur(IASTNode iastNode){

        //输出节点信息
        doIASTNode(iastNode, ZukMainForC::printIASTNode);

        //收集当前函数中的函数调用
        doIASTNode(iastNode, CFunctionCallUtil::funcationCall);

//        if (iastNode instanceof CASTFieldReference) {
//            //变量定义
//            System.out.println();
//        }
//        if (iastNode instanceof CASTFunctionCallExpression) {
//            //函数调用
//            System.out.println();
//        }
//        if(iastNode instanceof IASTName){
//            IASTName iastName = (IASTName) iastNode;
//            IBinding iBinding = iastName.resolveBinding();
//            Class cls = iBinding.getClass();
//            System.out.println(iBinding.getClass().getName());
//            System.out.println(iastName.getClass().getName());
//            if (iBinding instanceof CField) {
//                try {//属性
//                    IScope iScope = iBinding.getScope();
//                    EScopeKind eScopeKind = iScope.getKind();
//                    IName iName = iScope.getScopeName();
//                    if (iName != null) {
//                        String simpleName = new String(iName.getSimpleID());
//                        IASTFileLocation iastFileLocation = iName.getFileLocation();
//                        String filename = iastFileLocation.getFileName();
//                        int startLineNumber = iastFileLocation.getStartingLineNumber();
//                    }
//                    System.out.println();
//                }catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//                System.out.println();
//            }
//            if (iBinding instanceof CVariable){
//                try {//变量
//                    IScope iScope = iBinding.getScope();
//                    EScopeKind eScopeKind = iScope.getKind();
//                    IName iName = iScope.getScopeName();
//                    if (iName != null) {
//                        String simpleName = new String(iName.getSimpleID());
//                        IASTFileLocation iastFileLocation = iName.getFileLocation();
//                        String filename = iastFileLocation.getFileName();
//                        int startLineNumber = iastFileLocation.getStartingLineNumber();
//                    }
//                    System.out.println();
//                }catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//                System.out.println();
//            }
//        }

//        //获取方法中入参、变量信息
//        CxxFunctionVariableUtil cxxFunctionVariableUtil = new CxxFunctionVariableUtil();
//        doIASTNode(iastNode, cxxFunctionVariableUtil::methodParams);
//        IAST_NAME_WITH_IBINDING_SET.addAll(cxxFunctionVariableUtil.getIAST_NAME_WITH_IBINDING_SET());
//
//        //获取方法中函数调用信息
//        doIASTNode(iastNode, CxxFunctionCallUtil::funcationCall);

        //递归遍历IASTNode的子节点
        if (iastNode.getChildren() != null) {
            Arrays.stream(iastNode.getChildren()).forEach(ZukMainForC::recur);
        }
    }

    private static void printIASTNode(IASTNode iastNode){
        System.out.println("行号：" + iastNode.getFileLocation().getStartingLineNumber() + ",\n内容：" + iastNode.getRawSignature() + ",\nclass:" + iastNode.getClass().getName());
    }

    private static void doIASTNode(IASTNode iastNode, Consumer<IASTNode> consumer){
        consumer.accept(iastNode);
    }



}
