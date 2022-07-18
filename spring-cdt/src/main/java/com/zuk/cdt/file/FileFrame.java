package com.zuk.cdt.file;

import com.zuk.cdt.file.function.FileFunctionDto;
import com.zuk.cdt.file.function.call.FunctionCallDto;
import com.zuk.cdt.file.function.var.FunctionVariableVo;

import java.util.ArrayList;
import java.util.List;

public class FileFrame {

    private final String filePath;
    private List<Funtion> funtionList = new ArrayList<>();

    public FileFrame(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public static FileFrame getInstance(String filePath) {
        return new FileFrame(filePath);
    }

    public FileFrame addFuntion(Funtion funtion) {
        this.funtionList.add(funtion);
        return this;
    }

    public List<Funtion> getFuntionList() {
        return this.funtionList;
    }

    public static class Funtion {
        //函数基本信息
        private FileFunctionDto fileFunctionDto;
        //函数中变量信息
        private FunctionVariableVo fileFunctionVariableVo;
        //函数调用外部集
        private List<FunctionCallDto> functionCallDtos;

        public FileFunctionDto getFileFunctionDto() {
            return fileFunctionDto;
        }

        public FunctionVariableVo getFileFunctionVariableVo() {
            return fileFunctionVariableVo;
        }

        public List<FunctionCallDto> getFunctionCallDtos() { return functionCallDtos; }

        public Funtion setFileFunctionDto(FileFunctionDto fileFunctionDto) {
            this.fileFunctionDto = fileFunctionDto;
            return this;
        }

        public Funtion setFileFunctionVariableVo(FunctionVariableVo fileFunctionVariableVo) {
            this.fileFunctionVariableVo = fileFunctionVariableVo;
            return this;
        }

        public Funtion setFunctionCallDtos(List<FunctionCallDto> functionCallDtos) {
            this.functionCallDtos = functionCallDtos;
            return this;
        }
    }

}