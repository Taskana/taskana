package pro.taskana.common.internal.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

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

  @TestFactory
  Stream<DynamicTest> should_ReplaceWithUnderscore() {
    List<Triplet<String, String, String>> testValues =
        List.of(
            Triplet.of(
                "Replace linebreaks",
                "This\nis\na\nstring\nwith\na\nlot\nof\nlinebreaks\n",
                "This_is_a_string_with_a_lot_of_linebreaks_"),
            Triplet.of(
                "Replace windows linebreaks",
                "This\r\nis\r\na\r\nstring\r\nwith\r\na\r\nlot\r\nof\r\nwindows\r\nlinebreaks",
                "This__is__a__string__with__a__lot__of__windows__linebreaks"),
            Triplet.of(
                "Replace tabs",
                "This\tis\ta\tstring\twith\ta\tlot\tof\ttabs\t",
                "This_is_a_string_with_a_lot_of_tabs_"),
            Triplet.of(
                "Replace linebreaks, windows linebreaks and tabs",
                "This\tis\r\na\nstring\twith\r\ntaps,\nlinebreaks\tand\r\nwindows\tlinebreaks",
                "This_is__a_string_with__taps,_linebreaks_and__windows_linebreaks"));
    ThrowingConsumer<Triplet<String, String, String>> test =
        t -> {
          String output = LogSanitizer.stripLineBreakingChars(t.getMiddle());
          assertThat(output).isEqualTo(t.getRight());
        };
    return DynamicTest.stream(testValues.iterator(), Triplet::getLeft, test);
  }
}
