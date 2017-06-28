package org.taskana.rest;

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
import org.taskana.CategoryService;
import org.taskana.TaskService;
import org.taskana.TaskanaEngine;
import org.taskana.WorkbasketService;
import org.taskana.configuration.TaskanaEngineConfiguration;
import org.taskana.model.Workbasket;
import org.taskana.rest.serialization.WorkbasketMixIn;
import org.taskana.sampledata.SampleDataGenerator;

import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;

@SpringBootApplication
public class RestApplication {

	private static final Logger logger = LoggerFactory.getLogger(RestApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(RestApplication.class, args);
	}

	@Bean
	public CategoryService getCategoryService() throws Exception {
		return getTaskanaEngine().getCategoryService();
	}

	@Bean
	public TaskService getTaskService() throws Exception {
		return getTaskanaEngine().getTaskService();
	}

	@Bean
	public WorkbasketService getWorkbasketService() throws Exception {
		return getTaskanaEngine().getWorkbasketService();
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

	@PostConstruct
	public void createSampleData() {
		try {
			new SampleDataGenerator(getTaskanaEngineConfiguration().createDefaultDataSource()).generateSampleData();
		} catch (SQLException e) {
			logger.error("Could not create sample data.", e);
		}
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
		b.indentOutput(true).mixIn(Workbasket.class, WorkbasketMixIn.class);
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

}
