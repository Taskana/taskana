package pro.taskana.rest;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.ldap.LdapConfiguration;

/**
 * Configuration class for all rest tests.
 */
@Import({RestConfiguration.class, LdapConfiguration.class})
public class TestConfiguration {

    @Bean
    public DataSource dataSource() {
        return TaskanaEngineConfiguration.createDefaultDataSource();
    }

}
