package org.zuk.eclipse.fortran;

import org.eclipse.core.resources.IFile;
import org.eclipse.photran.internal.core.lexer.*;
import org.eclipse.photran.internal.core.lexer.sourceform.UnpreprocessedFreeSourceForm;
import org.eclipse.photran.internal.core.parser.*;
import org.eclipse.photran.internal.core.sourceform.ISourceForm;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

public class FortranLexerFactoryTest {

    public static void main(String[] args) throws Exception {
        ASTLexerFactory astLexerFactory = new ASTLexerFactory();
        ISourceForm iSourceForm = new UnpreprocessedFreeSourceForm();
        File file = new File("C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring_eclipse\\fortran\\f\\main.f90");
        IAccumulatingLexer iAccumulatingLexer = astLexerFactory.createLexer(file, iSourceForm);
        TokenList tokenList = iAccumulatingLexer.getTokenList();
        Parser parser = new Parser();
        ASTExecutableProgramNode astExecutableProgramNode = parser.parse(iAccumulatingLexer);
        IASTListNode<IProgramUnit> iProgramUnits = astExecutableProgramNode.getProgramUnitList();
        reusIASTListNode(iProgramUnits);
    }

    private static void reusIASTNode(IASTNode iastNode) {
        System.out.println("iastNode:" + iastNode.toString() + ",class:" + iastNode.getClass().getName());
        iastNode.getChildren().forEach(child -> {
            FortranLexerFactoryTest.reusIASTNode(child);
        });

    }

    private static void reusIASTListNode(IASTListNode<IProgramUnit> iastListNode){
        System.out.println("reusIASTListNode:" + iastListNode.getClass().getName());
        if (iastListNode != null) {

            List<IASTNode> iastNodeList =
                iastListNode.stream().filter(node -> {
                    if (node instanceof IASTNode) {
                        return true;
                    }
                    System.out.println("no IASTNode");
                    return false;
                }).map(e->(IASTNode) e).collect(Collectors.toList());

            if (iastNodeList != null) {
                iastNodeList.stream().forEach(FortranLexerFactoryTest::reusIASTNode);
            }
        }
    }



    public static class MySourceForm implements ISourceForm {

        @Override
        public ILexer createLexer(Reader in, IFile file, String filename, boolean accumulateWhitetext) throws IOException {
            return new FreeFormLexerPhase2(new FreeFormLexerPhase1(in, file, filename, accumulateWhitetext));
        }

        @Override
        public ISourceForm configuredWith(Object o) {
            return null;
        }

        @Override
        public boolean isFixedForm() {
            return false;
        }

        @Override
        public boolean isCPreprocessed() {
            return false;
        }
    }

}
