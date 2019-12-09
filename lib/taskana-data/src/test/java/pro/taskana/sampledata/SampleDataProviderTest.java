package pro.taskana.sampledata;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test SampleDataGenerator.
 */
class SampleDataProviderTest {

    @Test
    void getScriptsNotNull() {
        Assertions.assertNotNull(SampleDataProvider.getDefaultScripts());
        Assertions.assertNotNull(SampleDataProvider.getScriptsWithEvents());
    }

    @Test
    void getScriptsNotEmpty() {
        Assertions.assertTrue(SampleDataProvider.getDefaultScripts().count() > 0);
        Assertions.assertTrue(SampleDataProvider.getScriptsWithEvents().count() > 0);
    }

    @Test
    void getScriptsFileExists() {
        SampleDataProvider.getDefaultScripts()
            .map(SQLReplacer::getScriptBufferedStream)
            .forEach(Assertions::assertNotNull);
    }

}
