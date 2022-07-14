package com.zuk.cdt;

import com.zuk.cdt.binding.MethodCallIBinding;
import com.zuk.cdt.file.CppFileFrame;
import com.zuk.cdt.file.function.FileFunctionDto;
import com.zuk.cdt.file.var.FileFunctionVariableVo;
import com.zuk.cdt.file.var.FunctionVariableUtil;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ZukMainForFile {


    public static void main(String[] args) throws Exception {
        String file = "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring-cdt\\src\\main\\resources\\c\\src\\DnsCache.cc";
        IASTTranslationUnit iastTranslationUnit = CDTParser.parse(file, CDTParser.Language.CPP);

        CppFileFrame cppFileFrame = CppFileFrame.getInstance();
        //文件函数输出
        List<IASTFunctionDefinition> functionDefinitions = FuntionDefinitionUtil.getFuncationDefinistion(iastTranslationUnit, file);
        functionDefinitions.stream().forEach(e->{

            FileFunctionDto fileFunctionDto = FileFunctionDto.builder()
                                                    .setFunctionName(e.getDeclarator().getName().toString())
                                                    .setStartLineNumber(e.getFileLocation().getStartingLineNumber())
                                                    .setIastFileLocation(e.getFileLocation())
                                                    .build();

            //处理当前函数节点，从中提取变量集合（入参、本地变量、全局变量、类变量等）
            ZukMainForFile.recur(e);

            //获取函数中变量集
            FileFunctionVariableVo fileFunctionVariableVo = FunctionVariableUtil.getFileFunctionVariableVo();

            CppFileFrame.CppFuntion cppFuntion = new CppFileFrame.CppFuntion();
            cppFuntion.setFileFunctionDto(fileFunctionDto);
            cppFuntion.setFileFunctionVariableVo(fileFunctionVariableVo);

            //
            cppFileFrame.addCppFuntion(cppFuntion);

        });

        System.out.println("");
    }


    /***
     * 遍历AST
     */
    public static void recur(IASTNode iastNode){

        //输出节点信息
        //doIASTNode(iastNode, IASTNodeRecursive::printIASTNode);

        //获取方法中入参、变量信息
        doIASTNode(iastNode, FunctionVariableUtil::methodParams);

        //获取方法中函数调用信息
        doIASTNode(iastNode, MethodCallIBinding::funcationCall);

        //递归遍历IASTNode的子节点
        Arrays.stream(iastNode.getChildren()).forEach(ZukMainForFile::recur);

    }

    private static void printIASTNode(IASTNode iastNode){
        System.out.println(iastNode.getFileLocation().getStartingLineNumber() + "\n" + iastNode.getRawSignature());
    }

    private static void doIASTNode(IASTNode iastNode, Consumer<IASTNode> consumer){
        consumer.accept(iastNode);
    }

}
