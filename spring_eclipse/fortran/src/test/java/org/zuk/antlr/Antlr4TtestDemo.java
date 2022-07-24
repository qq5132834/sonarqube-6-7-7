package org.zuk.antlr;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.*;
import org.zuk.antlr.parse.*;

/***
 * 参考：https://blog.csdn.net/SevenBerry/article/details/124551631
 * 参考：https://blog.csdn.net/SHU15121856/article/details/106331151/
 *
 * Program main
 *     Implicit none
 *     write(*,*) "Hello World!"
 *     End program main
 */
public class Antlr4TtestDemo {

    public static void main(String[] args) throws Exception {
        // Fortran77Lexer 是词法分析器
        // Fortran77Parser 是语法分析器

        String content = "rogram ircle1;\n" +
                "stop\n" +
                "end ircle1";
        run(content);


    }

    public static void run(String expr) throws Exception{
        // 对每一个输入的字符串，构造一个 ANTLRStringStream 流 in
        ANTLRInputStream input = new ANTLRInputStream(expr);
        // 用 in 构造词法分析器 lexer，词法分析的作用是将字符聚集成单词或者符号
        Fortran77Lexer lexer = new Fortran77Lexer(input);
        // 用词法分析器 lexer 构造一个记号流 tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // 再使用 tokens 构造语法分析器 parser,至此已经完成词法分析和语法分析的准备工作
        Fortran77Parser ast = new Fortran77Parser(tokens);
        // 最终调用语法分析器的规则 parse（在demo.g4里定义的规则），完成对表达式的验证
        System.out.println();
        Fortran77Parser.ProgramContext programContext = ast.program(); //获取根节点
        Fortran77ParserBaseVisitor fortran77ParserBaseVisitor = new Fortran77ParserBaseVisitor();
        fortran77ParserBaseVisitor.visit(programContext);

        //监听器
        ParseTreeWalker walker = new ParseTreeWalker();
        Fortran77ParserBaseListener fortran77ParserBaseListener =  new Fortran77ParserBaseListener();
        walker.walk(fortran77ParserBaseListener, programContext);

    }


}
