package com.zuk.cdt;

import com.zuk.cdt.file.function.FileFunctionUtil;
import com.zuk.cdt.file.function.call.FunctionCallUtil;
import com.zuk.cdt.file.function.call.FunctionCallDto;
import com.zuk.cdt.file.CppFileFrame;
import com.zuk.cdt.file.function.FileFunctionDto;
import com.zuk.cdt.file.function.var.FileFunctionVariableVo;
import com.zuk.cdt.file.function.var.FunctionVariableUtil;
import org.eclipse.cdt.core.dom.ast.EScopeKind;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ZukMainForFile {

    private static Map<String, CppFileFrame> cppFileFrameMap = new HashMap<>();

    public static void main(String[] args) throws Exception {


        String file = "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring-cdt\\src\\main\\resources\\c\\src\\DnsCache.cc";
        CppFileFrame cppFileFrame = analyzeFile(file);
        if(cppFileFrame != null){
            cppFileFrame.getCppFuntionList().stream().forEach(cppFuntion -> {
                System.out.println("起始函数:" + cppFuntion.getFileFunctionDto().getBuilder().getFunctionName());
                cppFuntion.getFunctionCallDtos().stream().forEach(functionCallDto -> {
                    String callFunctionName = functionCallDto.getBuilder().getCallFunctionName();
                    EScopeKind eScopeKind = functionCallDto.getBuilder().geteScopeKind();
                    if (eScopeKind != null) {
                        String filePath = functionCallDto.getBuilder().getFileName();
                        recuCallFunction(filePath, callFunctionName);
                    }
                });
            });
        }
    }

    public static void recuCallFunction(String filePath, String callFunctionName){
        System.out.println("调用函数:" + callFunctionName);
        CppFileFrame cppFileFrame = null;
        if (cppFileFrameMap.keySet().contains(filePath)) {
            cppFileFrame = cppFileFrameMap.get(filePath);
        }
        else {
            cppFileFrame = analyzeFile(filePath);
        }
        if (cppFileFrame != null) {
            List<CppFileFrame.CppFuntion> list = cppFileFrame.getCppFuntionList().stream().filter(e->{
                if(e.getFileFunctionDto().getBuilder().getFunctionName().endsWith(callFunctionName)){
                    return true;
                }
                return false;
            }).collect(Collectors.toList());

            if(list != null && list.size() > 0){
                CppFileFrame.CppFuntion cppFuntion = list.get(0);
                cppFuntion.getFunctionCallDtos().stream().forEach(functionCallDto -> {
                    String callFunctionName1 = functionCallDto.getBuilder().getCallFunctionName();
                    EScopeKind eScopeKind = functionCallDto.getBuilder().geteScopeKind();
                    if (eScopeKind != null) {
                        String filePath1 = functionCallDto.getBuilder().getFileName();
                        recuCallFunction(filePath1, callFunctionName1);
                    }
                });
            }
        }
    }

    public static CppFileFrame analyzeFile(String filepath) {

        try {
            IASTTranslationUnit iastTranslationUnit = CDTParser.parse(filepath, CDTParser.Language.CPP);

            final CppFileFrame cppFileFrame = CppFileFrame.getInstance(filepath);
            //文件函数输出
            List<IASTFunctionDefinition> functionDefinitions = FileFunctionUtil.getFuncationDefinistion(iastTranslationUnit, filepath);
            functionDefinitions.stream().forEach(e->{

                FileFunctionDto fileFunctionDto = FileFunctionDto.builder()
                        .setFunctionName(e.getDeclarator().getName().toString())
                        .setStartLineNumber(e.getFileLocation().getStartingLineNumber())
                        .setEndLineNumber(e.getFileLocation().getEndingLineNumber())
                        .setIastFileLocation(e.getFileLocation())
                        .build();

                //处理当前函数节点，从中提取变量集合（入参、本地变量、全局变量、类变量等）
                ZukMainForFile.recur(e);

                //获取函数中变量集
                FileFunctionVariableVo fileFunctionVariableVo = FunctionVariableUtil.getFileFunctionVariableVo();

                //方法内部调用外部函数集
                List<FunctionCallDto> declareVariableDtos = FunctionCallUtil.getFunctionCall();

                CppFileFrame.CppFuntion cppFuntion = new CppFileFrame.CppFuntion();
                cppFuntion.setFileFunctionDto(fileFunctionDto);
                cppFuntion.setFileFunctionVariableVo(fileFunctionVariableVo);
                cppFuntion.setFunctionCallDtos(declareVariableDtos);

                //
                cppFileFrame.addCppFuntion(cppFuntion);
                //
                System.out.println("");
            });

            System.out.println("");

            cppFileFrameMap.put(filepath, cppFileFrame);

            return cppFileFrame;
        }catch (Exception e) {
            e.printStackTrace();
        }
        cppFileFrameMap.put(filepath, null);
        return null;
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
        doIASTNode(iastNode, FunctionCallUtil::funcationCall);

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
