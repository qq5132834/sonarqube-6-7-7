package com.zuk.cdt.file;

import com.zuk.cdt.file.function.FileFunctionDto;
import com.zuk.cdt.file.function.call.FunctionCallDto;
import com.zuk.cdt.file.function.var.FileFunctionVariableVo;

import java.util.ArrayList;
import java.util.List;

public class FileFrame {

    private final String filePath;
    private List<CxxFuntion> cppFuntionList = new ArrayList<>();

    public FileFrame(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public static FileFrame getInstance(String filePath) {
        return new FileFrame(filePath);
    }

    public FileFrame addCppFuntion(CxxFuntion cppFuntion) {
        this.cppFuntionList.add(cppFuntion);
        return this;
    }

    public List<CxxFuntion> getCppFuntionList() {
        return this.cppFuntionList;
    }

    public static class CxxFuntion {
        //函数基本信息
        private FileFunctionDto fileFunctionDto;
        //函数中变量信息
        private FileFunctionVariableVo fileFunctionVariableVo;
        //函数调用外部集
        private List<FunctionCallDto> functionCallDtos;

        public FileFunctionDto getFileFunctionDto() {
            return fileFunctionDto;
        }

        public FileFunctionVariableVo getFileFunctionVariableVo() {
            return fileFunctionVariableVo;
        }

        public List<FunctionCallDto> getFunctionCallDtos() { return functionCallDtos; }

        public CxxFuntion setFileFunctionDto(FileFunctionDto fileFunctionDto) {
            this.fileFunctionDto = fileFunctionDto;
            return this;
        }

        public CxxFuntion setFileFunctionVariableVo(FileFunctionVariableVo fileFunctionVariableVo) {
            this.fileFunctionVariableVo = fileFunctionVariableVo;
            return this;
        }

        public CxxFuntion setFunctionCallDtos(List<FunctionCallDto> functionCallDtos) {
            this.functionCallDtos = functionCallDtos;
            return this;
        }
    }

}