package pro.taskana;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

/**
 * Class to start an Application to test Taskana
 *
 */
@SpringBootApplication
@Import(TaskanaConfig.class)
public class TaskanaConfigTestApplication {

        public static void main(String[] args) {
                new SpringApplicationBuilder(TaskanaConfigTestApplication.class).run(args);
        }
}
