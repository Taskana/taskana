package pro.taskana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Example Application showing the implementation of taskana-rest-spring for jboss application
 * server.
 */
@EnableScheduling
@SpringBootApplication
@ComponentScan("pro.taskana")
public class TaskanaWildFlyApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(TaskanaWildFlyApplication.class, args);
  }
}
