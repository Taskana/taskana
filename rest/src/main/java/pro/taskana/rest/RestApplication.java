package pro.taskana.rest;

import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.SpringHandlerInstantiator;

import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;

import pro.taskana.ClassificationService;
import pro.taskana.TaskMonitorService;
import pro.taskana.TaskService;
import pro.taskana.TaskanaEngine;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.rest.resource.mapper.ClassificationMapper;
import pro.taskana.rest.resource.mapper.WorkbasketAccessItemMapper;
import pro.taskana.rest.resource.mapper.WorkbasketDefinitionMapper;
import pro.taskana.rest.resource.mapper.WorkbasketMapper;
import pro.taskana.rest.resource.mapper.WorkbasketSummaryMapper;
import pro.taskana.sampledata.SampleDataGenerator;

@SpringBootApplication
public class RestApplication {

    private static final Logger logger = LoggerFactory.getLogger(RestApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }

    @Bean
    public ClassificationService getClassificationService() throws Exception {
        return getTaskanaEngine().getClassificationService();
    }

    @Bean
    public TaskService getTaskService() throws Exception {
        return getTaskanaEngine().getTaskService();
    }

    @Bean
    public TaskMonitorService getTaskMonitorService() throws Exception {
        return getTaskanaEngine().getTaskMonitorService();
    }

    @Bean
    public WorkbasketService getWorkbasketService() throws Exception {
        return getTaskanaEngine().getWorkbasketService();
    }

    @Bean
    public WorkbasketSummaryMapper getWorkbasketSummaryMapper() {
        return new WorkbasketSummaryMapper();
    }

    @Bean
    public WorkbasketMapper getWorkbasketMapper() {
        return new WorkbasketMapper();
    }

    @Bean
    public WorkbasketAccessItemMapper getWorkbasketAccessItemMapper() {
        return new WorkbasketAccessItemMapper();
    }

    @Bean
    public WorkbasketDefinitionMapper getWorkbasketDefinitionMapper() {
        return new WorkbasketDefinitionMapper();
    }

    @Bean
    public ClassificationMapper getClassificationMapper() {
        return new ClassificationMapper();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskanaEngine getTaskanaEngine() throws SQLException {
        return getTaskanaEngineConfiguration().buildTaskanaEngine();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public TaskanaEngineConfiguration getTaskanaEngineConfiguration() throws SQLException {
        TaskanaEngineConfiguration taskanaEngineConfiguration = new TaskanaEngineConfiguration(null, true);
        return taskanaEngineConfiguration;
    }

    /**
     * Needed to override JSON De-/Serializer in Jackson.
     *
     * @param handlerInstantiator
     * @return
     */
    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder(HandlerInstantiator handlerInstantiator) {
        Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
        b.indentOutput(true);
        b.handlerInstantiator(handlerInstantiator);
        return b;
    }

    /**
     * Needed for injection into jackson deserilizer.
     *
     * @param context
     * @return
     */
    @Bean
    public HandlerInstantiator handlerInstantiator(ApplicationContext context) {
        return new SpringHandlerInstantiator(context.getAutowireCapableBeanFactory());
    }

    @PostConstruct
    public void createSampleData() {
        try {
            new SampleDataGenerator(getTaskanaEngineConfiguration().createDefaultDataSource()).generateSampleData();
        } catch (SQLException e) {
            logger.error("Could not create sample data.", e);
        }
    }

}
