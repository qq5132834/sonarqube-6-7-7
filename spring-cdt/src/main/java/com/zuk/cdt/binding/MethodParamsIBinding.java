package com.zuk.cdt.binding;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;

import java.util.HashSet;
import java.util.Set;

/***
 * 遍历函数的入参、变量、和作用域
 */
public class MethodParamsIBinding {

    public static Set<Class> CLASS_SET = new HashSet<>();

    public static void methodParams(IASTNode iastNode){
        if(iastNode instanceof IASTName){
            IASTName iastName = (IASTName) iastNode;
            IBinding iBinding = iastName.resolveBinding();
            CLASS_SET.add(iBinding.getClass());
        }
    }

}
