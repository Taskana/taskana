package pro.taskana.springtx;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

/**
 * Class to start an Application to test Taskana
 *
 * @author v101536 (Kilian Burkhardt)
 */
@SpringBootApplication
@Import(TaskanaConfig.class)
public class TaskanaConfigTestApplication {

        public static void main(String[] args) {
                new SpringApplicationBuilder(TaskanaConfigTestApplication.class).run(args);
        }
}
