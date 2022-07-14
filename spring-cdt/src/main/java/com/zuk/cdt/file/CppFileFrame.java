package com.zuk.cdt.file;

import com.zuk.cdt.file.var.FileVariableUtil;

public class CppFileFrame {

    /***
     * 文件中类变量和全局变量集合
     */
    private FileVariableUtil.FileFunctionCallVariable fileFunctionCallVariable;

    public void setFileFunctionCallVariable(FileVariableUtil.FileFunctionCallVariable fileFunctionCallVariable) {
        this.fileFunctionCallVariable = fileFunctionCallVariable;
    }



}