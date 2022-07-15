package com.zuk.cdt.file;

import com.zuk.cdt.file.function.call.FunctionCallDto;
import com.zuk.cdt.file.function.FileFunctionDto;
import com.zuk.cdt.file.function.var.FileFunctionVariableVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CppFileFrame {

    private final String filePath;
    private List<CppFuntion> cppFuntionList = new ArrayList<>();
    private Map<String, CppFuntion> cppFuntionMap = new HashMap<>();

    public CppFileFrame(String filePath) {
        this.filePath = filePath;
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
        private FileFunctionVariableVo fileFunctionVariableVo;
        //函数调用外部集
        private List<FunctionCallDto> declareVariableDtos;

        public FileFunctionDto getFileFunctionDto() {
            return fileFunctionDto;
        }

        public FileFunctionVariableVo getFileFunctionVariableVo() {
            return fileFunctionVariableVo;
        }

        public List<FunctionCallDto> getDeclareVariableDtos() { return declareVariableDtos; }

        public CppFuntion setFileFunctionDto(FileFunctionDto fileFunctionDto) {
            this.fileFunctionDto = fileFunctionDto;
            return this;
        }

        public CppFuntion setFileFunctionVariableVo(FileFunctionVariableVo fileFunctionVariableVo) {
            this.fileFunctionVariableVo = fileFunctionVariableVo;
            return this;
        }

        public CppFuntion setDeclareVariableDtos(List<FunctionCallDto> declareVariableDtos) {
            this.declareVariableDtos = declareVariableDtos;
            return this;
        }
    }

}