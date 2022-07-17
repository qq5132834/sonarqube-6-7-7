package com.zuk.cdt;

import com.zuk.cdt.file.CxxFileFrame;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.runtime.CoreException;

import java.io.IOException;

public class ZukMainForC {

    public static void main(String[] args) {


    }


    public static CxxFileFrame analyzeFile(String filepath) throws IOException, CoreException {
        IASTTranslationUnit iastTranslationUnit = CDTParser.parse(filepath, CDTParser.Language.C);
        return null;
    }

}
