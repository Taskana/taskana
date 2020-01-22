package pro.taskana.sampledata;

import static org.hamcrest.MatcherAssert.assertThat;
import static pro.taskana.sampledata.SqlReplacer.DATE_TIME_FORMATTER;
import static pro.taskana.sampledata.SqlReplacer.RELATIVE_DATE_PATTERN;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test SampleDataGenerator. */
class SqlReplacerTest {

  @Test
  void replaceRelativeTimeFunctionSameDate() {
    ZonedDateTime now = Instant.now().atZone(ZoneId.of("UTC"));
    String dateFormatted = now.format(DATE_TIME_FORMATTER);
    String sqlStringReplaced = SqlReplacer.replaceDatePlaceholder(now, "... RELATIVE_DATE(0) ...");
    assertThat(sqlStringReplaced, CoreMatchers.containsString(dateFormatted));
  }

  @Test
  void testDateRegex() {

    Assertions.assertTrue(RELATIVE_DATE_PATTERN.matcher("RELATIVE_DATE(123)").matches());

    Assertions.assertTrue(RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE(5) ...").find());
    Assertions.assertTrue(RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE(0) ...").find());
    Assertions.assertTrue(RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE(-123) ...").find());

    Assertions.assertFalse(RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE() ...").find());
    Assertions.assertFalse(RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE(ABCDE) ...").find());
    Assertions.assertFalse(RELATIVE_DATE_PATTERN.matcher("... RELATIVE_NO(5) ...").find());
    Assertions.assertFalse(RELATIVE_DATE_PATTERN.matcher("...").find());
  }

  @Test
  void testDateRegexExtractGroup() {
    Matcher matcher = RELATIVE_DATE_PATTERN.matcher("RELATIVE_DATE(123)");
    Assertions.assertTrue(matcher.find());
    Assertions.assertEquals("123", matcher.group(1));
  }

  @Test
  void replaceRelativeTimeFunctionPosDate() {
    ZonedDateTime now = Instant.now().atZone(ZoneId.of("UTC"));
    String dateFormatted = now.plusDays(5).format(DATE_TIME_FORMATTER);
    String sqlStringReplaced = SqlReplacer.replaceDatePlaceholder(now, "... RELATIVE_DATE(5) ...");
    assertThat(sqlStringReplaced, CoreMatchers.containsString(dateFormatted));
  }

  @Test
  void replaceRelativeTimeFunctionNegDate() {
    ZonedDateTime now = Instant.now().atZone(ZoneId.of("UTC"));
    String dateFormatted = now.plusDays(-10).format(DATE_TIME_FORMATTER);
    String sqlStringReplaced =
        SqlReplacer.replaceDatePlaceholder(now, "... RELATIVE_DATE(-10) ...");
    assertThat(sqlStringReplaced, CoreMatchers.containsString(dateFormatted));
  }
}
