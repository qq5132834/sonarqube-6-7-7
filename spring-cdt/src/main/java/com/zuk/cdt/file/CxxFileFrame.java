package com.zuk.cdt.file;

import com.zuk.cdt.file.function.FileFunctionDto;
import com.zuk.cdt.file.function.call.FunctionCallDto;
import com.zuk.cdt.file.function.var.CxxFileFunctionVariableVo;

import java.util.ArrayList;
import java.util.List;

public class CxxFileFrame {

    private final String filePath;
    private List<CxxFuntion> cppFuntionList = new ArrayList<>();

    public CxxFileFrame(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public static CxxFileFrame getInstance(String filePath) {
        return new CxxFileFrame(filePath);
    }

    public CxxFileFrame addCppFuntion(CxxFuntion cppFuntion) {
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
        private CxxFileFunctionVariableVo fileFunctionVariableVo;
        //函数调用外部集
        private List<FunctionCallDto> functionCallDtos;

        public FileFunctionDto getFileFunctionDto() {
            return fileFunctionDto;
        }

        public CxxFileFunctionVariableVo getFileFunctionVariableVo() {
            return fileFunctionVariableVo;
        }

        public List<FunctionCallDto> getFunctionCallDtos() { return functionCallDtos; }

        public CxxFuntion setFileFunctionDto(FileFunctionDto fileFunctionDto) {
            this.fileFunctionDto = fileFunctionDto;
            return this;
        }

        public CxxFuntion setFileFunctionVariableVo(CxxFileFunctionVariableVo fileFunctionVariableVo) {
            this.fileFunctionVariableVo = fileFunctionVariableVo;
            return this;
        }

        public CxxFuntion setFunctionCallDtos(List<FunctionCallDto> functionCallDtos) {
            this.functionCallDtos = functionCallDtos;
            return this;
        }
    }

}