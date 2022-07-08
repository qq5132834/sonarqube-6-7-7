package com.zuk.cdt;

import com.zuk.cdt.binding.MethodParamsIBinding;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class IASTNodeRecursive {

    private static Set<String> IBINDING_SET = new HashSet<>();

    /***
     * 遍历AST
     */
    public static void recur(IASTNode iastNode){
        //输出节点信息
        doIASTNode(iastNode, IASTNodeRecursive::printIASTNode);
        //获取节点IBinding信息
        doIASTNode(iastNode, MethodParamsIBinding::methodParams);
        IASTNode[] iastNodes = iastNode.getChildren();
        if(iastNodes != null && iastNodes.length > 0){
            Arrays.stream(iastNodes).forEach(IASTNodeRecursive::recur);
        }
    }

    private static void printIASTNode(IASTNode iastNode){
        System.out.println(iastNode.getFileLocation().getStartingLineNumber() + "\n" + iastNode.getRawSignature());
    }

    private static void doIASTNode(IASTNode iastNode, Consumer<IASTNode> consumer){
        consumer.accept(iastNode);
    }

}
