package acceptance.config;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Set;

import org.h2.store.fs.FileUtils;
import org.junit.Test;

import pro.taskana.TaskanaRole;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;

/**
 * Test taskana's role configuration.
 *
 * @author bbr
 */
public class TaskanaRoleConfigAccTest extends TaskanaEngineImpl {

    public TaskanaRoleConfigAccTest() throws SQLException {
        super(new TaskanaEngineConfiguration(TaskanaEngineConfigurationTest.getDataSource(), true,
            TaskanaEngineConfigurationTest.getSchemaName()));
    }

    @Test
    public void testStandardConfig() {
        Set<TaskanaRole> rolesConfigured = getConfiguration().getRoleMap().keySet();
        assertTrue(rolesConfigured.contains(TaskanaRole.ADMIN));
        assertTrue(rolesConfigured.contains(TaskanaRole.BUSINESS_ADMIN));
        assertTrue(rolesConfigured.contains(TaskanaRole.USER));

        Set<String> users = getConfiguration().getRoleMap().get(TaskanaRole.USER);
        assertTrue(users.contains("user_1_1"));
        assertTrue(users.contains("user_1_2"));

        Set<String> admins = getConfiguration().getRoleMap().get(TaskanaRole.ADMIN);
        assertTrue(admins.contains("name=konrad,organisation=novatec"));
        assertTrue(admins.contains("admin"));

        Set<String> businessAdmins = getConfiguration().getRoleMap().get(TaskanaRole.BUSINESS_ADMIN);
        assertTrue(businessAdmins.contains("max"));
        assertTrue(businessAdmins.contains("moritz"));

        Set<String> monitorAccessIds = getConfiguration().getRoleMap().get(TaskanaRole.MONITOR);
        assertTrue(monitorAccessIds.contains("john"));
        assertTrue(monitorAccessIds.contains("teamlead_2"));
        assertTrue(monitorAccessIds.contains("monitor"));

    }

    @Test
    public void testOtherConfigFileSameDelimiter() throws IOException {
        String propertiesFileName = createNewConfigFileWithSameDelimiter("/dummyTestConfig.properties");
        try {
            getConfiguration().initTaskanaProperties(propertiesFileName, "|");

            Set<TaskanaRole> rolesConfigured = getConfiguration().getRoleMap().keySet();
            assertTrue(rolesConfigured.contains(TaskanaRole.ADMIN));
            assertTrue(rolesConfigured.contains(TaskanaRole.BUSINESS_ADMIN));
            assertTrue(rolesConfigured.contains(TaskanaRole.USER));

            Set<String> users = getConfiguration().getRoleMap().get(TaskanaRole.USER);
            assertTrue(users.contains("nobody"));

            Set<String> admins = getConfiguration().getRoleMap().get(TaskanaRole.ADMIN);
            assertTrue(admins.contains("holger"));
            assertTrue(admins.contains("stefan"));

            Set<String> businessAdmins = getConfiguration().getRoleMap().get(TaskanaRole.BUSINESS_ADMIN);
            assertTrue(businessAdmins.contains("ebe"));
            assertTrue(businessAdmins.contains("konstantin"));
        } finally {
            deleteFile(propertiesFileName);
        }

    }

    @Test
    public void testOtherConfigFileDifferentDelimiter() throws IOException {
        String delimiter = ";";
        String propertiesFileName = createNewConfigFileWithDifferentDelimiter("/dummyTestConfig.properties", delimiter);
        try {
            getConfiguration().initTaskanaProperties(propertiesFileName, delimiter);

            Set<TaskanaRole> rolesConfigured = getConfiguration().getRoleMap().keySet();
            assertTrue(rolesConfigured.contains(TaskanaRole.ADMIN));
            assertTrue(rolesConfigured.contains(TaskanaRole.BUSINESS_ADMIN));
            assertTrue(rolesConfigured.contains(TaskanaRole.USER));

            Set<String> users = getConfiguration().getRoleMap().get(TaskanaRole.USER);
            assertTrue(users.isEmpty());

            Set<String> admins = getConfiguration().getRoleMap().get(TaskanaRole.ADMIN);
            assertTrue(admins.contains("holger"));
            assertTrue(admins.contains("name=stefan,organisation=novatec"));

            Set<String> businessAdmins = getConfiguration().getRoleMap().get(TaskanaRole.BUSINESS_ADMIN);
            assertTrue(businessAdmins.contains("name=ebe, ou = bpm"));
            assertTrue(businessAdmins.contains("konstantin"));
        } finally {
            deleteFile(propertiesFileName);
        }

    }

    private String createNewConfigFileWithDifferentDelimiter(String filename, String delimiter) throws IOException {
        String userHomeDirectroy = System.getProperty("user.home");
        String propertiesFileName = userHomeDirectroy + filename;
        File f = new File(propertiesFileName);
        if (!f.exists()) {
            try (PrintWriter writer = new PrintWriter(propertiesFileName, "UTF-8")) {
                writer.println("taskana.roles.Admin =hOlGeR " + delimiter + "name=Stefan,Organisation=novatec");
                writer.println("  taskana.roles.businessadmin  = name=ebe, ou = bpm " + delimiter + " konstantin ");
                writer.println(" taskana.roles.user = ");
            } catch (IOException e) {
                throw e;
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

    private String createNewConfigFileWithSameDelimiter(String filename) throws IOException {
        String userHomeDirectroy = System.getProperty("user.home");
        String propertiesFileName = userHomeDirectroy + filename;
        File f = new File(propertiesFileName);
        if (!f.exists()) {
            try (PrintWriter writer = new PrintWriter(propertiesFileName, "UTF-8")) {
                writer.println("taskana.roles.Admin =hOlGeR|Stefan");
                writer.println("  taskana.roles.businessadmin  = ebe  | konstantin ");
                writer.println(" taskana.roles.user = nobody");
            } catch (IOException e) {
                throw e;
            }
        }
        return propertiesFileName;
    }

}
