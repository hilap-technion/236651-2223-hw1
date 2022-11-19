import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.declarations.AssociableToAST;

import java.util.List;
import java.util.stream.Collectors;

public class Refactor {
    public static boolean extract(MethodDeclaration method, final Range rangeToExtract, final String newVarName, final Type newVarType) {
        return true;
    }

    public static boolean rename(CompilationUnit cu, final Range rangeToRename, final String renameTo) {
        return true;
    }


}
