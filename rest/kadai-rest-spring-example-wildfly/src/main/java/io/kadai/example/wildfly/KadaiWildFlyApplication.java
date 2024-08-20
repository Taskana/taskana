package io.kadai.example.wildfly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Example Application showing the implementation of kadai-rest-spring for jboss application server.
 */
@EnableScheduling
@SpringBootApplication
@ComponentScan("io.kadai")
public class KadaiWildFlyApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(KadaiWildFlyApplication.class, args);
  }
}
