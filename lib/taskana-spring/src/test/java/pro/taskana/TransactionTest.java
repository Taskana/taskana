package pro.taskana;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration("classpath:test-applicationContext.xml")
@EnableAutoConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionTest {

    @Autowired
    TaskService taskService;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int port;

    @Before
    public void init() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:task-engine;IGNORECASE=TRUE;LOCK_MODE=0", "SA",
            "SA")) {
            conn.createStatement().executeUpdate("DELETE FROM TASK WHERE 1=1");
            conn.commit();
        }
    }

    @Test
    @Ignore
    public void testCommit() throws SQLException {
        restTemplate.getForEntity("http://127.0.0.1:" + port + "/test", String.class);

        int resultCount = 0;
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:task-engine;IGNORECASE=TRUE;LOCK_MODE=0", "SA",
            "SA")) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT ID FROM TASK");

            while (rs.next()) {
                resultCount++;
            }
        }

        Assert.assertEquals(1, resultCount);
    }

    @Test
    @Ignore
    public void testRollback() throws SQLException {
        restTemplate.postForEntity("http://127.0.0.1:" + port + "/test", null, String.class);

        int resultCount = 0;
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:task-engine;IGNORECASE=TRUE;LOCK_MODE=0", "SA",
            "SA")) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT ID FROM TASK");

            while (rs.next()) {
                resultCount++;
            }
        }

        Assert.assertEquals(0, resultCount);
    }

}
