package pro.taskana.sampledata;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.sampledata.SqlReplacer.DATE_TIME_FORMATTER;
import static pro.taskana.sampledata.SqlReplacer.RELATIVE_DATE_PATTERN;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import org.junit.jupiter.api.Test;

/** Test SampleDataGenerator. */
class SqlReplacerTest {

  @Test
  void replaceRelativeTimeFunctionSameDate() {
    ZonedDateTime now = Instant.now().atZone(ZoneId.of("UTC"));
    String dateFormatted = now.format(DATE_TIME_FORMATTER);
    String sqlStringReplaced = SqlReplacer.replaceDatePlaceholder(now, "... RELATIVE_DATE(0) ...");
    assertThat(sqlStringReplaced).contains(dateFormatted);
  }

  @Test
  void testDateRegex() {

    assertThat(RELATIVE_DATE_PATTERN.matcher("RELATIVE_DATE(123)").matches()).isTrue();

    assertThat(RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE(5) ...").find()).isTrue();
    assertThat(RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE(0) ...").find()).isTrue();
    assertThat(RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE(-123) ...").find()).isTrue();

    assertThat(RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE() ...").find()).isFalse();
    assertThat(RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE(ABCDE) ...").find()).isFalse();
    assertThat(RELATIVE_DATE_PATTERN.matcher("... RELATIVE_NO(5) ...").find()).isFalse();
    assertThat(RELATIVE_DATE_PATTERN.matcher("...").find()).isFalse();
  }

  @Test
  void testDateRegexExtractGroup() {
    Matcher matcher = RELATIVE_DATE_PATTERN.matcher("RELATIVE_DATE(123)");
    assertThat(matcher.find()).isTrue();
    assertThat(matcher.group(1)).isEqualTo("123");
  }

  @Test
  void replaceRelativeTimeFunctionPosDate() {
    ZonedDateTime now = Instant.now().atZone(ZoneId.of("UTC"));
    String dateFormatted = now.plusDays(5).format(DATE_TIME_FORMATTER);
    String sqlStringReplaced = SqlReplacer.replaceDatePlaceholder(now, "... RELATIVE_DATE(5) ...");

    assertThat(sqlStringReplaced).contains(dateFormatted);
  }

  @Test
  void replaceRelativeTimeFunctionNegDate() {
    ZonedDateTime now = Instant.now().atZone(ZoneId.of("UTC"));
    String dateFormatted = now.plusDays(-10).format(DATE_TIME_FORMATTER);
    String sqlStringReplaced =
        SqlReplacer.replaceDatePlaceholder(now, "... RELATIVE_DATE(-10) ...");
    assertThat(sqlStringReplaced).contains(dateFormatted);
  }
}
