package com.zuk.cdt.file;

import com.zuk.cdt.file.function.FileFunctionDto;
import com.zuk.cdt.file.function.call.CxxFunctionCallDto;
import com.zuk.cdt.file.function.var.CxxFileFunctionVariableVo;

import java.util.ArrayList;
import java.util.List;

public class CppFileFrame {

    private final String filePath;
    private List<CppFuntion> cppFuntionList = new ArrayList<>();

    public CppFileFrame(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public static CppFileFrame getInstance(String filePath) {
        return new CppFileFrame(filePath);
    }

    public CppFileFrame addCppFuntion(CppFuntion cppFuntion) {
        this.cppFuntionList.add(cppFuntion);
        return this;
    }

    public List<CppFuntion> getCppFuntionList() {
        return this.cppFuntionList;
    }

    public static class CppFuntion {
        //函数基本信息
        private FileFunctionDto fileFunctionDto;
        //函数中变量信息
        private CxxFileFunctionVariableVo fileFunctionVariableVo;
        //函数调用外部集
        private List<CxxFunctionCallDto> functionCallDtos;

        public FileFunctionDto getFileFunctionDto() {
            return fileFunctionDto;
        }

        public CxxFileFunctionVariableVo getFileFunctionVariableVo() {
            return fileFunctionVariableVo;
        }

        public List<CxxFunctionCallDto> getFunctionCallDtos() { return functionCallDtos; }

        public CppFuntion setFileFunctionDto(FileFunctionDto fileFunctionDto) {
            this.fileFunctionDto = fileFunctionDto;
            return this;
        }

        public CppFuntion setFileFunctionVariableVo(CxxFileFunctionVariableVo fileFunctionVariableVo) {
            this.fileFunctionVariableVo = fileFunctionVariableVo;
            return this;
        }

        public CppFuntion setFunctionCallDtos(List<CxxFunctionCallDto> functionCallDtos) {
            this.functionCallDtos = functionCallDtos;
            return this;
        }
    }

}