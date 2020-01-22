package pro.taskana.sampledata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** This class replaces boolean values with int values if the database is db2. */
final class SqlReplacer {

  static final String RELATIVE_DATE_REGEX = "RELATIVE_DATE\\((-?\\d+)\\)";
  static final Pattern RELATIVE_DATE_PATTERN = Pattern.compile(RELATIVE_DATE_REGEX);
  static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  private SqlReplacer() {}

  static String getScriptAsSql(String dbProductName, ZonedDateTime now, String scriptPath) {
    return parseAndReplace(getScriptBufferedStream(scriptPath), now, dbProductName);
  }

  static boolean isPostgreSql(String databaseProductName) {
    return "PostgreSQL".equals(databaseProductName);
  }

  static boolean isDb2(String dbProductName) {
    return dbProductName != null && dbProductName.contains("DB2");
  }

  /**
   * This method resolves the custom sql function defined through this regex: {@value
   * RELATIVE_DATE_REGEX}. Its parameter is a digit representing the relative offset of a given
   * starting point date.
   *
   * <p>Yes, this can be done as an actual sql function, but that'd lead to a little more complexity
   * (and thus we'd have to maintain the code for db compatibility ...) Since we're already
   * replacing the boolean attributes of sql files this addition is not a huge computational cost.
   *
   * @param now anchor for relative date conversion.
   * @param sql sql statement which may contain the above declared custom function.
   * @return sql statement with the given function resolved, if the 'sql' parameter contained any.
   */
  static String replaceDatePlaceholder(ZonedDateTime now, String sql) {
    Matcher m = RELATIVE_DATE_PATTERN.matcher(sql);
    StringBuffer sb = new StringBuffer(sql.length());
    while (m.find()) {
      long daysToShift = Long.parseLong(m.group(1));
      String daysAsStringDate = formatToSqlDate(now, daysToShift);
      m.appendReplacement(sb, daysAsStringDate);
    }
    m.appendTail(sb);
    return sb.toString();
  }

  static BufferedReader getScriptBufferedStream(String script) {
    return Optional.ofNullable(SampleDataGenerator.class.getResourceAsStream(script))
        .map(
            inputStream ->
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
        .orElse(null);
  }

  static String getSanitizedTableName(String table) {
    return table.replaceAll("[^a-zA-Z0-9_]", "__");
  }

  private static String replaceBooleanWithInteger(String sql) {
    return sql.replaceAll("(?i)true", "1").replaceAll("(?i)false", "0");
  }

  private static String parseAndReplace(
      BufferedReader bufferedReader, ZonedDateTime now, String dbProductname) {
    boolean isDb2 = isDb2(dbProductname);
    String sql = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    if (isDb2) {
      sql = replaceBooleanWithInteger(sql);
    }
    return replaceDatePlaceholder(now, sql);
  }

  private static String formatToSqlDate(ZonedDateTime now, long days) {
    return "'" + now.plusDays(days).format(DATE_TIME_FORMATTER) + "'";
  }
}
