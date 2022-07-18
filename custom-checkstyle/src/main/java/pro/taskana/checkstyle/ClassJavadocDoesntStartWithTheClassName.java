package pro.taskana.checkstyle;

import com.puppycrawl.tools.checkstyle.FileStatefulCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.DetailNode;
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.javadoc.AbstractJavadocCheck;
import com.puppycrawl.tools.checkstyle.utils.JavadocUtil;

/**
 * The ClassJavadocDoesntStartWithTheClassName reports a checkstyle violation for each
 * Javadoc-comment above a class definition that doesn't start with "The " + class name.
 * ClassJavadocDoesntStartWithTheClassName is not applied to classes that don't have a Javadoc
 * comment above their definition.
 */
@FileStatefulCheck
public class ClassJavadocDoesntStartWithTheClassName extends AbstractJavadocCheck {

  private static final String ERROR_MESSAGE =
      "The Javadoc comment above a class definition must start with \"The \" + class name.";

  @Override
  public int[] getDefaultJavadocTokens() {
    return new int[] {
      JavadocTokenTypes.JAVADOC,
    };
  }

  /**
   * Visits a token of type JAVADOC. If the Javadoc is placed above a class definition, checks that
   * the text starts with "The " + class name.
   *
   * @param detailNode the token to process
   */
  /*
  A Javadoc comment above a class definition can be placed differently in the Java tree:

    case 1: The class has one or more annotations

            BLOCK_COMMENT_BEGIN is child of ANNOTATION.
            ANNOTATION is child of MODIFIERS is child of CLASS_DEF.
            Then the class name is the second neighbour of MODIFIERS as class name.

            Example:
              `--CLASS_DEF -> CLASS_DEF [10:0]
              |--MODIFIERS -> MODIFIERS [10:0]
              |   |--ANNOTATION -> ANNOTATION [10:0]
              |   |   |--BLOCK_COMMENT_BEGIN -> /* [9:0]
              |   |   |   |--COMMENT_CONTENT -> * The ExampleClass.  [9:2]
              |   |   |   |   `--JAVADOC -> JAVADOC [9:3]
              |   |   |   |       |--TEXT ->  The ExampleClass.  [9:3]
              |   |   |   |       `--EOF -> <EOF> [9:53]
              |   |   |   `--BLOCK_COMMENT_END -> [...] [9:52]
              |   |   |--AT -> @ [10:0]
              |   |   `--IDENT -> StatelessCheck [10:1]
              |   `--LITERAL_PUBLIC -> public [11:0]
              |--LITERAL_CLASS -> class [11:7]
              |--IDENT -> JavadocMissingWhitespaceAfterAsteriksCheck [11:13]
              |--EXTENDS_CLAUSE -> extends [11:56]
              |   `--IDENT -> AbstractJavadocCheck [11:64]
              `--OBJBLOCK -> OBJBLOCK [11:85]

   case 2: The class doesn't have neither any annotations nor modifiers

           BLOCK_COMMENT_BEGIN is child of CLASS_DEF
           Then, second neighbour of BLOCK_COMMENT_BEGIN is the class name

           Example:
           `--CLASS_DEF -> CLASS_DEF [11:0]
            |--MODIFIERS -> MODIFIERS [11:0]
            |--BLOCK_COMMENT_BEGIN -> /* [9:0]
            |   |--COMMENT_CONTENT -> * The JavadocMissingWhitespaceAfterAsteriksCheck .  [9:2]
            |   |   `--JAVADOC -> JAVADOC [9:3]
            |   |       |--TEXT ->  The JavadocMissingWhitespaceAfterAsteriksCheck .  [9:3]
            |   |       `--EOF -> <EOF> [9:53]
            |   `--BLOCK_COMMENT_END -> [...] [9:52]
            |--LITERAL_CLASS -> class [11:0]
            |--IDENT -> JavadocMissingWhitespaceAfterAsteriksCheck [11:6]
            |--EXTENDS_CLAUSE -> extends [11:49]
            |   `--IDENT -> AbstractJavadocCheck [11:57]
            `--OBJBLOCK -> OBJBLOCK [11:78]

   case 3: The class has modifiers, but no annotations

           BLOCK_COMMENT_BEGIN is child of MODIFIERS
           MODIFIERS is child of CLASS_DEF
           Then, second neighbour of MODIFIERS is the class name

           Example:
            --CLASS_DEF -> CLASS_DEF [12:0]
               |--MODIFIERS -> MODIFIERS [12:0]
               |   |--BLOCK_COMMENT_BEGIN -> /* [11:0]
               |   |   |--COMMENT_CONTENT -> * Run low level SQL Statements reusing the ...  [11:2]
               |   |   |   `--JAVADOC -> JAVADOC [11:3]
               |   |   |       |--TEXT ->  Run low level SQL Statements reusing the ...  [11:3]
                |   |   |       `--EOF -> <EOF> [11:65]
               |   |   `--BLOCK_COMMENT_END -> [...] [11:64]
           |   `--LITERAL_PUBLIC -> public [12:0]
           |--LITERAL_CLASS -> class [12:7]
           |--IDENT -> SqlConnectionRunner [12:13]
           `--OBJBLOCK -> OBJBLOCK [12:33]

  */
  @Override
  public void visitJavadocToken(DetailNode detailNode) {
    detailNode = JavadocUtil.findFirstToken(detailNode, JavadocTokenTypes.TEXT);
    DetailAST classTree = getBlockCommentAst();
    if (classTree != null && detailNode != null) {
      DetailAST parent = classTree.getParent();
      DetailAST className = null;
      if (parent != null && parent.getType() == TokenTypes.CLASS_DEF) {
        className = classTree.getNextSibling().getNextSibling();
      }
      if (parent != null
          && parent.getType() == TokenTypes.ANNOTATION
          && parent.getParent() != null
          && parent.getParent().getParent() != null
          && parent.getParent().getParent().getType() == TokenTypes.CLASS_DEF) {
        className = parent.getParent().getNextSibling().getNextSibling();
      }
      if (parent != null
          && parent.getType() == TokenTypes.MODIFIERS
          && parent.getParent() != null
          && parent.getParent().getType() == TokenTypes.CLASS_DEF) {
        className = parent.getNextSibling().getNextSibling();
      }
      if (className != null
          && !detailNode.getText().strip().startsWith("The " + className.getText())) {
        log(detailNode.getLineNumber(), detailNode.getColumnNumber(), ERROR_MESSAGE);
      }
    }
  }
}
