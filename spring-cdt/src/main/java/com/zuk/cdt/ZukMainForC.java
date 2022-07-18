package com.zuk.cdt;

import com.zuk.cdt.file.FileFrame;
import com.zuk.cdt.file.function.CFileFunctionUtil;
import com.zuk.cdt.file.function.FileFunctionDto;
import com.zuk.cdt.file.function.call.CFunctionCallUtil;
import com.zuk.cdt.file.function.call.FunctionCallDto;
import com.zuk.cdt.file.function.var.CFunctionVariableUtil;
import com.zuk.cdt.file.function.var.FileFunctionVariableVo;
import com.zuk.cdt.report.File2CallsReport;
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
        FILE_SET.stream().forEach(file -> {
            analyzeFile(file);
        });

        new File2CallsReport().report(CPP_FILE_FRAME_MAP);

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

                recur(fun);

                List<FunctionCallDto> cFunctionCallDtoList = CFunctionCallUtil.getFunctionCall();

                FileFunctionVariableVo fileFunctionVariableVo = CFunctionVariableUtil.getFileFunctionVariableVo(IAST_NAME_WITH_IBINDING_SET);
                IAST_NAME_WITH_IBINDING_SET.clear();


                FileFrame.CxxFuntion cppFuntion = new FileFrame.CxxFuntion();
                cppFuntion.setFileFunctionDto(fileFunctionDto);
                cppFuntion.setFunctionCallDtos(cFunctionCallDtoList);
                cppFuntion.setFileFunctionVariableVo(fileFunctionVariableVo);

                //
                cppFileFrame.addCppFuntion(cppFuntion);

                System.out.println();

            });

            CPP_FILE_FRAME_MAP.put(filepath, Optional.of(cppFileFrame));

            return cppFileFrame;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    private static Set<IASTName> IAST_NAME_WITH_IBINDING_SET = new HashSet<>();
    /***
     * 遍历AST
     */
    public static void recur(IASTNode iastNode){

        //输出节点信息
        doIASTNode(iastNode, ZukMainForC::printIASTNode);

        //收集当前函数中的函数调用
        doIASTNode(iastNode, CFunctionCallUtil::funcationCall);

        //
        CFunctionVariableUtil cFunctionVariableUtil = new CFunctionVariableUtil();
        doIASTNode(iastNode, cFunctionVariableUtil::methodParams);
        Set<IASTName> iastNameSet = cFunctionVariableUtil.getIAST_NAME_WITH_IBINDING_SET();
        IAST_NAME_WITH_IBINDING_SET.addAll(iastNameSet);
        //

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
        //System.out.println("行号：" + iastNode.getFileLocation().getStartingLineNumber() + ",\n内容：" + iastNode.getRawSignature() + ",\nclass:" + iastNode.getClass().getName());
    }

    private static void doIASTNode(IASTNode iastNode, Consumer<IASTNode> consumer){
        consumer.accept(iastNode);
    }



}
