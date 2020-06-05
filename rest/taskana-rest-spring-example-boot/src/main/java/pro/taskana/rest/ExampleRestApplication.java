package pro.taskana.rest;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import pro.taskana.RestConfiguration;
import pro.taskana.jobs.TransactionalJobsConfiguration;
import pro.taskana.sampledata.SampleDataGenerator;

/** Example Application showing the implementation of taskana-rest-spring. */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "pro.taskana")
@SuppressWarnings("checkstyle:Indentation")
@Import({
  ExampleRestConfiguration.class,
  TransactionalJobsConfiguration.class,
  RestConfiguration.class,
  WebMvcConfig.class
})
public class ExampleRestApplication {

  private final SampleDataGenerator sampleDataGenerator;

  @Value("${generateSampleData:true}")
  public boolean generateSampleData;

  @Autowired
  public ExampleRestApplication(SampleDataGenerator sampleDataGenerator) {
    this.sampleDataGenerator = sampleDataGenerator;
  }

  public static void main(String[] args) {
    SpringApplication.run(ExampleRestApplication.class, args);
  }

  @PostConstruct
  private void init() {
    if (generateSampleData) {
      sampleDataGenerator.generateSampleData();
    }
  }
}
