package pro.taskana;

import static org.junit.Assert.assertEquals;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import pro.taskana.TaskanaEngine;
import pro.taskana.history.HistoryEventProducer;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.rest.RestConfiguration;

/**
 * test loading of history plugin.
 *
 */
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "history.plugin")
@SpringBootTest(classes = RestConfiguration.class)
public class HistoryPluginLoaderTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TaskanaEngine taskanaEngine;

    @Test
    public void testHistoryEventProducerIsEnabled() {
        HistoryEventProducer historyEventProducer = ((TaskanaEngineImpl) taskanaEngine).getHistoryEventProducer();
        assertEquals(historyEventProducer.isEnabled(), true);
    }

}
