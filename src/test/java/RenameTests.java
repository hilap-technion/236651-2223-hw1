import com.github.javaparser.JavaParser;
import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.utils.LineSeparator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RenameTests {
    final String codeStr = "// Java program to Compare two strings\n" +
    "// lexicographically\n" +
    "class Compare {\n" +
    "\n" +
    "    // This method compares two strings\n" +
    "    // lexicographically without using\n" +
    "    // library functions\n" +
    "    public static int stringCompare(String str1,\n" +
    "                                    String str2)\n" +
    "    {\n" +
    "        for (int i = 0; i < str1.length() &&\n" +
    "                i < str2.length(); i++) {\n" +
    "            if ((int)str1.charAt(i) ==\n" +
    "                    (int)str2.charAt(i)) {\n" +
    "                continue;\n" +
    "            }\n" +
    "            else {\n" +
    "                return (int)str1.charAt(i) -\n" +
    "                        (int)str2.charAt(i);\n" +
    "            }\n" +
    "        }\n" +
    "\n" +
    "        // Edge case for strings like\n" +
    "        // String 1=\"Geeky\" and String 2=\"Geekyguy\"\n" +
    "        if (str1.length() < str2.length()) {\n" +
    "            return (str1.length()-str2.length());\n" +
    "        }\n" +
    "        else if (str1.length() > str2.length()) {\n" +
    "            return (str1.length()-str2.length());\n" +
    "        }\n" +
    "\n" +
    "        // If none of the above conditions is true,\n" +
    "        // it implies both the strings are equal\n" +
    "        else {\n" +
    "            return 0;\n" +
    "        }\n" +
    "    }\n" +
    "\n" +
    "    // Driver function to test the above program\n" +
    "    public static void main(String args[])\n" +
    "    {\n" +
    "        String str1 = new String(\"Geeks\");\n" +
    "        String str2 = new String(\"Practice\");\n" +
    "        String str3 = new String(\"Geeks\");\n" +
    "        String str4 = new String(\"Geeksforgeeks\");\n" +
    "        String str5 = new String(\"Spurious\");\n" +
    "\n" +
    "        System.out.println(stringCompare(str1,\n" +
    "                str2));\n" +
    "        System.out.println(stringCompare(str1,\n" +
    "                str3));\n" +
    "        System.out.println(stringCompare(str2,\n" +
    "                str1));\n" +
    "\n" +
    "        // To show for edge case\n" +
    "        // In these cases, the output is the difference of\n" +
    "        // length of the string\n" +
    "        System.out.println(stringCompare(str1,\n" +
    "                str4));\n" +
    "        System.out.println(stringCompare(str4,\n" +
    "                str1));\n" +
    "    }\n" +
    "}";
    JavaParser jp = null;
    private CompilationUnit cu;
    @Before public void reParse() {
        jp = Parser.initParser();
        cu = jp.parse(codeStr).getResult().get();
        cu.toString(); //workaround for stupid bug
    }
    @Test public void renameNoContext(){
        boolean changed = Refactor.rename(cu,
                new Range(new Position(46,16),new Position(46,19)),
                "diffName");
        assertTrue(changed);
        assertEquals(
                "// Driver function to test the above program\n" +
                        "public static void main(String[] args) {\n" +
                        "    String str1 = new String(\"Geeks\");\n" +
                        "    String str2 = new String(\"Practice\");\n" +
                        "    String str3 = new String(\"Geeks\");\n" +
                        "    String str4 = new String(\"Geeksforgeeks\");\n" +
                        "    String diffName = new String(\"Spurious\");\n" +
                        "    System.out.println(stringCompare(str1, str2));\n" +
                        "    System.out.println(stringCompare(str1, str3));\n" +
                        "    System.out.println(stringCompare(str2, str1));\n" +
                        "    // To show for edge case\n" +
                        "    // In these cases, the output is the difference of\n" +
                        "    // length of the string\n" +
                        "    System.out.println(stringCompare(str1, str4));\n" +
                        "    System.out.println(stringCompare(str4, str1));\n" +
                        "}",
                cu.getClassByName("Compare").get().getMethodsByName("main").get(0).toString());
    }



    @Test public void startFromUsage() {
        boolean changed = Refactor.rename(cu,
                new Range(new Position(60,42),new Position(60,45)),
                "diffName");
        assertTrue(changed);
        assertEquals(
                "// Driver function to test the above program\n" +
                        "public static void main(String[] args) {\n" +
                        "    String str1 = new String(\"Geeks\");\n" +
                        "    String str2 = new String(\"Practice\");\n" +
                        "    String str3 = new String(\"Geeks\");\n" +
                        "    String diffName = new String(\"Geeksforgeeks\");\n" +
                        "    String str5 = new String(\"Spurious\");\n" +
                        "    System.out.println(stringCompare(str1, str2));\n" +
                        "    System.out.println(stringCompare(str1, str3));\n" +
                        "    System.out.println(stringCompare(str2, str1));\n" +
                        "    // To show for edge case\n" +
                        "    // In these cases, the output is the difference of\n" +
                        "    // length of the string\n" +
                        "    System.out.println(stringCompare(str1, diffName));\n" +
                        "    System.out.println(stringCompare(diffName, str1));\n" +
                        "}",
                cu.getClassByName("Compare").get().getMethodsByName("main").get(0).toString());
    }



    @Test public void scopesAvoidCollision() {
        boolean changed = Refactor.rename(cu,
                new Range(new Position(9,44),new Position(9,47)),
                "str3");
        assertTrue(changed);
        assertEquals(
            "// Java program to Compare two strings\n" +
            "// lexicographically\n" +
            "class Compare {\n" +
            "\n" +
            "    // This method compares two strings\n" +
            "    // lexicographically without using\n" +
            "    // library functions\n" +
            "    public static int stringCompare(String str1, String str3) {\n" +
            "        for (int i = 0; i < str1.length() && i < str3.length(); i++) {\n" +
            "            if ((int) str1.charAt(i) == (int) str3.charAt(i)) {\n" +
            "                continue;\n" +
            "            } else {\n" +
            "                return (int) str1.charAt(i) - (int) str3.charAt(i);\n" +
            "            }\n" +
            "        }\n" +
            "        // Edge case for strings like\n" +
            "        // String 1=\"Geeky\" and String 2=\"Geekyguy\"\n" +
            "        if (str1.length() < str3.length()) {\n" +
            "            return (str1.length() - str3.length());\n" +
            "        } else if (str1.length() > str3.length()) {\n" +
            "            return (str1.length() - str3.length());\n" +
            "        } else // If none of the above conditions is true,\n" +
            "        // it implies both the strings are equal\n" +
            "        {\n" +
            "            return 0;\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    // Driver function to test the above program\n" +
            "    public static void main(String[] args) {\n" +
            "        String str1 = new String(\"Geeks\");\n" +
            "        String str2 = new String(\"Practice\");\n" +
            "        String str3 = new String(\"Geeks\");\n" +
            "        String str4 = new String(\"Geeksforgeeks\");\n" +
            "        String str5 = new String(\"Spurious\");\n" +
            "        System.out.println(stringCompare(str1, str2));\n" +
            "        System.out.println(stringCompare(str1, str3));\n" +
            "        System.out.println(stringCompare(str2, str1));\n" +
            "        // To show for edge case\n" +
            "        // In these cases, the output is the difference of\n" +
            "        // length of the string\n" +
            "        System.out.println(stringCompare(str1, str4));\n" +
            "        System.out.println(stringCompare(str4, str1));\n" +
            "    }\n" +
            "}\n",
            cu.toString()
        );
    }
    @Test public void scopesAvoidCollision2() {
        boolean changed = Refactor.rename(cu,
                new Range(new Position(53, 17), new Position(53, 20)),
                "diffName");
        assertTrue(changed);
        assertEquals(
            "// Java program to Compare two strings\n" +
            "// lexicographically\n" +
            "class Compare {\n" +
            "\n" +
            "    // This method compares two strings\n" +
            "    // lexicographically without using\n" +
            "    // library functions\n" +
            "    public static int stringCompare(String str1, String str2) {\n" +
            "        for (int i = 0; i < str1.length() && i < str2.length(); i++) {\n" +
            "            if ((int) str1.charAt(i) == (int) str2.charAt(i)) {\n" +
            "                continue;\n" +
            "            } else {\n" +
            "                return (int) str1.charAt(i) - (int) str2.charAt(i);\n" +
            "            }\n" +
            "        }\n" +
            "        // Edge case for strings like\n" +
            "        // String 1=\"Geeky\" and String 2=\"Geekyguy\"\n" +
            "        if (str1.length() < str2.length()) {\n" +
            "            return (str1.length() - str2.length());\n" +
            "        } else if (str1.length() > str2.length()) {\n" +
            "            return (str1.length() - str2.length());\n" +
            "        } else // If none of the above conditions is true,\n" +
            "        // it implies both the strings are equal\n" +
            "        {\n" +
            "            return 0;\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    // Driver function to test the above program\n" +
            "    public static void main(String[] args) {\n" +
            "        String diffName = new String(\"Geeks\");\n" +
            "        String str2 = new String(\"Practice\");\n" +
            "        String str3 = new String(\"Geeks\");\n" +
            "        String str4 = new String(\"Geeksforgeeks\");\n" +
            "        String str5 = new String(\"Spurious\");\n" +
            "        System.out.println(stringCompare(diffName, str2));\n" +
            "        System.out.println(stringCompare(diffName, str3));\n" +
            "        System.out.println(stringCompare(str2, diffName));\n" +
            "        // To show for edge case\n" +
            "        // In these cases, the output is the difference of\n" +
            "        // length of the string\n" +
            "        System.out.println(stringCompare(diffName, str4));\n" +
            "        System.out.println(stringCompare(str4, diffName));\n" +
            "    }\n" +
            "}\n",
            cu.toString()
        );
    }

    @Test public void renameSameName() {
        boolean changed = Refactor.rename(cu,
                new Range(new Position(9,44),new Position(9,47)),
                "str2");
        assertFalse(changed);
        assertEquals(
    "// Java program to Compare two strings\n" +
            "// lexicographically\n" +
            "class Compare {\n" +
            "\n" +
            "    // This method compares two strings\n" +
            "    // lexicographically without using\n" +
            "    // library functions\n" +
            "    public static int stringCompare(String str1, String str2) {\n" +
            "        for (int i = 0; i < str1.length() && i < str2.length(); i++) {\n" +
            "            if ((int) str1.charAt(i) == (int) str2.charAt(i)) {\n" +
            "                continue;\n" +
            "            } else {\n" +
            "                return (int) str1.charAt(i) - (int) str2.charAt(i);\n" +
            "            }\n" +
            "        }\n" +
            "        // Edge case for strings like\n" +
            "        // String 1=\"Geeky\" and String 2=\"Geekyguy\"\n" +
            "        if (str1.length() < str2.length()) {\n" +
            "            return (str1.length() - str2.length());\n" +
            "        } else if (str1.length() > str2.length()) {\n" +
            "            return (str1.length() - str2.length());\n" +
            "        } else // If none of the above conditions is true,\n" +
            "        // it implies both the strings are equal\n" +
            "        {\n" +
            "            return 0;\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    // Driver function to test the above program\n" +
            "    public static void main(String[] args) {\n" +
            "        String str1 = new String(\"Geeks\");\n" +
            "        String str2 = new String(\"Practice\");\n" +
            "        String str3 = new String(\"Geeks\");\n" +
            "        String str4 = new String(\"Geeksforgeeks\");\n" +
            "        String str5 = new String(\"Spurious\");\n" +
            "        System.out.println(stringCompare(str1, str2));\n" +
            "        System.out.println(stringCompare(str1, str3));\n" +
            "        System.out.println(stringCompare(str2, str1));\n" +
            "        // To show for edge case\n" +
            "        // In these cases, the output is the difference of\n" +
            "        // length of the string\n" +
            "        System.out.println(stringCompare(str1, str4));\n" +
            "        System.out.println(stringCompare(str4, str1));\n" +
            "    }\n" +
            "}\n",
            cu.toString()
        );
    }
    @Test public void renameBadRange() {
        boolean changed = Refactor.rename(cu,
                new Range(new Position(9,43),new Position(9,52)),
                "str8");
        assertFalse(changed);
        assertEquals(
            "// Java program to Compare two strings\n" +
            "// lexicographically\n" +
            "class Compare {\n" +
            "\n" +
            "    // This method compares two strings\n" +
            "    // lexicographically without using\n" +
            "    // library functions\n" +
            "    public static int stringCompare(String str1, String str2) {\n" +
            "        for (int i = 0; i < str1.length() && i < str2.length(); i++) {\n" +
            "            if ((int) str1.charAt(i) == (int) str2.charAt(i)) {\n" +
            "                continue;\n" +
            "            } else {\n" +
            "                return (int) str1.charAt(i) - (int) str2.charAt(i);\n" +
            "            }\n" +
            "        }\n" +
            "        // Edge case for strings like\n" +
            "        // String 1=\"Geeky\" and String 2=\"Geekyguy\"\n" +
            "        if (str1.length() < str2.length()) {\n" +
            "            return (str1.length() - str2.length());\n" +
            "        } else if (str1.length() > str2.length()) {\n" +
            "            return (str1.length() - str2.length());\n" +
            "        } else // If none of the above conditions is true,\n" +
            "        // it implies both the strings are equal\n" +
            "        {\n" +
            "            return 0;\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    // Driver function to test the above program\n" +
            "    public static void main(String[] args) {\n" +
            "        String str1 = new String(\"Geeks\");\n" +
            "        String str2 = new String(\"Practice\");\n" +
            "        String str3 = new String(\"Geeks\");\n" +
            "        String str4 = new String(\"Geeksforgeeks\");\n" +
            "        String str5 = new String(\"Spurious\");\n" +
            "        System.out.println(stringCompare(str1, str2));\n" +
            "        System.out.println(stringCompare(str1, str3));\n" +
            "        System.out.println(stringCompare(str2, str1));\n" +
            "        // To show for edge case\n" +
            "        // In these cases, the output is the difference of\n" +
            "        // length of the string\n" +
            "        System.out.println(stringCompare(str1, str4));\n" +
            "        System.out.println(stringCompare(str4, str1));\n" +
            "    }\n" +
            "}\n",
            cu.toString()
        );
    }
}
