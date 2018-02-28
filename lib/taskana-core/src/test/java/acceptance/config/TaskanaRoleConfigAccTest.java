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
        super(new TaskanaEngineConfiguration(TaskanaEngineConfigurationTest.getDataSource(), true));
    }

    @Test
    public void testStandardConfig() {
        Set<TaskanaRole> rolesConfigured = super.roleMap.keySet();
        assertTrue(rolesConfigured.contains(TaskanaRole.ADMIN));
        assertTrue(rolesConfigured.contains(TaskanaRole.BUSINESS_ADMIN));
        assertTrue(rolesConfigured.contains(TaskanaRole.USER));

        Set<String> users = roleMap.get(TaskanaRole.USER);
        assertTrue(users.contains("user_1_1"));
        assertTrue(users.contains("user_1_2"));

        Set<String> admins = roleMap.get(TaskanaRole.ADMIN);
        assertTrue(admins.contains("name=konrad,organisation=novatec"));
        assertTrue(admins.contains("admin"));

        Set<String> businessAdmins = roleMap.get(TaskanaRole.BUSINESS_ADMIN);
        assertTrue(businessAdmins.contains("max"));
        assertTrue(businessAdmins.contains("moritz"));

    }

    @Test
    public void testOtherConfigFileSameDelimiter() throws IOException, SQLException {
        String propertiesFileName = createNewConfigFileWithSameDelimiter("/dummyTestConfig.properties");
        try {
            initRoles(propertiesFileName, null);

            Set<TaskanaRole> rolesConfigured = super.roleMap.keySet();
            assertTrue(rolesConfigured.contains(TaskanaRole.ADMIN));
            assertTrue(rolesConfigured.contains(TaskanaRole.BUSINESS_ADMIN));
            assertTrue(rolesConfigured.contains(TaskanaRole.USER));

            Set<String> users = roleMap.get(TaskanaRole.USER);
            assertTrue(users.contains("nobody"));

            Set<String> admins = roleMap.get(TaskanaRole.ADMIN);
            assertTrue(admins.contains("holger"));
            assertTrue(admins.contains("stefan"));

            Set<String> businessAdmins = roleMap.get(TaskanaRole.BUSINESS_ADMIN);
            assertTrue(businessAdmins.contains("ebe"));
            assertTrue(businessAdmins.contains("konstantin"));
        } finally {
            deleteFile(propertiesFileName);
        }

    }

    @Test
    public void testOtherConfigFileDifferentDelimiter() throws IOException, SQLException {
        String delimiter = ";";
        String propertiesFileName = createNewConfigFileWithDifferentDelimiter("/dummyTestConfig.properties", delimiter);
        try {
            initRoles(propertiesFileName, delimiter);

            Set<TaskanaRole> rolesConfigured = super.roleMap.keySet();
            assertTrue(rolesConfigured.contains(TaskanaRole.ADMIN));
            assertTrue(rolesConfigured.contains(TaskanaRole.BUSINESS_ADMIN));
            assertTrue(rolesConfigured.contains(TaskanaRole.USER));

            Set<String> users = roleMap.get(TaskanaRole.USER);
            assertTrue(users.isEmpty());

            Set<String> admins = roleMap.get(TaskanaRole.ADMIN);
            assertTrue(admins.contains("holger"));
            assertTrue(admins.contains("name=stefan,organisation=novatec"));

            Set<String> businessAdmins = roleMap.get(TaskanaRole.BUSINESS_ADMIN);
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
