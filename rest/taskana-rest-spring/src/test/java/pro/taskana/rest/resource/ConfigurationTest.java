package pro.taskana.rest.resource;

import org.junit.Test;
import pro.taskana.configuration.TaskanaEngineConfiguration;

import static org.junit.Assert.assertNotNull;

/**
 * Tests for Taskana lib configuration.
 */
public class ConfigurationTest {

    @Test
    public void testImplementationVersionIsInTaskanaCorePackage() {
        assertNotNull(TaskanaEngineConfiguration.class.getPackage().getImplementationVersion());
    }
}
