package com.zuk.cdt.file.var;

import org.eclipse.cdt.core.dom.ast.EScopeKind;
import org.eclipse.cdt.core.dom.ast.IScope;

/***
 * 全局变量、类定义变量
 */
public class FileVariableDto {
    private EScopeKind eScopeKind; //作用域
    private String varName; //变量名称
    private IScope iScope; //作用域
}