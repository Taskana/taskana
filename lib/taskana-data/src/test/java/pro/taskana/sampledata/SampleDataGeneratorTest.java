package pro.taskana.sampledata;

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

}
