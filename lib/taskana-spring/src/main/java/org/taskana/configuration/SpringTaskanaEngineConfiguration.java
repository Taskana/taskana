package org.taskana.configuration;

import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.taskana.SpringTaskanaEngineImpl;
import org.taskana.TaskanaEngine;

public class SpringTaskanaEngineConfiguration extends TaskanaEngineConfiguration {

	@PostConstruct
	public void init() throws SQLException {
		dbScriptRunner = new DbScriptRunner(this.dataSource);
		dbScriptRunner.run();
	}

	/**
	 * This method creates the Spring-based TaskanaEngine without an
	 * sqlSessionFactory
	 * 
	 * @return the TaskanaEngine
	 * @throws SQLException
	 */
	public TaskanaEngine buildTaskanaEngine() throws SQLException {
		return new SpringTaskanaEngineImpl(this);
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
