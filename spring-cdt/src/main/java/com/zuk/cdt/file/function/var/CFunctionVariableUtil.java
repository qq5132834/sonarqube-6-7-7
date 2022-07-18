package com.zuk.cdt.file.function.var;

import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;
import org.eclipse.cdt.internal.core.dom.parser.c.CParameter;
import org.eclipse.cdt.internal.core.dom.parser.c.CVariable;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPScope;

import java.util.HashSet;
import java.util.Set;

/***
 * C++
 * 遍历函数的入参、变量、和作用域(用起止行的位置进行描述)
 */
public class CFunctionVariableUtil {

    private Set<Class> CLASS_SET = new HashSet<>();
    private Set<IBinding> PARAM_VAL_SET = new HashSet<>();
    private Set<IScope> SCOPE_SET = new HashSet<>();
    private Set<IASTNode> IAST_NODE_ALL_SET = new HashSet<>();
    private Set<IASTName> IAST_NAME_WITH_IBINDING_SET = new HashSet<>();

    public Set<IASTName> getIAST_NAME_WITH_IBINDING_SET() {
        return IAST_NAME_WITH_IBINDING_SET;
    }

    public void methodParams(IASTNode iastNode){
        if(iastNode instanceof IASTName){

            //查看全局
            IAST_NODE_ALL_SET.add(iastNode);

            IASTName iastName = (IASTName) iastNode;
            IBinding iBinding = iastName.resolveBinding();

            Class cls = iBinding.getClass();

            CLASS_SET.add(cls);
            boolean isProblemBinding = checkProblemBinding(iBinding);
            if(!isProblemBinding){
                try{
                    IScope iScope = iBinding.getScope();
                    SCOPE_SET.add(iScope);
                    IName iName = iScope.getScopeName();
                    if(iName != null){
                        IASTFileLocation iastFileLocation = iName.getFileLocation();
                        if (iastFileLocation != null) {
                            IAST_NAME_WITH_IBINDING_SET.add(iastName);
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("MethodParamsIBinding.iBinding问题:" + iBinding.toString());
            }
        }
    }

    /***
     * 判断是否是problemBinding
     * @param iBinding
     * @return true有问题的ibinding
     */
    private boolean checkProblemBinding(IBinding iBinding){
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
        if(iBinding instanceof CVariable){
            PARAM_VAL_SET.add(iBinding);
            CVariable cVariable = (CVariable) iBinding;
            cVariable.getDefinition();
            IValue iValue = cVariable.getInitialValue();
            IASTNode iastNodeDefinition = cVariable.getDefinition();
            String name = cVariable.getName();
            IBinding iBindingOwner = cVariable.getOwner();
            IScope iScope = cVariable.getScope();
            IType iType = cVariable.getType();
            System.out.println();
        }
        //方法入参
        if(iBinding instanceof CParameter){
            PARAM_VAL_SET.add(iBinding);
            CParameter cParameter = (CParameter) iBinding;
//            int paramPosition = cParameter.getParameterPosition();
//            IASTNode iastNode = cParameter.getPhysicalNode();
//            IASTNode iastNodeDefinition = cParameter.getDefinition();
            IValue iValue = cParameter.getInitialValue();
            ILinkage iLinkage = cParameter.getLinkage();
            IBinding iBindingOwner = cParameter.getOwner();
            IType iType = cParameter.getType();
            IScope iScope = cParameter.getScope();
            System.out.println();
        }
        return false;
    }

    public static FunctionVariableVo getFileFunctionVariableVo(Set<IASTName> iastNameSet){
        Set<FunctionVariableDto> eClassSet = new HashSet<>();
        Set<FunctionVariableDto> eGlobalSet = new HashSet<>();
        Set<FunctionVariableDto> eLocalSet = new HashSet<>();
        iastNameSet.stream().forEach(iastName->{
            try {
                IBinding iBinding = iastName.resolveBinding();
                IScope iScope = iBinding.getScope();
                EScopeKind eScopeKind = iScope.getKind();
                IName iName = iScope.getScopeName();
                IASTFileLocation iastFileLocation = iName.getFileLocation();
                if(iastFileLocation == null){
                     return;
                }
                FunctionVariableDto dto = FunctionVariableDto.builder()
                        .seteScopeKind(eScopeKind)
                        .setIastFileLocation(iastFileLocation)
                        .setSimpleName(new String(iName.getSimpleID()))
                        .setRawSignature(iastName.getRawSignature())
                        .build();
                if(eScopeKind == EScopeKind.eClassType){
                    eClassSet.add(dto);
                }
                else if (eScopeKind == EScopeKind.eGlobal) {
                    eGlobalSet.add(dto);
                }
                else if (eScopeKind == EScopeKind.eLocal) {
                    eLocalSet.add(dto);
                }
            } catch (DOMException ex) {
                ex.printStackTrace();
            }
        });
        //cleanAllSet();
        return new FunctionVariableVo(eClassSet, eGlobalSet, eLocalSet);
    }

    public void printResultAndClearSet(){
        //输出IBinding的主要类
        CLASS_SET.stream().map(e->e.getName()).forEach(System.out::println);
        //输出函数中的参数：函数入参，函数中定义的局部变量
        PARAM_VAL_SET.stream().forEach(pv->{System.out.println(pv.getName());});
        //输出作用域
        SCOPE_SET.stream().forEach(ss->{System.out.println(ss.getKind().toString() + "," + ss.getScopeName());});
        //输出ibinding的节点
        IAST_NODE_ALL_SET.stream().forEach(ins->{
            //System.out.println("IASTNodeClass:" + ins.getClass().getName());
            IASTName iastName = (IASTName) ins;
            //System.out.println("IASTNameClass:" + iastName.getClass().getName());
            IBinding iBinding = iastName.resolveBinding();
            //System.out.println("IBindingClass:" + iBinding.getClass().getName());
            if (!(iBinding instanceof ProblemBinding)) {
                try {
                    IScope iScope = iBinding.getScope();
                    EScopeKind kind = iScope.getKind();
                    IName iName = iScope.getScopeName();
                    IASTFileLocation location = iName.getFileLocation();
                    String simpleName = new String(iName.getSimpleID());
                    System.out.println("kind:" + kind.toString() + ",simpleName:" + simpleName);
                }catch (Exception e) {}

            }
        });

        IAST_NAME_WITH_IBINDING_SET.stream().forEach(iastName -> {
            IBinding iBinding = iastName.resolveBinding();
            try {
                String rawSignature = iastName.getRawSignature();
                IScope iScope = iBinding.getScope();
                EScopeKind eScopeKind = iScope.getKind();
                IName iName = iScope.getScopeName();
                IASTFileLocation iastFileLocation = iName.getFileLocation();
                String fileName = iastFileLocation.getFileName();
                int startLineNumber = iastFileLocation.getStartingLineNumber();
                String simpleName = new String(iName.getSimpleID());
                if (EScopeKind.eClassType == eScopeKind ||  EScopeKind.eGlobal == eScopeKind) {
                    System.out.println();
                }
            }catch (Exception ex) {}

        });
        cleanAllSet();
    }

    private void cleanAllSet(){
        CLASS_SET.clear();
        PARAM_VAL_SET.clear();
        SCOPE_SET.clear();
        IAST_NODE_ALL_SET.clear();
        IAST_NAME_WITH_IBINDING_SET.clear();
    }

}
