package pro.taskana.sampledata;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test SampleDataGenerator.
 */
class SampleDataGeneratorTest {

    @Test
    void getScriptsNotEmpty() {
        ArrayList<String> scripts = SampleDataGenerator.getDefaultScripts();
        Assertions.assertNotNull(scripts);
        Assertions.assertTrue(scripts.size() > 0);
    }

    @Test
    void getScriptsFileExists() {
        ArrayList<String> scripts = SampleDataGenerator.getDefaultScripts();
        for (String script : scripts) {
            Assertions.assertNotNull(SampleDataGenerator.getScriptBufferedStream(script));
        }

    }
}
