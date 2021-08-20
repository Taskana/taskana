package pro.taskana.common.internal.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class LogSanitizerTest {

  @Test
  void should_NotModifyInput_When_NothingHasToBeStripped() {
    String input = "this is a regular text with some weird characters: | !\"§$%&(/)=?*'Ä#ä+";

    String output = LogSanitizer.stripLineBreakingChars(input);

    assertThat(output).isEqualTo(input);
  }

  @Test
  void should_CallToStringMethod_When_ObjectIsPassed() {
    class TestObject {
      @Override
      public String toString() {
        return "test string";
      }
    }

    Object input = new TestObject();

    String output = LogSanitizer.stripLineBreakingChars(input);

    assertThat(output).isEqualTo(input.toString());
  }

  @Test
  void should_ReplaceMultipleLineBreaksWithUnderscore_When_InputContainsLineBreaks() {
    String input = "This\nis\na\nstring\nwith\na\nlot\nof\nlinebreaks\n";

    String output = LogSanitizer.stripLineBreakingChars(input);

    assertThat(output).isEqualTo("This_is_a_string_with_a_lot_of_linebreaks_");
  }

  @Test
  void should_ReplaceMultipleTabsWithUnderscore_When_InputContainsTabs() {
    String input = "This\tis\ta\tstring\twith\ta\tlot\tof\ttabs\t";

    String output = LogSanitizer.stripLineBreakingChars(input);

    assertThat(output).isEqualTo("This_is_a_string_with_a_lot_of_tabs_");
  }

  @Test
  void should_ReplaceMultipleWindowsLineBreaksWithUnderscore_When_InputContainsWindowsLineBreaks() {
    String input = "This\r\nis\r\na\r\nstring\r\nwith\r\na\r\nlot\r\nof\r\nwindows\r\nlinebreaks";

    String output = LogSanitizer.stripLineBreakingChars(input);

    assertThat(output).isEqualTo("This__is__a__string__with__a__lot__of__windows__linebreaks");
  }

  @Test
  void should_ReplaceAllLinebreaksAndTabsWithUnderscore_When_InputContainsDifferentCharacters() {
    String input = "This\tis\r\na\nstring\twith\r\ntaps,\nlinebreaks\tand\r\nwindows\tlinebreaks";

    String output = LogSanitizer.stripLineBreakingChars(input);

    assertThat(output)
        .isEqualTo("This_is__a_string_with__taps,_linebreaks_and__windows_linebreaks");
  }
}
