package com.zuk.cdt.binding.dto;

import org.eclipse.cdt.core.dom.ast.EScopeKind;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;

public class DeclareVariableDto {
    private Builder builder;
    DeclareVariableDto(Builder builder){
        this.builder = builder;
    }
    public static Builder builder(){return new Builder();}

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
