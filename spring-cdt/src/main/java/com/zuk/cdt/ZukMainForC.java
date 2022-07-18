package com.zuk.cdt;

import com.zuk.cdt.file.FileFrame;
import com.zuk.cdt.file.function.CFileFunctionUtil;
import com.zuk.cdt.file.function.FileFunctionDto;
import com.zuk.cdt.file.function.call.CFunctionCallUtil;
import com.zuk.cdt.file.function.call.FunctionCallDto;
import com.zuk.cdt.file.function.var.CFunctionVariableUtil;
import com.zuk.cdt.file.function.var.FunctionVariableVo;
import com.zuk.cdt.report.File2CallsReport;
import com.zuk.cdt.report.File2FuncsReport;
import com.zuk.cdt.report.Func2VarsReport;
import org.eclipse.cdt.core.dom.ast.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ZukMainForC {

    private static Map<String, Optional<FileFrame>> CPP_FILE_FRAME_MAP = new ConcurrentHashMap<>();

    private static Set<String> FILE_SET = new HashSet<>();
    private static Set<String> SUFFIX_SET = new HashSet<>();
    static {
        SUFFIX_SET.add(".c");
//        SUFFIX_SET.add(".cpp");
//        SUFFIX_SET.add(".h");
    }

    public static void recus(File file) {

        if ( file.isFile() && SUFFIX_SET.stream().filter(e->{
            if (file.getName().endsWith(e)) {
                return true;
            }
            return false;
        }).count() > 0 ) {
            try {
                FILE_SET.add(file.getCanonicalPath());
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(ZukMainForC::recus);
        }
    }

    public static void main(String[] args) throws Exception {

        //
//        String filepath = "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring-cdt\\src\\main\\resources\\c\\src\\callback.c";
//        analyzeFile(filepath);

        //
        String fileDir = "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring-cdt\\src\\main\\resources\\c\\src";
        recus(new File(fileDir));
        FILE_SET.parallelStream().forEach(file -> {
            analyzeFile(file);
        });

        new File2CallsReport().report(CPP_FILE_FRAME_MAP);
        new File2FuncsReport().report(CPP_FILE_FRAME_MAP);
        new Func2VarsReport().report(CPP_FILE_FRAME_MAP);

    }


    public static FileFrame analyzeFile(String filepath) {
        try {
            IASTTranslationUnit iastTranslationUnit = CDTParser.parse(filepath, CDTParser.Language.C);

            final FileFrame cppFileFrame = FileFrame.getInstance(filepath);

            List<IASTFunctionDefinition>  iastFunctionDefinitionList = CFileFunctionUtil.getFuncationDefinistion(iastTranslationUnit);
            iastFunctionDefinitionList.stream().forEach(fun -> {

                FileFunctionDto fileFunctionDto = FileFunctionDto.builder()
                        .setFunctionName(fun.getDeclarator().getName().toString())
                        .setStartLineNumber(fun.getFileLocation().getStartingLineNumber())
                        .setEndLineNumber(fun.getFileLocation().getEndingLineNumber())
                        .setIastFileLocation(fun.getFileLocation())
                        .build();

                System.out.println(fun.getRawSignature());

                IASTFunctionDeclarator iastFunctionDeclarator = fun.getDeclarator();
                IASTName iastName = iastFunctionDeclarator.getName();
                //C语言函数名称输出
                System.out.println("函数名称：" + iastName.getRawSignature());


                CFunctionVariableUtil cFunctionVariableUtil = new CFunctionVariableUtil();
                CFunctionCallUtil cFunctionCallUtil = new CFunctionCallUtil();

                List<Consumer<IASTNode>> consumerList = new ArrayList<>();
                consumerList.add(ZukMainForC::printIASTNode);
                consumerList.add(cFunctionCallUtil::funcationCall);
                consumerList.add(cFunctionVariableUtil::methodParams);
                recur(fun, consumerList);

                List<FunctionCallDto> cFunctionCallDtoList = cFunctionCallUtil.getFunctionCall();
                FunctionVariableVo functionVariableVo = cFunctionVariableUtil.getFunctionVariableVo();

                FileFrame.Funtion cppFuntion = new FileFrame.Funtion();
                cppFuntion.setFileFunctionDto(fileFunctionDto);
                cppFuntion.setFunctionCallDtos(cFunctionCallDtoList);
                cppFuntion.setFileFunctionVariableVo(functionVariableVo);

                //
                cppFileFrame.addFuntion(cppFuntion);

                System.out.println();

            });

            CPP_FILE_FRAME_MAP.put(filepath, Optional.of(cppFileFrame));

            return cppFileFrame;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /***
     * 遍历AST
     */
    public static void recur(IASTNode iastNode, List<Consumer<IASTNode>> consumerList){
        consumerList.forEach(e-> e.accept(iastNode));
        //递归遍历IASTNode的子节点
        if (iastNode.getChildren() != null) {
            Arrays.stream(iastNode.getChildren()).forEach(e -> {
                recur(e, consumerList);
            });
        }
    }

    private static void printIASTNode(IASTNode iastNode){
        //System.out.println("行号：" + iastNode.getFileLocation().getStartingLineNumber() + ",\n内容：" + iastNode.getRawSignature() + ",\nclass:" + iastNode.getClass().getName());
    }

    private static void doIASTNode(IASTNode iastNode, Consumer<IASTNode> consumer){
        consumer.accept(iastNode);
    }



}
