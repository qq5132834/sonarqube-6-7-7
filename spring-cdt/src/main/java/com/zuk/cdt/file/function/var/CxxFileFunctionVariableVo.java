package com.zuk.cdt.file.function.var;

import java.util.Set;

public class CxxFileFunctionVariableVo {

    //类变量
    private final Set<CxxFunctionVariableDto> classVariableSet;
    //全局变量
    private final Set<CxxFunctionVariableDto> globalVariableSet;
    //本地变量
    private final Set<CxxFunctionVariableDto> localVariableSet;

    public CxxFileFunctionVariableVo(Set<CxxFunctionVariableDto> classVariableSet,
                                     Set<CxxFunctionVariableDto> globalVariableSet,
                                     Set<CxxFunctionVariableDto> localVariableSet) {
        this.classVariableSet = classVariableSet;
        this.globalVariableSet = globalVariableSet;
        this.localVariableSet = localVariableSet;
    }
    public Set<CxxFunctionVariableDto> getClassVariableSet() { return classVariableSet; }
    public Set<CxxFunctionVariableDto> getGlobalVariableSet() {
        return globalVariableSet;
    }
    public Set<CxxFunctionVariableDto> getLocalVariableSet() { return localVariableSet; }
}
