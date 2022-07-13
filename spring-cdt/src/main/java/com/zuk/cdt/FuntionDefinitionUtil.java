package com.zuk.cdt;

import com.zuk.cdt.binding.dto.DeclareVariableDto;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.ProblemBinding;

import java.util.*;

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

                    getIBinding(e);
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
        printlnResult();
        return fds;
    }

    //存储文件的ibinding
    public static Map<IASTNode, IASTNode> IAST_NODE_SET = new HashMap<>();
    public static Map<DeclareVariableDto, DeclareVariableDto> DECLARE_VARIABLE = new HashMap<>();
    private static void getIBinding(IASTNode iastNode){
        if(iastNode instanceof IASTName){
            IASTName iastName = (IASTName) iastNode;
            IBinding iBinding = iastName.resolveBinding();
            if(!(iBinding instanceof ProblemBinding)){
                IAST_NODE_SET.put(iastNode, iastNode);

                //将IASTName转变dto
                DeclareVariableDto declareVariableDto = DeclareVariableDto.createInstanceByIASTName(iastName);
                if (declareVariableDto != null) {
                    DECLARE_VARIABLE.put(declareVariableDto, declareVariableDto);
                }

            }
        }
        if(iastNode != null && iastNode.getChildren() != null){
            Arrays.stream(iastNode.getChildren()).forEach(FuntionDefinitionUtil::getIBinding);
        }
    }

    public static void printlnResult(){
        System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
        DECLARE_VARIABLE.keySet().stream().forEach(System.out::println);
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//        IAST_NODE_SET.keySet().stream().forEach(ins->{
//            //System.out.println("IASTNodeClass:" + ins.getClass().getName());
//            IASTName iastName = (IASTName) ins;
//            //System.out.println("IASTNameClass:" + iastName.getClass().getName());
//            IBinding iBinding = iastName.resolveBinding();
//            //System.out.println("IBindingClass:" + iBinding.getClass().getName());
//            if (!(iBinding instanceof ProblemBinding)) {
//                try {
//                    IScope iScope = iBinding.getScope();
//                    EScopeKind kind = iScope.getKind();
//                    IName iName = iScope.getScopeName();
//                    IASTFileLocation location = iName.getFileLocation();
//                    String simpleName = new String(iName.getSimpleID());
//                    System.out.println("kind:" + kind.toString()
//                                    + ",simpleName:" + simpleName
//                                    + ",rawSignature:" + ins.getRawSignature().replaceAll("\n" , "")
//                                    + ",lineNumber:" + ins.getFileLocation().getStartingLineNumber()
//                                    + ",file:" + ins.getFileLocation().getFileName()
//                                    )
//                    ;
//                }catch (Exception e) {}
//
//            }
//        });
    }

}
