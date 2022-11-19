import com.github.javaparser.JavaParser;
import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class ExtractToVariableTests {
    final String code =
        "class KMP_String_Matching {\n" +
        "    void KMPSearch(String pat, String txt) {\n" +
        "        int M = pat.length();\n" +
        "        int N = txt.length();\n" +
        "\n" +
        "        // create lps[] that will hold the longest\n" +
        "        // prefix suffix values for pattern\n" +
        "        int lps[] = new int[M];\n" +
        "        int j = 0; // index for pat[]\n" +
        "\n" +
        "        // Preprocess the pattern (calculate lps[]\n" +
        "        // array)\n" +
        "        //computeLPSArray(pat, M, lps);\n" +
        "\n" +
        "        int i = 0; // index for txt[]\n" +
        "        while (i < N) {\n" +
        "            if (pat.charAt(j) == txt.charAt(i)) {\n" +
        "                j++;\n" +
        "                i++;\n" +
        "            }\n" +
        "            if (j == M) {\n" +
        "                System.out.println(\"Found pattern \"\n" +
        "                        + \"at index \" + (i - j));\n" +
        "                j = lps[j - 1];\n" +
        "            }\n" +
        "\n" +
        "            // mismatch after j matches\n" +
        "            else if (i < N\n" +
        "                    && pat.charAt(j) != txt.charAt(i)) {\n" +
        "                // Do not match lps[0..lps[j-1]] characters,\n" +
        "                // they will match anyway\n" +
        "                if (j != 0)\n" +
        "                    j = lps[j - 1];\n" +
        "                else\n" +
        "                    i = i + 1;\n" +
        "            }\n" +
        "        }\n" +
        "    }\n" +
        "}";

    JavaParser parser;
    CompilationUnit cu;
    MethodDeclaration methodAST;

    @Before public void reParse() {
        parser = Parser.initParser();
        cu = parser.parse(code).getResult().get();
        methodAST = cu.getClassByName("KMP_String_Matching").get().getMethods().get(0);
        cu.toString();//dumb bug
        methodAST.toString();
    }

    @Test public void testExtractSimpleExpr() {
        boolean changed = Refactor.extract(methodAST,
                new Range(new Position(23,41), new Position(23,47)),
                "diff",
                PrimitiveType.intType());
        assertTrue(changed);
        assertEquals(
          "void KMPSearch(String pat, String txt) {\n" +
            "    int M = pat.length();\n" +
            "    int N = txt.length();\n" +
            "    // create lps[] that will hold the longest\n" +
            "    // prefix suffix values for pattern\n" +
            "    int[] lps = new int[M];\n" +
            "    // index for pat[]\n" +
            "    int j = 0;\n" +
            "    // Preprocess the pattern (calculate lps[]\n" +
            "    // array)\n" +
            "    // computeLPSArray(pat, M, lps);\n" +
            "    // index for txt[]\n" +
            "    int i = 0;\n" +
            "    while (i < N) {\n" +
            "        if (pat.charAt(j) == txt.charAt(i)) {\n" +
            "            j++;\n" +
            "            i++;\n" +
            "        }\n" +
            "        if (j == M) {\n" +
            "            int diff = (i - j);\n" +
            "            System.out.println(\"Found pattern \" + \"at index \" + diff);\n" +
            "            j = lps[j - 1];\n" +
            "        } else // mismatch after j matches\n" +
            "        if (i < N && pat.charAt(j) != txt.charAt(i)) {\n" +
            "            // Do not match lps[0..lps[j-1]] characters,\n" +
            "            // they will match anyway\n" +
            "            if (j != 0)\n" +
            "                j = lps[j - 1];\n" +
            "            else\n" +
            "                i = i + 1;\n" +
            "        }\n" +
            "    }\n" +
            "}",
            methodAST.toString()
        );
    }
    @Test public void testExtractInCondition() {
        boolean changed = Refactor.extract(methodAST,
                new Range(new Position(33,29), new Position(33,33)),
                "diff",
                PrimitiveType.intType());
        assertTrue(changed);
        assertEquals(
                "void KMPSearch(String pat, String txt) {\n" +
                        "    int M = pat.length();\n" +
                        "    int N = txt.length();\n" +
                        "    // create lps[] that will hold the longest\n" +
                        "    // prefix suffix values for pattern\n" +
                        "    int[] lps = new int[M];\n" +
                        "    // index for pat[]\n" +
                        "    int j = 0;\n" +
                        "    // Preprocess the pattern (calculate lps[]\n" +
                        "    // array)\n" +
                        "    // computeLPSArray(pat, M, lps);\n" +
                        "    // index for txt[]\n" +
                        "    int i = 0;\n" +
                        "    while (i < N) {\n" +
                        "        if (pat.charAt(j) == txt.charAt(i)) {\n" +
                        "            j++;\n" +
                        "            i++;\n" +
                        "        }\n" +
                        "        if (j == M) {\n" +
                        "            System.out.println(\"Found pattern \" + \"at index \" + (i - j));\n" +
                        "            j = lps[j - 1];\n" +
                        "        } else // mismatch after j matches\n" +
                        "        if (i < N && pat.charAt(j) != txt.charAt(i)) {\n" +
                        "            // Do not match lps[0..lps[j-1]] characters,\n" +
                        "            // they will match anyway\n" +
                        "            if (j != 0) {\n" +
                        "                int diff = j - 1;\n" +
                        "                j = lps[diff];\n" +
                        "            } else\n" +
                        "                i = i + 1;\n" +
                        "        }\n" +
                        "    }\n" +
                        "}",
                methodAST.toString()
        );
    }

    @Test public void testAlreadyAVariable() {
        boolean changed = Refactor.extract(methodAST,
                new Range(new Position(2,21), new Position(2,47)),
                "init",
                new ArrayType(PrimitiveType.intType()));

        assertFalse(changed);
        assertEquals(
    "void KMPSearch(String pat, String txt) {\n" +
            "    int M = pat.length();\n" +
            "    int N = txt.length();\n" +
            "    // create lps[] that will hold the longest\n" +
            "    // prefix suffix values for pattern\n" +
            "    int[] lps = new int[M];\n" +
            "    // index for pat[]\n" +
            "    int j = 0;\n" +
            "    // Preprocess the pattern (calculate lps[]\n" +
            "    // array)\n" +
            "    // computeLPSArray(pat, M, lps);\n" +
            "    // index for txt[]\n" +
            "    int i = 0;\n" +
            "    while (i < N) {\n" +
            "        if (pat.charAt(j) == txt.charAt(i)) {\n" +
            "            j++;\n" +
            "            i++;\n" +
            "        }\n" +
            "        if (j == M) {\n" +
            "            System.out.println(\"Found pattern \" + \"at index \" + (i - j));\n" +
            "            j = lps[j - 1];\n" +
            "        } else // mismatch after j matches\n" +
            "        if (i < N && pat.charAt(j) != txt.charAt(i)) {\n" +
            "            // Do not match lps[0..lps[j-1]] characters,\n" +
            "            // they will match anyway\n" +
            "            if (j != 0)\n" +
            "                j = lps[j - 1];\n" +
            "            else\n" +
            "                i = i + 1;\n" +
            "        }\n" +
            "    }\n" +
            "}",
            methodAST.toString()
        );
    }
    @Test public void testExtractBadRange() {
        boolean changed = Refactor.extract(methodAST,
                new Range(new Position(2,24), new Position(2,47)),
                "init",
                new PrimitiveType(PrimitiveType.Primitive.BOOLEAN));

        assertFalse(changed);
        assertEquals(
            "void KMPSearch(String pat, String txt) {\n" +
            "    int M = pat.length();\n" +
            "    int N = txt.length();\n" +
            "    // create lps[] that will hold the longest\n" +
            "    // prefix suffix values for pattern\n" +
            "    int[] lps = new int[M];\n" +
            "    // index for pat[]\n" +
            "    int j = 0;\n" +
            "    // Preprocess the pattern (calculate lps[]\n" +
            "    // array)\n" +
            "    // computeLPSArray(pat, M, lps);\n" +
            "    // index for txt[]\n" +
            "    int i = 0;\n" +
            "    while (i < N) {\n" +
            "        if (pat.charAt(j) == txt.charAt(i)) {\n" +
            "            j++;\n" +
            "            i++;\n" +
            "        }\n" +
            "        if (j == M) {\n" +
            "            System.out.println(\"Found pattern \" + \"at index \" + (i - j));\n" +
            "            j = lps[j - 1];\n" +
            "        } else // mismatch after j matches\n" +
            "        if (i < N && pat.charAt(j) != txt.charAt(i)) {\n" +
            "            // Do not match lps[0..lps[j-1]] characters,\n" +
            "            // they will match anyway\n" +
            "            if (j != 0)\n" +
            "                j = lps[j - 1];\n" +
            "            else\n" +
            "                i = i + 1;\n" +
            "        }\n" +
            "    }\n" +
            "}",
            methodAST.toString()
        );
    }
}
