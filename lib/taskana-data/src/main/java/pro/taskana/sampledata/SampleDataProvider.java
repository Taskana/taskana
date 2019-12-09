package pro.taskana.sampledata;

import java.util.stream.Stream;

import org.apache.ibatis.jdbc.ScriptRunner;

/**
 * Provides a sample data set.
 */
public final class SampleDataProvider {

    private static final String CLEAR_HISTORY_EVENTS = "/sql/clear/clear-history-events.sql";
    private static final String HISTORY_EVENT = "/sql/sample-data/history-event.sql";
    private static final String CHECK_HISTORY_EVENT_EXIST = "/sql/sample-data/check-history-event-exist.sql";

    private static final String TASK = "/sql/sample-data/task.sql";
    private static final String WORKBASKET = "/sql/sample-data/workbasket.sql";
    private static final String DISTRIBUTION_TARGETS = "/sql/sample-data/distribution-targets.sql";
    private static final String WORKBASKET_ACCESS_LIST = "/sql/sample-data/workbasket-access-list.sql";
    private static final String CLASSIFICATION = "/sql/sample-data/classification.sql";
    private static final String OBJECT_REFERENCE = "/sql/sample-data/object-reference.sql";
    private static final String ATTACHMENT = "/sql/sample-data/attachment.sql";

    private SampleDataProvider() {
    }

    static Stream<String> getScripts() {
        return Stream.of(
            WORKBASKET, DISTRIBUTION_TARGETS, CLASSIFICATION, TASK, ATTACHMENT, WORKBASKET_ACCESS_LIST,
            OBJECT_REFERENCE);
    }

    static Stream<String> getScriptsWithEvents() {
        return Stream.concat(getScripts(), Stream.of(CLEAR_HISTORY_EVENTS, HISTORY_EVENT));
    }

    public static Stream<String> getDataProvider(ScriptRunner runner) {

        try {
            //TODO find a better method of testing if a table exists
            runner.runScript(SampleDataGenerator.getScriptBufferedStream(CHECK_HISTORY_EVENT_EXIST));
            return SampleDataProvider.getScriptsWithEvents();
        } catch (Exception e) {
            return SampleDataProvider.getScripts();
        }
    }
}
