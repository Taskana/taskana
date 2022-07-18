package pro.taskana.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

/** The TaskanaConfigTestApplication starts an Application to test Taskana. */
@SpringBootApplication
@Import(TaskanaConfig.class)
@SuppressWarnings("checkstyle:hideutilityclassconstructor")
public class TaskanaConfigTestApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(TaskanaConfigTestApplication.class).run(args);
  }
}
