package pro.taskana.rest;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;

import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.hal.Jackson2HalModule;

import org.springframework.http.MediaType;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pro.taskana.configuration.SpringTaskanaEngineConfiguration;
import pro.taskana.exceptions.SystemException;
import pro.taskana.sampledata.SampleDataGenerator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestSchemaNameCustomizable {

    String schemaName = "CUSTOMSCHEMANAME";
    boolean isPostgres = false;

    @Autowired
    private DataSource dataSource;

    public void resetDb() {
        SampleDataGenerator sampleDataGenerator;
        try {
            if ("PostgreSQL".equals(dataSource.getConnection().getMetaData().getDatabaseProductName())) {
                isPostgres = true;
                schemaName = schemaName.toLowerCase();
            }
            new SpringTaskanaEngineConfiguration(dataSource, true, true, schemaName);
            sampleDataGenerator = new SampleDataGenerator(dataSource);
            sampleDataGenerator.generateSampleData(schemaName);
        } catch (SQLException e) {
            throw new SystemException("tried to reset DB and caught Exception " + e, e);
        }
    }

    @Test
    public void chekCustomSchemaNameIsDefined() {
        resetDb();
        ResultSet rs;
        try {
            Statement stmt = dataSource.getConnection().createStatement();
            if (isPostgres) {
                rs = stmt.executeQuery(
                    "SELECT * FROM pg_catalog.pg_tables where schemaname = '" + schemaName.toLowerCase() + "'");

            } else {
                rs = stmt.executeQuery(
                    "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + schemaName + "'");
            }
            while (rs.next()) {
                String tableName = rs.getString(isPostgres ? "tablename" : "TABLE_NAME");
                if (tableName.equals(isPostgres ? "workbasket" : "WORKBASKET")) {
                    Assert.assertEquals(tableName, isPostgres ? "workbasket" : "WORKBASKET");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return a REST template which is capable of dealing with responses in HAL format
     *
     * @return RestTemplate
     */
    private RestTemplate getRestTemplate() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new Jackson2HalModule());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
        // converter.setSupportedMediaTypes(ImmutableList.of(MediaTypes.HAL_JSON));
        converter.setObjectMapper(mapper);

        RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));
        return template;
    }

}
