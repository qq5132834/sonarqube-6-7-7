package com.zuk.cdt.binding.dto;

import com.sun.istack.internal.NotNull;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;

public class DeclareVariableDto {

    private Builder builder;

    private DeclareVariableDto(Builder builder){
        this.builder = builder;
    }
    public static Builder builder(){return new Builder();}

    public static DeclareVariableDto createInstanceByIASTName(@NotNull IASTNode iastNode, @NotNull IASTName iastName){
        IBinding iBinding = iastName.resolveBinding();
        if(!(iBinding instanceof ProblemBinding)){
            try {
                IScope iScope = iBinding.getScope();
                IName iName = iScope.getScopeName();
                if(iName.getFileLocation() == null){
                    System.out.println("");
                }
                return DeclareVariableDto.builder()
                        .seteScopeKind(iScope.getKind())
                        .setSimpleName(new String(iName.getSimpleID()))
                        .setIastFileLocation(iastNode.getFileLocation())
                        .setRawSignature(iastNode.getRawSignature())
                        .build();
            }catch (Exception e) {}
        }
        return null;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("kind:" + this.builder.geteScopeKind().toString());
        stringBuilder.append(",");
        stringBuilder.append("simpleName:" + this.builder.getSimpleName());
        stringBuilder.append(",");
        stringBuilder.append("rawSignature:" + this.builder.getRawSignature());
        stringBuilder.append(",");
        stringBuilder.append("lineNumber:" + (this.builder.getIastFileLocation()==null?"??":this.builder.getIastFileLocation().getStartingLineNumber()));
        stringBuilder.append(",");
        stringBuilder.append("file:" + (this.builder.getIastFileLocation()==null?"??":this.builder.getIastFileLocation().getFileName()));
        return stringBuilder.toString();
    }

    //
    public static class Builder {
        private EScopeKind eScopeKind;
        private IASTFileLocation iastFileLocation;
        private String simpleName;
        private String rawSignature;

        public DeclareVariableDto build(){
            return new DeclareVariableDto(this);
        }

        public EScopeKind geteScopeKind() {
            return eScopeKind;
        }

        public IASTFileLocation getIastFileLocation() {
            return iastFileLocation;
        }

        public String getSimpleName() {
            return simpleName;
        }

        public String getRawSignature() {
            return rawSignature;
        }

        public Builder seteScopeKind(EScopeKind eScopeKind) {
            this.eScopeKind = eScopeKind;
            return this;
        }

        public Builder setIastFileLocation(IASTFileLocation iastFileLocation) {
            this.iastFileLocation = iastFileLocation;
            return this;
        }

        public Builder setSimpleName(String simpleName) {
            this.simpleName = simpleName;
            return this;
        }

        public Builder setRawSignature(String rawSignature) {
            this.rawSignature = rawSignature;
            return this;
        }
    }
}
