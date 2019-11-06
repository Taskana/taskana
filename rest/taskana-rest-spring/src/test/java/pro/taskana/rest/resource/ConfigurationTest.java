package pro.taskana.rest.resource;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

import pro.taskana.configuration.TaskanaEngineConfiguration;

/**
 * Tests for Taskana lib configuration.
 */
class ConfigurationTest {

    @Test
    void testImplementationVersionIsInTaskanaCorePackage() {
        assertNotNull(TaskanaEngineConfiguration.class.getPackage().getImplementationVersion());
    }
}
