package com.zuk.cdt;

import com.zuk.cdt.binding.MethodCallIBinding;
import com.zuk.cdt.file.var.FileVariableUtil;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import java.util.Arrays;
import java.util.function.Consumer;

public class IASTNodeRecursive {

    /***
     * 遍历AST
     */
    public static void recur(IASTNode iastNode){

        //输出节点信息
        //doIASTNode(iastNode, IASTNodeRecursive::printIASTNode);

        //获取方法中入参、变量信息
        doIASTNode(iastNode, FileVariableUtil::methodParams);

        //获取方法中函数调用信息
        doIASTNode(iastNode, MethodCallIBinding::funcationCall);

        //递归遍历IASTNode的子节点
        Arrays.stream(iastNode.getChildren()).forEach(IASTNodeRecursive::recur);

    }

    private static void printIASTNode(IASTNode iastNode){
        System.out.println(iastNode.getFileLocation().getStartingLineNumber() + "\n" + iastNode.getRawSignature());
    }

    private static void doIASTNode(IASTNode iastNode, Consumer<IASTNode> consumer){
        consumer.accept(iastNode);
    }

}
