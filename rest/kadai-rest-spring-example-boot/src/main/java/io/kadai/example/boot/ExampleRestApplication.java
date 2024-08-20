package io.kadai.example.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Example Application showing a minimal implementation of the kadai REST service. */
@EnableScheduling
@SpringBootApplication
@ComponentScan("io.kadai")
public class ExampleRestApplication {

  public static void main(String[] args) {
    SpringApplication.run(ExampleRestApplication.class, args);
  }
}
