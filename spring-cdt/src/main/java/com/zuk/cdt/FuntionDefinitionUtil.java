package com.zuk.cdt;

import org.eclipse.cdt.core.dom.ast.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FuntionDefinitionUtil {
    public static List<IASTFunctionDefinition> getFuncationDefinistion(IASTTranslationUnit unit, String filePath){
        List<IASTFunctionDefinition> fds = new ArrayList<>();
        IASTDeclaration[] iastDeclarations = unit.getDeclarations();
        Arrays.stream(iastDeclarations).forEach(
                e->{
                    System.out.println(e.getClass().getName());
                    if(e instanceof IASTFunctionDefinition){
                        fds.add((IASTFunctionDefinition) e);
                    }
                }
        );
//        unit.accept(new ASTVisitor() {
//            @Override
//            public int visit(IASTDeclarator declarator) {
//                if(declarator instanceof IASTFunctionDefinition){
//                    fds.add((IASTFunctionDefinition) declarator);
//                }
//                return 1;
//            }
//        });
        return fds;
    }
}
