package pro.taskana.checkstyle;

import com.puppycrawl.tools.checkstyle.StatelessCheck;
import com.puppycrawl.tools.checkstyle.api.DetailNode;
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes;
import com.puppycrawl.tools.checkstyle.checks.javadoc.AbstractJavadocCheck;
import com.puppycrawl.tools.checkstyle.utils.JavadocUtil;

/**
 * The JavadocTagStartsInUppercaseOrEndsWithAPoint reports a checkstyle violation for each Javadoc
 * tag that starts in uppercase or ends with a dot.
 */
@StatelessCheck
public class JavadocTagStartsInUppercaseOrEndsWithAPoint extends AbstractJavadocCheck {

  private static final String ERROR_MESSAGE =
      "The Javadoc tag should neither start in uppercase nor end with a dot.";

  @Override
  public int[] getDefaultJavadocTokens() {
    return new int[] {
      JavadocTokenTypes.JAVADOC_TAG,
    };
  }

  @Override
  public int[] getRequiredJavadocTokens() {
    return getAcceptableJavadocTokens();
  }

  /**
   * Visits a token of type JAVADOC_TAG and checks the first and last characters of the description
   * of the tag. First character shouldn't be uppercase, and last character shouldn't be a dot.
   *
   * @param detailNode the token to process
   */
  /* Example extract from a valid Javadoc tree:
   `--JAVADOC -> JAVADOC [20:5]
      |       |--NEWLINE -> \n [20:5]
      |       |--LEADING_ASTERISK ->    * [21:0]
      |       |--TEXT ->  Run custom queries on a given connection. [21:4]
      |       |--NEWLINE -> \n [21:83]
      |       |--LEADING_ASTERISK ->    * [22:0]
      |       |--NEWLINE -> \n [22:4]
      |       |--LEADING_ASTERISK ->    * [23:0]
      |       |--WS ->   [23:4]
      |       |--JAVADOC_TAG -> JAVADOC_TAG [23:5]
      |       |   |--PARAM_LITERAL -> @param [23:5]
      |       |   |--WS ->   [23:11]
      |       |   |--PARAMETER_NAME -> consumer [23:12]
      |       |   |--WS ->   [23:20]
      |       |   `--DESCRIPTION -> DESCRIPTION [23:21]
      |       |       |--TEXT -> consumes a connection [23:21]
      |       |       `--NEWLINE -> \n [23:42]
      |       |--LEADING_ASTERISK ->    * [24:0]
   You can print a tree in this form using
   AstTreeStringPrinter.printJavaAndJavadocTree(File file))
  */
  @Override
  public void visitJavadocToken(DetailNode detailNode) {
    DetailNode textNode = JavadocUtil.findFirstToken(detailNode, JavadocTokenTypes.DESCRIPTION);
    if (textNode != null) {
      textNode = JavadocUtil.findFirstToken(textNode, JavadocTokenTypes.TEXT);
      if (textNode != null) {
        String text = textNode.getText();
        if (text != null
            && text.length() > 0
            && (text.charAt(text.length() - 1) == '.'
                || (text.charAt(0) <= 'Z') && (text.charAt(0) >= 'A'))) {
          log(detailNode.getLineNumber(), detailNode.getColumnNumber(), ERROR_MESSAGE);
        }
      }
    }
  }
}
