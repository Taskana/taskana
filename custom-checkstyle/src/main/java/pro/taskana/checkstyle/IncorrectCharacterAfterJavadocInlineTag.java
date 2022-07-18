package pro.taskana.checkstyle;

import com.puppycrawl.tools.checkstyle.StatelessCheck;
import com.puppycrawl.tools.checkstyle.api.DetailNode;
import com.puppycrawl.tools.checkstyle.api.JavadocTokenTypes;
import com.puppycrawl.tools.checkstyle.checks.javadoc.AbstractJavadocCheck;
import com.puppycrawl.tools.checkstyle.utils.JavadocUtil;
import java.util.Set;

/**
 * The IncorrectCharacterAfterJavadocInlineTag reports a checkstyle violation for each inline tag
 * that is followed by an unacceptable character. The only acceptable characters are:
 *
 * <p>{@code ' ', ',', '.', ';', '\n', '<', '-'}
 *
 * <p>Example:
 *
 * <p>- {@code {@linkplain Task}s} is invalid. Use {@code {@linkplain Task Tasks}}.
 *
 * <p>- {@code {@code some}Text} is invalid. Use {@code {@code some} Text} instead.
 */
@StatelessCheck
public class IncorrectCharacterAfterJavadocInlineTag extends AbstractJavadocCheck {

  public static final Set<Character> ACCEPTABLE_CHARS = Set.of(' ', ',', '.', ';', '\n', '<', '-');
  private static final String ERROR_MESSAGE =
      "Javadoc inline tag should be followed by one of the "
          + "following characters: ' ' , ',' , '.' , ';' , '\\n' , '<' , '-' ";

  @Override
  public int[] getDefaultJavadocTokens() {
    return new int[] {
      JavadocTokenTypes.JAVADOC_INLINE_TAG,
    };
  }

  @Override
  public int[] getRequiredJavadocTokens() {
    return getAcceptableJavadocTokens();
  }

  /**
   * Visits a token of type JAVADOC_INLINE_TAG and checks the next character after
   * JAVADOC_INLINE_TAG (first character of the next sibling).
   *
   * @param detailNode the token to process
   */
  /* Example extract from a valid Javadoc tree:
   `--JAVADOC -> JAVADOC [705:5]
    |       |--NEWLINE -> \n [705:5]
    |       |--LEADING_ASTERISK ->    * [706:0]
    |       |--TEXT ->  Terminates a  [706:4]
    |       |--JAVADOC_INLINE_TAG -> JAVADOC_INLINE_TAG [706:18]
    |       |   |--JAVADOC_INLINE_TAG_START -> { [706:18]
    |       |   |--LINKPLAIN_LITERAL -> @linkplain [706:19]
    |       |   |--WS ->   [706:29]
    |       |   |--REFERENCE -> REFERENCE [706:30]
    |       |   |   `--PACKAGE_CLASS -> Task [706:30]
    |       |   `--JAVADOC_INLINE_TAG_END -> } [706:34]
    |       |--TEXT -> . Termination is an administrative action to complete a [706:35]
    |       |--NEWLINE -> \n [706:90]
   ...

   You can print a tree in this form using
   AstTreeStringPrinter.printJavaAndJavadocTree(File file))
  */
  @Override
  public void visitJavadocToken(DetailNode detailNode) {
    DetailNode textNode = JavadocUtil.getNextSibling(detailNode);
    if (textNode != null && textNode.getType() != JavadocTokenTypes.EOF) {
      if (!ACCEPTABLE_CHARS.contains(textNode.getText().charAt(0))) {
        log(textNode.getLineNumber(), textNode.getColumnNumber(), ERROR_MESSAGE);
      }
    }
  }
}
