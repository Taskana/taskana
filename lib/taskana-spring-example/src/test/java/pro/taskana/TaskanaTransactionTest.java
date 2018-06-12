package pro.taskana;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TaskanaConfigTestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"inmemorydb", "dev"})
public class TaskanaTransactionTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    @Before
    public void before() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DELETE FROM TASK");
        jdbcTemplate.execute("DELETE FROM WORKBASKET");
        jdbcTemplate.execute("DELETE FROM CUSTOMDB.TEST");
    }

    @Test
    public void testTaskanaSchema() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/schema", String.class);
        assertThat(responseEntity.getBody(), is("TASKANA"));
    }

    @Test
    public void testTransaction() {
        assertBefore(0, 0);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/transaction", String.class);
        System.err.println("response: " + responseEntity.getBody());
        assertThat(responseEntity.getBody(), containsString("workbaskets: 1"));

        assertAfter(1, 0);
    }

    @Test
    public void testTransactionRollback() {
        assertBefore(0, 0);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/transaction?rollback={rollback}",
            String.class, "true");
        System.err.println("result: " + responseEntity.getBody());
        assertThat(responseEntity.getBody(), containsString("workbaskets: 1"));

        assertAfter(0, 0);
    }

    @Test
    public void testTransactionCombined() {
        assertBefore(0, 0);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/transaction-many", String.class);
        System.err.println("response: " + responseEntity.getBody());
        assertThat(responseEntity.getBody(), containsString("workbaskets: 3"));

        assertAfter(3, 0);
    }

    @Test
    public void testTransactionCombinedRollback() {
        assertBefore(0, 0);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/transaction-many?rollback={rollback}",
            String.class, "true");
        System.err.println("result: " + responseEntity.getBody());
        assertThat(responseEntity.getBody(), containsString("workbaskets: 3"));

        assertAfter(0, 0);
    }

    @Test
    public void testTransactionCustomdb() {
        assertBefore(0, 0);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/customdb", String.class);
        System.err.println("response: " + responseEntity.getBody());
        assertThat(responseEntity.getBody(), containsString("workbaskets: 2"));
        assertThat(responseEntity.getBody(), containsString("tests: 2"));

        assertAfter(2, 2);
    }

    @Test
    public void testTransactionCustomdbRollback() {
        assertBefore(0, 0);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/customdb?rollback={rollback}",
            String.class, "true");
        System.err.println("response: " + responseEntity.getBody());
        assertThat(responseEntity.getBody(), containsString("workbaskets: 2"));
        assertThat(responseEntity.getBody(), containsString("tests: 2"));

        assertAfter(0, 0);
    }

    private void assertBefore(int workbaskets, int tests) {
        assertWorkbaskets("before", workbaskets);
        assertCustomdbTests("before", tests);
    }

    private void assertAfter(int workbaskets, int tests) {
        assertWorkbaskets("after", workbaskets);
        assertCustomdbTests("after", tests);
    }

    private void assertWorkbaskets(String assertion, int value) {
        int workbaskets = getWorkbaskets();
        System.err.println(assertion + " workbaskets: " + workbaskets);
        assertThat(workbaskets, is(value));
    }

    private void assertCustomdbTests(String assertion, int value) {
        int tests = getCustomdbTests();
        System.err.println(assertion + " tests: " + tests);
        assertThat(tests, is(value));
    }

    private int getWorkbaskets() {
        ResponseEntity<Integer> workbaskets = restTemplate.getForEntity("/workbaskets", Integer.class);
        if (workbaskets.getStatusCode().is2xxSuccessful()) {
            return workbaskets.getBody();
        } else {
            throw new RuntimeException("error get workbaskets: " + workbaskets.getBody());
        }
    }

    private int getCustomdbTests() {
        ResponseEntity<Integer> tests = restTemplate.getForEntity("/customdb-tests", Integer.class);
        if (tests.getStatusCode().is2xxSuccessful()) {
            return tests.getBody();
        } else {
            throw new RuntimeException("error get customdb.tests: " + tests.getBody());
        }
    }
}
