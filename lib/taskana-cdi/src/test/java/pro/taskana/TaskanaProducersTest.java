package pro.taskana;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;
import org.wildfly.swarm.undertow.WARArchive;

@RunWith(Arquillian.class)
public class TaskanaProducersTest {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() throws Exception {
        WARArchive deployment = ShrinkWrap.create(WARArchive.class);
        deployment.addPackage("pro.taskana");
        deployment.addClass(TaskanaProducers.class);
        deployment.addAllDependencies();
        deployment.addDependency("org.mybatis:mybatis:3.4.2");
        deployment.addDependency("org.mybatis:mybatis-cdi:1.0.0");
        deployment.addDependency("pro.taskana:taskana-core:1.0.4-SNAPSHOT");
        deployment.addAsResource("META-INF/beans.xml");
        deployment.addAsResource("taskana.properties");
        deployment.addAsResource("project-defaults.yml");
        return deployment;
    }

    @CreateSwarm
    public static Swarm newContainer() throws Exception {
        Swarm swarm = new Swarm();
        return swarm;
    }

    @Test
    public void testCommit() throws SQLException, ClassNotFoundException {

        Client client = ClientBuilder.newClient();
        client.target("http://127.0.0.1:8090/rest/test").request().get();

        Class.forName("org.h2.Driver");
        int resultCount = 0;
        try (Connection conn = DriverManager.getConnection(
            "jdbc:h2:~/taskana-h2-data/testdb;AUTO_SERVER=TRUE;IGNORECASE=TRUE;LOCK_MODE=0",
            "SA", "SA")) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT ID, OWNER FROM TASKANA.TASK");

            while (rs.next()) {
                resultCount++;
            }
        }
        Assert.assertEquals(0, resultCount);
    }

    @Test
    public void testRollback() throws SQLException, ClassNotFoundException {
        Client client = ClientBuilder.newClient();
        client.target("http://127.0.0.1:8090/rest/test").request().post(null);

        Class.forName("org.h2.Driver");
        int resultCount = 0;
        try (Connection conn = DriverManager.getConnection(
            "jdbc:h2:~/taskana-h2-data/testdb;AUTO_SERVER=TRUE;IGNORECASE=TRUE;LOCK_MODE=0",
            "SA", "SA")) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT ID, OWNER FROM TASKANA.TASK");

            while (rs.next()) {
                resultCount++;
            }
        }

        Assert.assertEquals(0, resultCount);
    }
}
