package com.zuk.cdt;

import com.zuk.cdt.binding.MethodCallIBinding;
import com.zuk.cdt.binding.MethodParamsIBinding;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import java.util.List;

public class ZukMainForFile {


    public static void main(String[] args) throws Exception {
        String file = "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring-cdt\\src\\main\\resources\\c\\src\\DnsCache.cc";
        IASTTranslationUnit iastTranslationUnit = CDTParser.parse(file, CDTParser.Language.CPP);

        //文件函数输出
        List<IASTFunctionDefinition> functionDefinitions = FuntionDefinitionUtil.getFuncationDefinistion(iastTranslationUnit, file);
        functionDefinitions.stream().forEach(e->{
            //函数方法名
            String funcationMethodName = e.getDeclarator().getName().toString();
            //起始位置
            int startLine = e.getFileLocation().getStartingLineNumber();
            //截止位置
            int endLine = e.getFileLocation().getEndingLineNumber();
            //变量函数AST
            IASTNodeRecursive.recur(e);
            //输出函数中的IBinding和变量
            MethodParamsIBinding.printResultAndClearSet();
            //
            MethodCallIBinding.printResultAndClearSet();
        });

        System.out.println("");
    }



//    public static ISet getMethodOverrides(IASTTranslationUnit tu) {
//        ASTAmbiguousNode.NameCollector anc = new ASTAmbiguousNode.NameCollector();
//        tu.accept(anc);
//        Set<IBinding> bindings = new HashSet<>();
//        Stream.of(anc.getNames()).forEach(it -> bindings.add(it.resolveBinding()));
//        ISetWriter methodOverrides = vf.setWriter();
//        bindings.stream().filter(ICPPMethod.class::isInstance).forEach(override -> {
//            Stream.of(ClassTypeHelper.findOverridden((ICPPMethod) override, tu)).forEach(base -> {
//                try {
//                    methodOverrides.insert(vf.tuple(br.resolveBinding(base), br.resolveBinding(override)));
//                } catch (FactTypeUseException | URISyntaxException e) {
//                    err("Got FactTypeUseException\n" + e.getMessage());
//                }
//            });
//        });
//        return methodOverrides.done();
//    }
}
