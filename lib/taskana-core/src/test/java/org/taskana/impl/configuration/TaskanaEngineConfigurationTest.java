package org.taskana.impl.configuration;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import javax.security.auth.login.LoginException;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.taskana.TaskanaEngine;
import org.taskana.configuration.TaskanaEngineConfiguration;
/**
 * Integration Test for TaskanaEngineConfiguration.
 * @author EH
 */
public class TaskanaEngineConfigurationTest {

    @Test
    public void testCreateTaskEngine() throws FileNotFoundException, SQLException, LoginException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:workbasket-test-db");
        ds.setPassword("sa");
        ds.setUser("sa");
        TaskanaEngineConfiguration taskEngineConfiguration = new TaskanaEngineConfiguration(ds, false);

        TaskanaEngine te = taskEngineConfiguration.buildTaskanaEngine();

        Assert.assertNotNull(te);
    }
}
