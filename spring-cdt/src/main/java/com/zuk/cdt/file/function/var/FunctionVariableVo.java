package com.zuk.cdt.file.function.var;

import java.util.Set;

public class FunctionVariableVo {

    //类变量
    private final Set<FunctionVariableDto> classVariableSet;
    //全局变量
    private final Set<FunctionVariableDto> globalVariableSet;
    //本地变量
    private final Set<FunctionVariableDto> localVariableSet;

    public FunctionVariableVo(Set<FunctionVariableDto> classVariableSet,
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
