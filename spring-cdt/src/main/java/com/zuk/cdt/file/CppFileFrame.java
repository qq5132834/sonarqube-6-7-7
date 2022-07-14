package com.zuk.cdt.file;

import com.zuk.cdt.file.function.FileFunctionDto;
import com.zuk.cdt.file.var.FunctionVariableDto;
import com.zuk.cdt.file.var.FunctionVariableUtil;

public class CppFileFrame {

    /***
     * 文件中类变量和全局变量集合
     */
    private FunctionVariableUtil.FileFunctionCallVariable fileFunctionCallVariable;

    public void setFileFunctionCallVariable(FunctionVariableUtil.FileFunctionCallVariable fileFunctionCallVariable) {
        this.fileFunctionCallVariable = fileFunctionCallVariable;
    }


    public static class CppFuntion {

        private FileFunctionDto fileFunctionDto;
        private FunctionVariableDto functionVariableDto;

        public FileFunctionDto getFileFunctionDto() {
            return fileFunctionDto;
        }

        public void setFileFunctionDto(FileFunctionDto fileFunctionDto) {
            this.fileFunctionDto = fileFunctionDto;
        }

        public FunctionVariableDto getFunctionVariableDto() {
            return functionVariableDto;
        }

        public void setFunctionVariableDto(FunctionVariableDto functionVariableDto) {
            this.functionVariableDto = functionVariableDto;
        }
    }

}