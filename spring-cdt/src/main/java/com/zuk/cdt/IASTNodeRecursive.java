package com.zuk.cdt;

import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.ast.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IASTNodeRecursive {

    private static Set<String> iBindingSet = new HashSet<>();

    public static void recur(IASTNode iastNode){
//        System.out.println(iastNode.getFileLocation().getStartingLineNumber() + "," + iastNode.getRawSignature());
        if(iastNode instanceof IVariable){
//            System.out.println("IVariable");
        }
        else if (iastNode instanceof IParameter) {
//            System.out.println("IParameter");
        }
        else if(iastNode instanceof IASTName){
            try {
                IASTName iastName = (IASTName) iastNode;
                IBinding iBinding = iastName.resolveBinding();
                iBindingSet.add(iBinding.getClass().getName());
                //System.out.println(iBinding.getClass().getName());
                //IScope iScope = iBinding.getScope();
                IBinding iBindingOwner = iBinding.getOwner();
                ILinkage iLinkage = iBinding.getLinkage();
                System.out.println(iastNode.getFileLocation().getStartingLineNumber()
                        + "," + iBinding.getName()
                );

                if(
                        iBindingOwner == null ||
                                iLinkage == null
                ){
                    System.out.println("");
                }

            }catch (Exception e) {
                e.printStackTrace();
            }

        }
        else {
            Arrays.stream(iastNode.getTranslationUnit().getDeclarations()).forEach(e->{

            });
        }
        IASTNode[] iastNodes = iastNode.getChildren();
        if(iastNodes != null && iastNodes.length > 0){
            for (IASTNode node : iastNodes) {
                recur(node);
            }
        }
    }


}
