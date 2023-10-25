package pro.taskana.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = TaskanaConfigTestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"inmemorydb", "dev"})
@Import({TransactionalJobsConfiguration.class})
// This BootstrapTest must be executed before all other tests
// especially before TaskanaTransactionIntTest (There is everything deleted...
// here we only test the execution of PostConstruct method
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(1)
class ExampleBootstrapTest {

  @Autowired private JdbcTemplate jdbcTemplate;

  @Test
  void should_count_tasks_after_psotConstruc_method_was_executed() {
    Integer actualNumberOfTasks = jdbcTemplate.queryForObject(
        "SELECT COUNT(ID) FROM TASK WHERE NAME = ?",
        Integer.class,
        "Spring example task");

    assertThat(actualNumberOfTasks).isEqualTo(1);
  }
}
