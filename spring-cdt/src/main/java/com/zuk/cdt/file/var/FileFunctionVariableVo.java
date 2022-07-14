package com.zuk.cdt.file.var;

import java.util.Set;

public class FileFunctionVariableVo {

    //类变量
    private final Set<FunctionVariableDto> classVariableSet;
    //全局变量
    private final Set<FunctionVariableDto> globalVariableSet;
    //本地变量
    private final Set<FunctionVariableDto> localVariableSet;

    public FileFunctionVariableVo(Set<FunctionVariableDto> classVariableSet,
                                    Set<FunctionVariableDto> globalVariableSet,
                                    Set<FunctionVariableDto> localVariableSet) {
        this.classVariableSet = classVariableSet;
        this.globalVariableSet = globalVariableSet;
        this.localVariableSet = localVariableSet;
    }
    public Set<FunctionVariableDto> getClassVariableSet() { return classVariableSet; }
    public Set<FunctionVariableDto> getGlobalVariableSet() {
        return globalVariableSet;
    }
    public Set<FunctionVariableDto> getLocalVariableSet() { return localVariableSet; }
}
