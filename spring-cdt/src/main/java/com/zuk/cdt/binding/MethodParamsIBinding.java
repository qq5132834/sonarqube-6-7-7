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
 * 遍历函数的入参、变量、和作用域(用起止行的位置进行描述)
 */
public class MethodParamsIBinding {

    private static Set<Class> CLASS_SET = new HashSet<>();
    private static Set<IBinding> PARAM_VAL_SET = new HashSet<>();
    private static Set<IScope> SCOPE_SET = new HashSet<>();

    public static void methodParams(IASTNode iastNode){
        if(iastNode instanceof IASTName){
            IASTName iastName = (IASTName) iastNode;
            IBinding iBinding = iastName.resolveBinding();

            Class cls = iBinding.getClass();

            CLASS_SET.add(cls);
            boolean isProblemBinding = checkProblemBinding(iBinding);
            if(!isProblemBinding){
                try{
                    IScope iScope = iBinding.getScope();
                    SCOPE_SET.add(iScope);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("iBinding问题:" + iBinding.toString());
            }
        }
    }

    /***
     * 判断是否是problemBinding
     * @param iBinding
     * @return true有问题的ibinding
     */
    private static boolean checkProblemBinding(IBinding iBinding){
        if(iBinding instanceof ProblemBinding
                || iBinding instanceof CPPScope.CPPScopeProblem){
            //可以通过解析在AST中找到的名称（调用IASTName.resolveBinding()）并查看结果绑定是否为IProblemBinding来检查许多类别的错误
            //了解CDT的ProblemBindingChecker如何在编辑器中显示许多错误。
            //请注意，这不会捕获所有错误;您可以查看CDT的other checkers，了解如何捕获其他类别的错误（某些检查程序还会生成警告）。
            ProblemBinding problemBinding = (ProblemBinding) iBinding;
            System.out.println(problemBinding.getMessage());
            return true;
        }
        //定义局部变量
        if(iBinding instanceof CPPVariable){
            PARAM_VAL_SET.add(iBinding);
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
        //方法入参
        if(iBinding instanceof CPPParameter){
            PARAM_VAL_SET.add(iBinding);
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
        return false;
    }

    public static void printResultAndClearSet(){
        //输出IBinding的主要类
        MethodParamsIBinding.CLASS_SET.stream().map(e->e.getName()).forEach(System.out::println);
        //清空集合
        MethodParamsIBinding.CLASS_SET.clear();
        //输出函数中的参数：函数入参，函数中定义的局部变量
        MethodParamsIBinding.PARAM_VAL_SET.stream().forEach(pv->{System.out.println(pv.getName());});
        //清空参数集合
        MethodParamsIBinding.PARAM_VAL_SET.clear();
        //输出作用域
        MethodParamsIBinding.SCOPE_SET.stream().forEach(ss->{System.out.println(ss.getKind().toString() + "," + ss.getScopeName());});
        //清空作用域
        MethodParamsIBinding.SCOPE_SET.clear();
    }

}
