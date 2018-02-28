package pro.taskana.rest;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import sampledata.SampleDataGenerator;

@SpringBootApplication
@Import(RestConfiguration.class)
public class ExampleRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleRestApplication.class, args);
    }

    @Bean
    @DependsOn("taskanaEngineConfiguration") //generate sample data after schema was inserted
    public SampleDataGenerator generateSampleData(DataSource dataSource) throws SQLException {
        SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource);
        sampleDataGenerator.generateSampleData();
        return sampleDataGenerator;
    }
}
