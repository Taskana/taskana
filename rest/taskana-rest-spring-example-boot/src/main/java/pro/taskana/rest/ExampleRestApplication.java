package pro.taskana.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import pro.taskana.sampledata.SampleDataGenerator;

/** Example Application showing a minimal implementation of the taskana REST service. */
@EnableScheduling
@EnableAutoConfiguration
@ComponentScan("pro.taskana")
public class ExampleRestApplication {

  @Autowired
  public ExampleRestApplication(
      SampleDataGenerator sampleDataGenerator,
      @Value("${generateSampleData:true}") boolean generateSampleData) {
    if (generateSampleData) {
      sampleDataGenerator.generateSampleData();
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(ExampleRestApplication.class, args);
  }
}
