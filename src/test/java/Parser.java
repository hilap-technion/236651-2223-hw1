import com.github.javaparser.JavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class Parser {
    public static JavaParser initParser() {
        JavaParser jp = new JavaParser();
        ReflectionTypeSolver typeSolver = new ReflectionTypeSolver();
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        jp.getParserConfiguration().setSymbolResolver(symbolSolver);
        jp.getParserConfiguration().setLexicalPreservationEnabled(true);
        return jp;
    }
}
