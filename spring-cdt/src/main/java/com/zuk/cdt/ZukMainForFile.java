package com.zuk.cdt;

import com.zuk.cdt.file.function.FileFunctionUtil;
import com.zuk.cdt.file.function.call.CxxFunctionCallUtil;
import com.zuk.cdt.file.function.call.CxxFunctionCallDto;
import com.zuk.cdt.file.CppFileFrame;
import com.zuk.cdt.file.function.FileFunctionDto;
import com.zuk.cdt.file.function.var.CxxFileFunctionVariableVo;
import com.zuk.cdt.file.function.var.CxxFunctionVariableUtil;
import com.zuk.cdt.report.File2CallsReport;
import com.zuk.cdt.report.File2FuncsReport;
import com.zuk.cdt.report.Func2VarsReport;
import org.eclipse.cdt.core.dom.ast.EScopeKind;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ZukMainForFile {

    private static Map<String, Optional<CppFileFrame>> CPP_FILE_FRAME_MAP = new ConcurrentHashMap<>();

    private static Set<String> SUFFIX_SET = new HashSet<>();
    static {
        SUFFIX_SET.add(".cc");
//        SUFFIX_SET.add(".cpp");
//        SUFFIX_SET.add(".h");
    }
    private static Set<String> FILE_SET = new HashSet<>();

    public static void recus(File file) {

        if ( file.isFile() && SUFFIX_SET.stream().filter(e->{
            if (file.getName().endsWith(e)) {
                return true;
            }
            return false;
        }).count() > 0 ) {
            try {
                SUFFIX_SET.add(file.getCanonicalPath());
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(ZukMainForFile::recus);
        }
    }

    public static void main(String[] args) {
        recus(new File("D:\\development\\java\\eclipse-cpp-indigo-SR2-incubation-win32-x86_64\\sogou_workflow\\workflow"));
        SUFFIX_SET.stream().forEach(file -> analyzeFile(file));

        new File2FuncsReport().report(CPP_FILE_FRAME_MAP);
        new File2CallsReport().report(CPP_FILE_FRAME_MAP);
        new Func2VarsReport().report(CPP_FILE_FRAME_MAP);

        System.out.println();
    }

    /***
     * 单个文件追踪
     * @param args
     * @throws Exception
     */
    public static void main1(String[] args) throws Exception {

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
                    else {
                        System.out.println("调用函数:" + callFunctionName);
                    }
                });
            });
        }
    }

    public static void recuCallFunction(String filePath, String callFunctionName){
        System.out.println("调用函数:" + callFunctionName);
        CppFileFrame cppFileFrame = null;
        if (CPP_FILE_FRAME_MAP.keySet().contains(filePath)) {
            cppFileFrame = CPP_FILE_FRAME_MAP.get(filePath).get();
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
                    else {
                        System.out.println("调用函数:" + callFunctionName1);
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
                CxxFileFunctionVariableVo fileFunctionVariableVo = CxxFunctionVariableUtil.getFileFunctionVariableVo();

                //方法内部调用外部函数集
                List<CxxFunctionCallDto> declareVariableDtos = CxxFunctionCallUtil.getFunctionCall();

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

            CPP_FILE_FRAME_MAP.put(filepath, Optional.of(cppFileFrame));

            return cppFileFrame;
        }catch (Exception e) {
            e.printStackTrace();
        }
        CPP_FILE_FRAME_MAP.put(filepath, Optional.empty());
        return null;
    }

    /***
     * 遍历AST
     */
    public static void recur(IASTNode iastNode){

        //输出节点信息
        //doIASTNode(iastNode, IASTNodeRecursive::printIASTNode);

        //获取方法中入参、变量信息
        doIASTNode(iastNode, CxxFunctionVariableUtil::methodParams);

        //获取方法中函数调用信息
        doIASTNode(iastNode, CxxFunctionCallUtil::funcationCall);

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
