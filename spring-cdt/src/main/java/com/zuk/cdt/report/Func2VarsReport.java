package com.zuk.cdt.report;

import com.zuk.cdt.file.FileFrame;
import com.zuk.cdt.file.function.var.FunctionVariableDto;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Func2VarsReport implements DoReport {

    private static String FILE_NAME = "Func2Vars.xml";
    private static String PATH = "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring-cdt\\src\\test\\resources\\results\\";

    @Override
    public void report(Map<String, Optional<FileFrame>> cppFileFrameSet) {
        Document doc = DocumentHelper.createDocument();
        doc.addComment("函数变量图");
        Element ubiSec = doc.addElement("UbiSec");
        Element func2Vars = ubiSec.addElement("Func2Vars");

        cppFileFrameSet.values().stream().forEach(optionalCppFileFrame -> {
            if(!optionalCppFileFrame.isPresent()){
                return;
            }
            FileFrame cppFileFrame = optionalCppFileFrame.get();
            String filePath = cppFileFrame.getFilePath();

            //文件函数节点
            cppFileFrame.getCppFuntionList().stream().forEach(cppFuntion -> {
                String functionName = cppFuntion.getFileFunctionDto().getBuilder().getFunctionName();
                int line = cppFuntion.getFileFunctionDto().getBuilder().getStartLineNumber();

                Element funcInfo = func2Vars.addElement("FuncInfo");
                funcInfo.addAttribute("path", filePath);
                funcInfo.addAttribute("name", functionName);
                funcInfo.addAttribute("line", String.valueOf(line));

                Set<FunctionVariableDto> classVarSet = cppFuntion.getFileFunctionVariableVo().getClassVariableSet();
                Set<FunctionVariableDto> globalVarSet = cppFuntion.getFileFunctionVariableVo().getGlobalVariableSet();
                Set<FunctionVariableDto> localVarSet = cppFuntion.getFileFunctionVariableVo().getLocalVariableSet();

                classVarSet.stream().forEach(e->{
                    Element element = funcInfo.addElement("VariableInfo");
                    element.addAttribute("name", e.getBuilder().getRawSignature());
                    if (e.getBuilder().getIastFileLocation() != null) {
                        element.addAttribute("path", e.getBuilder().getIastFileLocation().getFileName());
                        element.addAttribute("line", String.valueOf(e.getBuilder().getIastFileLocation().getStartingLineNumber()));
                    }
                });

//                globalVarSet.stream().forEach(e->{
//                    Element element = funcInfo.addElement("VariableInfo");
//                    element.addAttribute("name", e.getBuilder().getRawSignature());
//                });

//                localVarSet.stream().forEach(e->{
//                    Element element = funcInfo.addElement("VariableInfo");
//                    element.addAttribute("name", e.getBuilder().getRawSignature());
//                });

            });
        });

        //
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        Writer out;
        try {
            out = new FileWriter(PATH + File.separator + FILE_NAME);
            XMLWriter writer = new XMLWriter(out, format);
            writer.write(doc);
            writer.close();
            System.out.println("生成函数变量图成功");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
