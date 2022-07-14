package com.zuk.cdt.file.function.call;

import com.sun.istack.internal.NotNull;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;

public class FunctionCallDto {

    private Builder builder;

    private FunctionCallDto(Builder builder){
        this.builder = builder;
    }

    public Builder getBuilder() { return builder; }

    public static Builder builder(){return new Builder();}

    public static FunctionCallDto createInstanceByIASTName(@NotNull IASTName iastName){
        IBinding iBinding = iastName.resolveBinding();
        if(!(iBinding instanceof ProblemBinding)){
            try {
                IScope iScope = iBinding.getScope();
                IName iName = iScope.getScopeName();
                if(iName.getFileLocation() == null){
                    System.out.println("");
                }
                return FunctionCallDto.builder()
                        .seteScopeKind(iScope.getKind())
                        .setScopeSimpleName(new String(iName.getSimpleID()))
                        .setIastFileLocation(iastName.getFileLocation())
                        .setVariableName(iastName.getRawSignature())
                        .build();
            }catch (Exception e) {}
        }
        return null;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        if(this.builder.geteScopeKind() != null){
            stringBuilder.append(this.builder.geteScopeKind().toString());
            stringBuilder.append(" ");
        }
        if(this.builder.getScopeSimpleName() != null) {
            stringBuilder.append(this.builder.getScopeSimpleName());
            stringBuilder.append("::");
        }
        if (this.builder.getVariableName() != null) {
            stringBuilder.append(this.builder.getVariableName());
            stringBuilder.append(".");
        }
        stringBuilder.append(this.builder.getCallFunctionName());
        stringBuilder.append(";");
        stringBuilder.append("[");
        stringBuilder.append("lineNumber:" + (this.builder.getIastFileLocation()==null?"??":this.builder.getIastFileLocation().getStartingLineNumber()));
        stringBuilder.append(",");
        stringBuilder.append("file:" + (this.builder.getIastFileLocation()==null?"??":this.builder.getIastFileLocation().getFileName()));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    //
    public static class Builder {

        //作用域
        private EScopeKind eScopeKind; //作用域类型
        private IASTFileLocation iastFileLocation; //文件路径
        private String scopeSimpleName; //作用域名称
        private String variableName; //对象名称

        //调用函数名称
        private String callFunctionName;

        public FunctionCallDto build(){
            return new FunctionCallDto(this);
        }

        public EScopeKind geteScopeKind() {
            return eScopeKind;
        }

        public IASTFileLocation getIastFileLocation() {
            return iastFileLocation;
        }

        public String getScopeSimpleName() {
            return scopeSimpleName;
        }

        public String getVariableName() { return variableName; }

        public String getCallFunctionName() {return callFunctionName; }

        public Builder seteScopeKind(EScopeKind eScopeKind) {
            this.eScopeKind = eScopeKind;
            return this;
        }

        public Builder setIastFileLocation(IASTFileLocation iastFileLocation) {
            this.iastFileLocation = iastFileLocation;
            return this;
        }

        public Builder setScopeSimpleName(String scopeSimpleName) {
            this.scopeSimpleName = scopeSimpleName;
            return this;
        }

        public Builder setVariableName(String variableName) {
            this.variableName = variableName;
            return this;
        }

        public Builder setCallFunctionName(String callFunctionName) {
            this.callFunctionName = callFunctionName;
            return this;
        }
    }
}
