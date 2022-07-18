package pro.taskana.common.internal.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pro.taskana.task.api.TaskService;

/** The TransactionTest ... */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration("classpath:test-applicationContext.xml")
@EnableAutoConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Disabled
class TransactionTest {

  @Autowired TaskService taskService;
  @LocalServerPort int port;
  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  void init() throws Exception {
    Class.forName("org.h2.Driver");
    try (Connection conn = getConnection()) {
      try (Statement statement = conn.createStatement()) {
        statement.executeUpdate("DELETE FROM TASK WHERE 1=1");
        conn.commit();
      }
    }
  }

  @Test
  void testCommit() throws Exception {
    restTemplate.getForEntity("http://127.0.0.1:" + port + "/test", String.class);

    int resultCount = 0;
    try (Connection conn = getConnection()) {
      try (Statement statement = conn.createStatement()) {
        ResultSet rs = statement.executeQuery("SELECT ID FROM TASK");

        while (rs.next()) {
          resultCount++;
        }
      }
    }

    assertThat(resultCount).isOne();
  }

  @Test
  void testRollback() throws Exception {
    restTemplate.postForEntity("http://127.0.0.1:" + port + "/test", null, String.class);

    int resultCount = 0;
    try (Connection conn = getConnection()) {
      try (Statement statement = conn.createStatement()) {
        ResultSet rs = statement.executeQuery("SELECT ID FROM TASK");

        while (rs.next()) {
          resultCount++;
        }
      }

      assertThat(resultCount).isZero();
    }
  }

  private Connection getConnection() throws Exception {
    return DriverManager.getConnection(
        "jdbc:h2:mem:task-engine;IGNORECASE=TRUE;LOCK_MODE=0", "SA", UUID.randomUUID().toString());
  }
}
