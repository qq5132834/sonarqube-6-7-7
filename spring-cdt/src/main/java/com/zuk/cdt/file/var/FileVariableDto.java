package com.zuk.cdt.file.var;

import org.eclipse.cdt.core.dom.ast.EScopeKind;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IScope;

/***
 * 全局变量、类定义变量
 */
public class FileVariableDto {

    private final FileVariableDto.Builder builder;

    private FileVariableDto(Builder builder){
        this.builder = builder;
    }

    public Builder getBuilder() { return builder; }

    public static FileVariableDto.Builder builder(){
        return new FileVariableDto.Builder();
    }

    public static class Builder {

        private EScopeKind eScopeKind;
        private IASTFileLocation iastFileLocation;
        private String simpleName;
        private IScope iScope;
        private String rawSignature;

        public FileVariableDto build(){
            return new FileVariableDto(this);
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

        public IScope getiScope() {
            return iScope;
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

        public Builder setiScope(IScope iScope) {
            this.iScope = iScope;
            return this;
        }

        public Builder setRawSignature(String rawSignature) {
            this.rawSignature = rawSignature;
            return this;
        }
    }
}