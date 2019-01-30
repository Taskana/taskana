package pro.taskana;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import pro.taskana.jobs.TransactionalJobsConfiguration;
import pro.taskana.ldap.LdapCacheTestImpl;
import pro.taskana.ldap.LdapClient;
import pro.taskana.ldap.LdapConfiguration;
import pro.taskana.rest.AccessIdController;
import pro.taskana.rest.RestConfiguration;
import pro.taskana.sampledata.SampleDataGenerator;

/**
 * Example Application showing the implementation of taskana-rest-spring for jboss application server.
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "pro.taskana")
@Import({TransactionalJobsConfiguration.class, LdapConfiguration.class, RestConfiguration.class})
public class TaskanaWildFlyApplication extends SpringBootServletInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaWildFlyApplication.class);
    @Value("${taskana.schemaName:TASKANA}")
    public String schemaName;

    @Value("${generateSampleData:true}")
    public boolean generateSampleData;

    @Autowired
    private SampleDataGenerator sampleDataGenerator;

    @Autowired
    private LdapClient ldapClient;

    @Autowired private LdapCacheTestImpl ldapCacheTest;

    public static void main(String[] args) {
        SpringApplication.run(TaskanaWildFlyApplication.class, args);
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties props = new DataSourceProperties();
        props.setUrl("jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0;INIT=CREATE SCHEMA IF NOT EXISTS " + schemaName);
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
    @DependsOn("getTaskanaEngine") // generate sample data after schema was inserted
    public SampleDataGenerator generateSampleData(DataSource dataSource) throws SQLException {
        sampleDataGenerator = new SampleDataGenerator(dataSource);
        return sampleDataGenerator;
    }

    @PostConstruct
    private void init() {
        if (!ldapClient.useLdap()) {
            AccessIdController.setLdapCache(ldapCacheTest);
        }
        if (generateSampleData) {
            sampleDataGenerator.generateSampleData(schemaName);
        }
    }
}

