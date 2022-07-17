package com.zuk.cdt.file.function;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;

public class CxxFileFunctionDto {

    private CxxFileFunctionDto.Builder builder;

    private CxxFileFunctionDto(CxxFileFunctionDto.Builder builder){
        this.builder = builder;
    }

    public static CxxFileFunctionDto.Builder builder(){
        return new CxxFileFunctionDto.Builder();
    }

    public Builder getBuilder() { return builder; }

    public static class Builder {
        //方法名
        private String functionName;
        //用来计算多态
        private String md5;
        //开始行号
        private int startLineNumber;
        //截止行号
        private int endLineNumber;
        //
        private IASTFileLocation iastFileLocation;

        public CxxFileFunctionDto build(){
            return new CxxFileFunctionDto(this);
        }

        public String getFunctionName() {
            return functionName;
        }

        public String getMd5() {
            return md5;
        }

        public int getStartLineNumber() {
            return startLineNumber;
        }

        public IASTFileLocation getIastFileLocation() {
            return iastFileLocation;
        }

        public int getEndLineNumber() { return endLineNumber; }

        public Builder setFunctionName(String functionName) {
            this.functionName = functionName;
            return this;
        }

        public Builder setMd5(String md5) {
            this.md5 = md5;
            return this;
        }

        public Builder setStartLineNumber(int startLineNumber) {
            this.startLineNumber = startLineNumber;
            return this;
        }

        public Builder setIastFileLocation(IASTFileLocation iastFileLocation) {
            this.iastFileLocation = iastFileLocation;
            return this;
        }

        public Builder setEndLineNumber(int endLineNumber) {
            this.endLineNumber = endLineNumber;
            return this;
        }
    }
}
