package com.zuk.cdt.file;

import com.zuk.cdt.binding.dto.DeclareVariableDto;
import com.zuk.cdt.file.function.FileFunctionDto;
import com.zuk.cdt.file.function.var.FileFunctionVariableVo;

import java.util.ArrayList;
import java.util.List;

public class CppFileFrame {

    private List<CppFuntion> cppFuntionList = new ArrayList<>();

    public static CppFileFrame getInstance() {
        return new CppFileFrame();
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
        private List<DeclareVariableDto> declareVariableDtos;

        public FileFunctionDto getFileFunctionDto() {
            return fileFunctionDto;
        }

        public FileFunctionVariableVo getFileFunctionVariableVo() {
            return fileFunctionVariableVo;
        }

        public List<DeclareVariableDto> getDeclareVariableDtos() { return declareVariableDtos; }

        public CppFuntion setFileFunctionDto(FileFunctionDto fileFunctionDto) {
            this.fileFunctionDto = fileFunctionDto;
            return this;
        }

        public CppFuntion setFileFunctionVariableVo(FileFunctionVariableVo fileFunctionVariableVo) {
            this.fileFunctionVariableVo = fileFunctionVariableVo;
            return this;
        }

        public CppFuntion setDeclareVariableDtos(List<DeclareVariableDto> declareVariableDtos) {
            this.declareVariableDtos = declareVariableDtos;
            return this;
        }
    }

}