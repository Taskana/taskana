package pro.taskana.sampledata;

import java.util.stream.Stream;

/** Provides a sample data set. */
public final class SampleDataProvider {

  static final String TEST_TASK = "/sql/test-data/task.sql";
  static final String TEST_TASK_COMMENT = "/sql/test-data/task-comment.sql";
  static final String TEST_WORKBASKET = "/sql/test-data/workbasket.sql";
  static final String TEST_DISTRIBUTION_TARGETS = "/sql/test-data/distribution-targets.sql";
  static final String TEST_WORKBASKET_ACCESS_LIST = "/sql/test-data/workbasket-access-list.sql";
  static final String TEST_CLASSIFICATION = "/sql/test-data/classification.sql";
  static final String TEST_OBJECT_REFERENCE = "/sql/test-data/object-reference.sql";
  static final String TEST_ATTACHMENT = "/sql/test-data/attachment.sql";
  static final String TEST_TASK_HISTORY_EVENT = "/sql/test-data/task-history-event.sql";
  static final String TEST_WORKBASKET_HISTORY_EVENT = "/sql/test-data/workbasket-history-event.sql";
  static final String TEST_CLASSIFICATION_HISTORY_EVENT =
      "/sql/test-data/classification-history-event.sql";
  static final String MONITOR_SAMPLE_DATA = "/sql/monitor-data/monitor-sample-data.sql";
  private static final String DB_CLEAR_TABLES_SCRIPT = "/sql/clear/clear-db.sql";
  private static final String DB_DROP_TABLES_SCRIPT = "/sql/clear/drop-tables.sql";
  private static final String SAMPLE_TASK_HISTORY_EVENT = "/sql/sample-data/task-history-event.sql";
  private static final String SAMPLE_TASK = "/sql/sample-data/task.sql";
  private static final String SAMPLE_TASK_COMMENT = "/sql/sample-data/task-comment.sql";
  private static final String SAMPLE_WORKBASKET = "/sql/sample-data/workbasket.sql";
  private static final String SAMPLE_DISTRIBUTION_TARGETS =
      "/sql/sample-data/distribution-targets.sql";
  private static final String SAMPLE_WORKBASKET_ACCESS_LIST =
      "/sql/sample-data/workbasket-access-list.sql";
  private static final String SAMPLE_CLASSIFICATION = "/sql/sample-data/classification.sql";
  private static final String SAMPLE_OBJECT_REFERENCE = "/sql/sample-data/object-reference.sql";
  private static final String SAMPLE_ATTACHMENT = "/sql/sample-data/attachment.sql";

  private SampleDataProvider() {}

  static Stream<String> getSampleDataCreationScripts() {
    return Stream.of(
        SAMPLE_WORKBASKET,
        SAMPLE_DISTRIBUTION_TARGETS,
        SAMPLE_CLASSIFICATION,
        SAMPLE_TASK,
        SAMPLE_TASK_COMMENT,
        SAMPLE_ATTACHMENT,
        SAMPLE_WORKBASKET_ACCESS_LIST,
        SAMPLE_OBJECT_REFERENCE,
        SAMPLE_TASK_HISTORY_EVENT);
  }

  static Stream<String> getScriptsToClearDatabase() {
    return Stream.of(DB_CLEAR_TABLES_SCRIPT);
  }

  static Stream<String> getScriptsToDropDatabase() {
    return Stream.of(DB_DROP_TABLES_SCRIPT);
  }

  static Stream<String> getTestDataScripts() {
    return Stream.of(
        TEST_CLASSIFICATION,
        TEST_WORKBASKET,
        TEST_TASK,
        TEST_TASK_HISTORY_EVENT,
        TEST_WORKBASKET_HISTORY_EVENT,
        TEST_CLASSIFICATION_HISTORY_EVENT,
        TEST_TASK_COMMENT,
        TEST_WORKBASKET_ACCESS_LIST,
        TEST_DISTRIBUTION_TARGETS,
        TEST_OBJECT_REFERENCE,
        TEST_ATTACHMENT);
  }

  static Stream<String> getMonitorDataScripts() {
    return Stream.of(MONITOR_SAMPLE_DATA);
  }
}
