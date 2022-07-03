package compiler;


import gen.jythonLexer;
import gen.jythonListener;
import gen.jythonParser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import symboltable.WalkAndDetect;

import java.io.IOException;


public class compiler {
    public static void main(String[] args) throws IOException {
        WalkAndDetect walkAndDetect = new WalkAndDetect();
        CharStream stream = CharStreams.fromFileName("./samples/input2.cl");
        jythonLexer lexer = new jythonLexer(stream);
        TokenStream tokens = new CommonTokenStream(lexer);
        jythonParser parser = new jythonParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        jythonListener listener = new ProgramPrinterTest();
        walker.walk(walkAndDetect,tree);
//        walker.walk(listener,tree);
    }
}
