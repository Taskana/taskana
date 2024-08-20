package io.kadai.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

/** Class to start an Application to test Kadai. */
@SpringBootApplication
@Import(KadaiConfig.class)
@SuppressWarnings("checkstyle:hideutilityclassconstructor")
public class KadaiConfigTestApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(KadaiConfigTestApplication.class).run(args);
  }
}
