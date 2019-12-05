package pro.taskana.sampledata;

import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDateTime;
import java.util.regex.Matcher;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test SampleDataGenerator.
 */
class SampleDataGeneratorTest {

    @Test
    void getScriptsNotNull() {
        Assertions.assertNotNull(SampleDataGenerator.getDefaultScripts());
    }

    @Test
    void getScriptsNotEmpty() {
        Assertions.assertTrue(SampleDataGenerator.getDefaultScripts().count() > 0);
    }

    @Test
    void getScriptsFileExists() {
        SampleDataGenerator.getDefaultScripts()
            .map(SampleDataGenerator::getScriptBufferedStream)
            .forEach(Assertions::assertNotNull);
    }

    @Test
    void replaceRelativeTimeFunctionSameDate() {
        LocalDateTime now = LocalDateTime.now();
        String dateFormatted = now.format(SampleDataGenerator.DATE_TIME_FORMATTER);
        String sqlStringReplaced = SampleDataGenerator.replaceDatePlaceholder(now,
            "... RELATIVE_DATE(0) ...");
        assertThat(sqlStringReplaced, CoreMatchers.containsString(dateFormatted));
    }

    @Test
    void testDateRegex() {

        Assertions.assertTrue(SampleDataGenerator.RELATIVE_DATE_PATTERN.matcher("RELATIVE_DATE(123)").matches());

        Assertions.assertTrue(SampleDataGenerator.RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE(5) ...").find());
        Assertions.assertTrue(SampleDataGenerator.RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE(0) ...").find());
        Assertions.assertTrue(SampleDataGenerator.RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE(-123) ...").find());

        Assertions.assertFalse(SampleDataGenerator.RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE() ...").find());
        Assertions.assertFalse(
            SampleDataGenerator.RELATIVE_DATE_PATTERN.matcher("... RELATIVE_DATE(ABCDE) ...").find());
        Assertions.assertFalse(SampleDataGenerator.RELATIVE_DATE_PATTERN.matcher("... RELATIVE_NO(5) ...").find());
        Assertions.assertFalse(SampleDataGenerator.RELATIVE_DATE_PATTERN.matcher("...").find());
    }

    @Test
    void testDateRegexExtractGroup() {
        Matcher matcher = SampleDataGenerator.RELATIVE_DATE_PATTERN.matcher("RELATIVE_DATE(123)");
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("123", matcher.group(1));
    }

    @Test
    void replaceRelativeTimeFunctionPosDate() {
        LocalDateTime now = LocalDateTime.now();
        String dateFormatted = now.plusDays(5).format(SampleDataGenerator.DATE_TIME_FORMATTER);
        String sqlStringReplaced = SampleDataGenerator.replaceDatePlaceholder(now,
            "... RELATIVE_DATE(5) ...");
        assertThat(sqlStringReplaced, CoreMatchers.containsString(dateFormatted));
    }

    @Test
    void replaceRelativeTimeFunctionNegDate() {
        LocalDateTime now = LocalDateTime.now();
        String dateFormatted = now.plusDays(-10).format(SampleDataGenerator.DATE_TIME_FORMATTER);
        String sqlStringReplaced = SampleDataGenerator.replaceDatePlaceholder(now,
            "... RELATIVE_DATE(-10) ...");
        assertThat(sqlStringReplaced, CoreMatchers.containsString(dateFormatted));
    }

}
