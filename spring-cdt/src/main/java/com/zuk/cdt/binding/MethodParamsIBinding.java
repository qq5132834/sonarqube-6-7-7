package com.zuk.cdt.binding;

import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPScope;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariable;

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
            Class cls = iBinding.getClass();
            CLASS_SET.add(cls);
            checkBinding(iBinding);
        }
    }

    private static void checkBinding(IBinding iBinding){
        if(iBinding instanceof ProblemBinding
                || iBinding instanceof CPPScope.CPPScopeProblem){
            //可以通过解析在AST中找到的名称（调用IASTName.resolveBinding()）并查看结果绑定是否为IProblemBinding来检查许多类别的错误
            //了解CDT的ProblemBindingChecker如何在编辑器中显示许多错误。
            //请注意，这不会捕获所有错误;您可以查看CDT的other checkers，了解如何捕获其他类别的错误（某些检查程序还会生成警告）。
            ProblemBinding problemBinding = (ProblemBinding) iBinding;
            System.out.println(problemBinding.getMessage());
        }
        if(iBinding instanceof CPPVariable){
            CPPVariable cppVariable = (CPPVariable) iBinding;
            cppVariable.getDefinition();
            IValue iValue = cppVariable.getInitialValue();
            IASTNode iastNodeDefinition = cppVariable.getDefinition();
            String name = cppVariable.getName();
            IBinding iBindingOwner = cppVariable.getOwner();
            IScope iScope = cppVariable.getScope();
            IType iType = cppVariable.getType();
            System.out.println();
        }
        if(iBinding instanceof CPPParameter){
            //入参
            CPPParameter cppParameter = (CPPParameter) iBinding;
            int paramPosition = cppParameter.getParameterPosition();
            IASTNode iastNode = cppParameter.getPhysicalNode();
            IASTNode iastNodeDefinition = cppParameter.getDefinition();
            IValue iValue = cppParameter.getInitialValue();
            ILinkage iLinkage = cppParameter.getLinkage();
            IBinding iBindingOwner = cppParameter.getOwner();
            IType iType = cppParameter.getType();
            IScope iScope = cppParameter.getScope();
            System.out.println();
        }
    }


}
