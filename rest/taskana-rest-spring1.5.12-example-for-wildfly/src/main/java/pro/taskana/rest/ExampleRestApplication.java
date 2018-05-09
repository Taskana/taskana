package pro.taskana.rest;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.impl.ClassificationServiceImpl;
import pro.taskana.sampledata.SampleDataGenerator;

/**
 * Example Application showing the implementation of taskana-rest-spring.
 */
@SpringBootApplication
@EnableScheduling
@Import(RestConfiguration.class)
public class ExampleRestApplication extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationServiceImpl.class);

    public static void main(String[] args) {
        SpringApplication.run(ExampleRestApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ExampleRestApplication.class);
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties props = new DataSourceProperties();
        props.setUrl("jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0;INIT=CREATE SCHEMA IF NOT EXISTS TASKANA");
        return props;
    }

    @Bean
    public DataSource dataSource(DataSourceProperties dsProperties) {
        // First try to load Properties and get Datasource via jndi lookup
        Context ctx;
        DataSource dataSource;
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream propertyStream = classloader.getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            ctx = new InitialContext();
            properties.load(propertyStream);
            dataSource = (DataSource) ctx.lookup(properties.getProperty("datasource.jndi"));
            return dataSource;
        } catch (Exception e) {
            LOGGER.error(
                "Caught exception {} when attempting to start Taskana with Datasource from Jndi. Using default H2 datasource. ",
                e);
            return dsProperties.initializeDataSourceBuilder().build();
        }
    }

    @Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @DependsOn("taskanaEngineConfiguration") // generate sample data after schema was inserted
    public SampleDataGenerator generateSampleData(DataSource dataSource) throws SQLException {
        SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource);
        sampleDataGenerator.generateSampleData();
        return sampleDataGenerator;
    }
}
