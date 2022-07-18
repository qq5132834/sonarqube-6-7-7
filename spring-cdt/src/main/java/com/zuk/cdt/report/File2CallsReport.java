package com.zuk.cdt.report;

import com.zuk.cdt.file.FileFrame;
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

public class File2CallsReport implements DoReport {

    private static String FILE_NAME = "File2Calls.xml";
    private static String PATH = "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring-cdt\\src\\test\\resources\\results\\";

    @Override
    public void report(Map<String, Optional<FileFrame>> cppFileFrameSet) {
        Document doc = DocumentHelper.createDocument();
        doc.addComment("函数调用图");
        Element ubiSec = doc.addElement("UbiSec");
        Element func2Calls = ubiSec.addElement("Func2Calls");

        cppFileFrameSet.values().stream().forEach(optionalCppFileFrame -> {
            if(!optionalCppFileFrame.isPresent()){
                return;
            }
            FileFrame cppFileFrame = optionalCppFileFrame.get();
            String filePath = cppFileFrame.getFilePath();

            //文件函数节点
            cppFileFrame.getFuntionList().stream().forEach(cppFuntion -> {
                String functionName = cppFuntion.getFileFunctionDto().getBuilder().getFunctionName();
                int line = cppFuntion.getFileFunctionDto().getBuilder().getStartLineNumber();

                Element funcInfo = func2Calls.addElement("FuncInfo");
                funcInfo.addAttribute("path", filePath);
                funcInfo.addAttribute("name", functionName);
                funcInfo.addAttribute("line", String.valueOf(line));

                cppFuntion.getFunctionCallDtos().stream().forEach(call -> {
                    Element callFunction = funcInfo.addElement("CallFuncInfo");

                    if (call.getBuilder().geteScopeKind() == null) {
                        callFunction.addAttribute("path", filePath);
                        callFunction.addAttribute("name", call.getBuilder().getCallFunctionName());
//                        callFunction.addAttribute("line", String.valueOf(call.getBuilder().getStartLineNumber()));
                    }
                    else {
                        callFunction.addAttribute("path", call.getBuilder().getFileName());
                        callFunction.addAttribute("name", call.getBuilder().getCallFunctionName());
                        callFunction.addAttribute("line", String.valueOf(call.getBuilder().getStartLineNumber()));
                    }
                });
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
            System.out.println("生成函数调用关系图成功");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
