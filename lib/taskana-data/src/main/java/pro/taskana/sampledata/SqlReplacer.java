package pro.taskana.sampledata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
  static final DateTimeFormatter DATE_TIME_PARSER =  
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  
  private static Pattern DATE_TIME_PATTERN_REGEX = Pattern.compile(
      "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}");

  private static Pattern CURRENT_TIMESTAMP_PATTERN_REGEX = Pattern.compile(
      "CURRENT_TIMESTAMP");
  
  private SqlReplacer() {}

  static String getScriptAsSql(String dbProductName, LocalDateTime now, String scriptPath) {
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
   * <p>This method must be invoked before replaceDateTimeStrings().
   *
   * @param now anchor for relative date conversion.
   * @param sql sql statement which may contain the above declared custom function.
   * @return sql statement with the given function resolved, if the 'sql' parameter contained any.
   */
  static String replaceDatePlaceholder(LocalDateTime now, String sql) {
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
  
  /**
   * This method replaces the 'CURRENT_TIMESTAMP' value by its DateTimeString representation.
   * This method must be invoked before replaceDateTimeStrings().

   * @param sql  Statement the may contain DateTimeStrings like '2018-01-29 15:55:00'
   * @return the same sql statement with the DateTimeStrings now in UTC time zone.
   */
  
  static String replaceCurrentTimestamp(String sql) {
    Matcher m = CURRENT_TIMESTAMP_PATTERN_REGEX.matcher(sql);
    StringBuffer sb = new StringBuffer(sql.length());
    while (m.find()) {
      String dateTimeString = "'" + DATE_TIME_PARSER.format(LocalDateTime.now()) + "'";      
      m.appendReplacement(sb, dateTimeString);
    }
    m.appendTail(sb);
    return sb.toString();
  }
  
  
  /**
   * This method replaces the DateTimeStrings by their equivalent Value in the UTC zone.
   * @param sql  Statement the may contain DateTimeStrings like '2018-01-29 15:55:00'
   * @return the same sql statement with the DateTimeStrings now in UTC time zone.
   */
  static String replaceDateTimeStringsByTheirUtCValue(String sql) {
    Matcher m = DATE_TIME_PATTERN_REGEX.matcher(sql);
    StringBuffer sb = new StringBuffer(sql.length());
    while (m.find()) {
      String dateTimeString = m.group(0);
      String result = toUtc(dateTimeString);
      m.appendReplacement(sb, result);
    }
    m.appendTail(sb);
    return sb.toString();
  }

  /**
   * Converts a date string from local timezone into the same instant expressed in UTC zone. 
   * @param date the date String in local timezone. 
   * @return the date String in the utc time zone.
   */
  static String toUtc(String date) {
    LocalDateTime locatDateTime = LocalDateTime.from(DATE_TIME_PARSER.parse(date));
    ZonedDateTime zonedAtLocalZone = locatDateTime.atZone(ZoneId.systemDefault());
    ZonedDateTime zonedAtUtc = zonedAtLocalZone.withZoneSameInstant(ZoneId.of("UTC"));
    return DATE_TIME_PARSER.format(zonedAtUtc);  
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
      BufferedReader bufferedReader, LocalDateTime now, String dbProductname) {
    boolean isDb2 = isDb2(dbProductname);
    String sql = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    if (isDb2) {
      sql = replaceBooleanWithInteger(sql);
    }
    sql = replaceCurrentTimestamp(sql);
    sql = replaceDatePlaceholder(now, sql);
    return replaceDateTimeStringsByTheirUtCValue(sql);
  }

  private static String formatToSqlDate(LocalDateTime now, long days) {
    return "'" + now.plusDays(days).format(DATE_TIME_FORMATTER) + "'";
  }
}
