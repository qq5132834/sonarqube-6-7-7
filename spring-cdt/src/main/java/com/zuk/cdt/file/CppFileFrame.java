package com.zuk.cdt.file;

import com.zuk.cdt.file.function.FileFunctionDto;
import com.zuk.cdt.file.var.FileFunctionVariableVo;

import java.util.List;

public class CppFileFrame {

    private List<CppFuntion> cppFuntionList;

    public List<CppFuntion> getCppFuntionList() {
        return cppFuntionList;
    }

    public void setCppFuntionList(List<CppFuntion> cppFuntionList) {
        this.cppFuntionList = cppFuntionList;
    }

    public static class CppFuntion {
        //函数基本信息
        private FileFunctionDto fileFunctionDto;
        //函数中变量信息
        private FileFunctionVariableVo functionVariableDto;

        public FileFunctionDto getFileFunctionDto() {
            return fileFunctionDto;
        }

        public FileFunctionVariableVo getFunctionVariableDto() {
            return functionVariableDto;
        }

        public CppFuntion setFileFunctionDto(FileFunctionDto fileFunctionDto) {
            this.fileFunctionDto = fileFunctionDto;
            return this;
        }

        public CppFuntion setFunctionVariableDto(FileFunctionVariableVo functionVariableDto) {
            this.functionVariableDto = functionVariableDto;
            return this;
        }
    }

}