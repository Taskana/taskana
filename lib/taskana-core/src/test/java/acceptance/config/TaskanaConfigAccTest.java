package acceptance.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.jupiter.api.Test;

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

    @Test
    public void testDoesNotExistPropertyClassificationTypeOrItIsEmpty() throws IOException {
        taskanaEngineConfiguration.setClassificationTypes(new ArrayList<>());
        String propertiesFileName = createNewConfigFile("/dummyTestConfig.properties", false, true);
        String delimiter = ";";
        try {
            getConfiguration().initTaskanaProperties(propertiesFileName, delimiter);
        } finally {
            deleteFile(propertiesFileName);
        }
        assertTrue(taskanaEngineConfiguration.getClassificationTypes().isEmpty());
    }

    @Test
    public void testDoesNotExistPropertyClassificatioCategoryOrItIsEmpty() throws IOException {
        taskanaEngineConfiguration.setClassificationTypes(new ArrayList<>());
        taskanaEngineConfiguration.setClassificationCategoriesByType(new HashMap<>());
        String propertiesFileName = createNewConfigFile("/dummyTestConfig.properties", true, false);
        String delimiter = ";";
        try {
            getConfiguration().initTaskanaProperties(propertiesFileName, delimiter);
        } finally {
            deleteFile(propertiesFileName);
        }
        assertNull(taskanaEngineConfiguration.getClassificationCategoriesByType(
            taskanaEngineConfiguration.getClassificationTypes().get(0)));
    }

    @Test
    public void testWithCategoriesAndClassificationFilled() throws IOException {
        taskanaEngineConfiguration.setClassificationTypes(new ArrayList<String>());
        taskanaEngineConfiguration.setClassificationCategoriesByType(new HashMap<String, List<String>>());
        String propertiesFileName = createNewConfigFile("/dummyTestConfig.properties", true, true);
        String delimiter = ";";
        try {
            getConfiguration().initTaskanaProperties(propertiesFileName, delimiter);
        } finally {
            deleteFile(propertiesFileName);
        }
        assertFalse(taskanaEngineConfiguration.getClassificationTypes().isEmpty());
        assertFalse(taskanaEngineConfiguration.getClassificationCategoriesByType(
            taskanaEngineConfiguration.getClassificationTypes().get(0)).isEmpty());
        assertEquals(taskanaEngineConfiguration.getClassificationTypes().size(), 2);
        assertEquals(taskanaEngineConfiguration.getClassificationCategoriesByType(
            taskanaEngineConfiguration.getClassificationTypes().get(0)).size(), 4);
        assertEquals(taskanaEngineConfiguration.getClassificationCategoriesByType(
            taskanaEngineConfiguration.getClassificationTypes().get(1)).size(), 1);
    }

    private String createNewConfigFile(String filename, boolean addingTypes, boolean addingClassification)
        throws IOException {
        String userHomeDirectroy = System.getProperty("user.home");
        String propertiesFileName = userHomeDirectroy + filename;
        File f = new File(propertiesFileName);
        if (!f.exists()) {
            try (PrintWriter writer = new PrintWriter(propertiesFileName, "UTF-8")) {
                writer.println("taskana.roles.Admin =Holger|Stefan");
                writer.println("taskana.roles.businessadmin  = ebe  | konstantin ");
                writer.println("taskana.roles.user = nobody");
                if (addingTypes) {
                    writer.println("taskana.classification.types= TASK , document");
                }
                if (addingClassification) {
                    writer.println("taskana.classification.categories.task= EXTERNAL, manual, autoMAtic, Process");
                    writer.println("taskana.classification.categories.document= EXTERNAL");
                }
            }
        }
        return propertiesFileName;
    }

    private void deleteFile(String propertiesFileName) {
        System.out.println("about to delete " + propertiesFileName);
        File f = new File(propertiesFileName);
        if (f.exists() && !f.isDirectory()) {
            FileUtils.delete(propertiesFileName);
        }
    }

}
