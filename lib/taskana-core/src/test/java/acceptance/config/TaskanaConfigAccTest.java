package acceptance.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Test;

import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;

/**
 * Test taskana configuration without roles.
 *
 * @author bbr
 */
public class TaskanaConfigAccTest extends TaskanaEngineImpl {

    public TaskanaConfigAccTest() throws SQLException {
        super(new TaskanaEngineConfiguration(TaskanaEngineConfigurationTest.getDataSource(), true,
            TaskanaEngineConfigurationTest.getSchemaName()));
    }

    @Test
    public void testDomains() {
        assertEquals(2, getConfiguration().getDomains().size());
        assertTrue(getConfiguration().getDomains().contains("DOMAIN_A"));
        assertTrue(getConfiguration().getDomains().contains("DOMAIN_B"));
        assertFalse(getConfiguration().getDomains().contains("Domain_A"));
    }

    @Test
    public void testClassificationTypes() {
        assertEquals(2, getConfiguration().getClassificationTypes().size());
        assertTrue(getConfiguration().getClassificationTypes().contains("TASK"));
        assertTrue(getConfiguration().getClassificationTypes().contains("DOCUMENT"));
        assertFalse(getConfiguration().getClassificationTypes().contains("document"));
    }

    @Test
    public void testClassificationCategories() {
        assertEquals(4, getConfiguration().getClassificationCategoriesByType("TASK").size());
        assertTrue(getConfiguration().getClassificationCategoriesByType("TASK").contains("EXTERNAL"));
        assertTrue(getConfiguration().getClassificationCategoriesByType("TASK").contains("MANUAL"));
        assertTrue(getConfiguration().getClassificationCategoriesByType("TASK").contains("AUTOMATIC"));
        assertTrue(getConfiguration().getClassificationCategoriesByType("TASK").contains("PROCESS"));
        assertFalse(getConfiguration().getClassificationCategoriesByType("TASK").contains("manual"));
    }

}
