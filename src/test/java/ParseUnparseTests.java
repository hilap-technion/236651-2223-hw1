import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

import com.github.javaparser.resolution.declarations.AssociableToAST;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.junit.BeforeClass;
import org.junit.Test;


public class ParseUnparseTests {
    JavaParser jp = Parser.initParser();


    @Test public void testParsing() {
        String classCode = "class HelloWorld {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\"); \n" +
                "    }\n" +
                "}";
        ParseResult<CompilationUnit> res1 = jp.parse(classCode);
        if (!res1.isSuccessful()) {
            res1.getProblems().forEach(problem -> System.out.println(problem.getCause()));
            assertTrue("Shouldn't fail.", false);
        }
        CompilationUnit cu = res1.getResult().get();
        assertEquals(1, cu.getTypes().size());
        assertEquals("HelloWorld", cu.getType(0).getName().asString());
    }

    @Test public void testASTtoString() {
        String classCode = "class HelloWorld {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\"); \n" +
                "    }\n" +
                "}";

        ParseResult<CompilationUnit> res1 = jp.parse(classCode);
        CompilationUnit cu = res1.getResult().get();

        assertEquals("class HelloWorld {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}\n", cu.toString());

        assertEquals("[public , static ]",
                cu.getType(0).asClassOrInterfaceDeclaration().getMember(0)
                        .asMethodDeclaration().getModifiers().toString());
    }
    @Test public void testModifyAndWrite1() {
        String classCode = "class HelloWorld {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\"); \n" +
                "    }\n" +
                "}";
        ParseResult<CompilationUnit> res1 = jp.parse(classCode);
        CompilationUnit cu = res1.getResult().get();
        cu.getType(0).setName("HelloWorld2");
        assertEquals("class HelloWorld2 {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}\n", cu.toString());

    }

    @Test public void testModifyAndWrite2() {
        String classCode = "class HelloWorld {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\"); \n" +
                "    }\n" +
                "}";
        ParseResult<CompilationUnit> res1 = jp.parse(classCode);
        CompilationUnit cu = res1.getResult().get();
        ClassOrInterfaceDeclaration classDecl = cu.getType(0).asClassOrInterfaceDeclaration();



        classDecl.addMember(
                new MethodDeclaration(Modifier.createModifierList(Modifier.Keyword.PUBLIC), new VoidType(), "foo")
        );

        assertEquals("class HelloWorld {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n\n" +
                "    public void foo() {\n" +
                "    }\n" +
                "}\n", cu.toString());

    }

    @Test public void testModifyAndWrite3() {
        String classCode = "class HelloWorld {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Good morning, starshine\"); \n" +
                "        System.out.println(\"The earth says hello\"); \n" +
                "    }\n" +
                "}";
        ParseResult<CompilationUnit> res1 = jp.parse(classCode);
        CompilationUnit cu = res1.getResult().get();
        BlockStmt block = cu.getClassByName("HelloWorld").get().getMethods().get(0).getBody().get();
        Expression arg1 = block.getStatement(0).asExpressionStmt().getExpression().asMethodCallExpr().getArgument(0);
        Expression arg2 = block.getStatement(1).asExpressionStmt().getExpression().asMethodCallExpr().getArgument(0);

        Expression arg1Copy = arg1.clone(); //API docs recommend cloning nodes being moved around so parents aren't confused.
        arg2.replace(arg1Copy); //replace arg2 with arg1Copy in its parent
        arg1.replace(arg2.clone());

        assertEquals(
            "class HelloWorld {\n\n" +
            "    public static void main(String[] args) {\n" +
            "        System.out.println(\"The earth says hello\");\n" +
            "        System.out.println(\"Good morning, starshine\");\n" +
            "    }\n" +
            "}\n",
            cu.toString()
        );
    }

    @Test public void testModifyAndWrite4() {
        String classCode = "class HelloWorld {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\"); \n" +
                "    }\n" +
                "}";
        ParseResult<CompilationUnit> res1 = jp.parse(classCode);
        CompilationUnit cu = res1.getResult().get();
        ClassOrInterfaceDeclaration classDecl = cu.getType(0).asClassOrInterfaceDeclaration();

        classDecl.accept(new ModifierVisitor<Void>() { //not entirely an in-place modification

            @Override public Visitable visit(MethodCallExpr call, Void arg) {
                if (call.getNameAsString().equals("println")) {
                    Expression newPrint = new BinaryExpr(
                            new ArrayAccessExpr(new NameExpr("args"),new IntegerLiteralExpr(0)),
                            new StringLiteralExpr("abc"),
                            BinaryExpr.Operator.PLUS
                    );
                    call.setArgument(0,newPrint);
                }
                return super.visit(call, arg);
            }
        },null);

        assertEquals("class HelloWorld {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(args[0] + \"abc\");\n" +
                "    }\n" +
                "}\n", cu.toString());

    }

    @Test public void testGetSymbols() {
        String classCode = "class HelloWorld {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(args[0]); \n" +
                "    }\n" +
                "}";

        CompilationUnit cu = jp.parse(classCode).getResult().get();
        SimpleName args = cu.findAll(SimpleName.class).get(7);
        NameExpr argsUsage = (NameExpr)args.getParentNode().get(); //forcibly downcast from Node
        Parameter argsDef = cu.findAll(Parameter.class).get(0);
        AssociableToAST<?> assocToAst = (AssociableToAST<?>)argsUsage.resolve(); //only Expressions have resolve
        Node resolvedNode = assocToAst.toAst().get();
        assertEquals(argsDef,resolvedNode);
    }
}
