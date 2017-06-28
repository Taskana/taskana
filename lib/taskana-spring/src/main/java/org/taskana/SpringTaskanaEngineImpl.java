package org.taskana;

import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.taskana.configuration.SpringTaskanaEngineConfiguration;
import org.taskana.impl.TaskanaEngineImpl;

public class SpringTaskanaEngineImpl extends TaskanaEngineImpl {

	public SpringTaskanaEngineImpl(SpringTaskanaEngineConfiguration taskanaEngineConfiguration) {
		super(taskanaEngineConfiguration);
	}

	private PlatformTransactionManager transactionManager;

	@PostConstruct
	public void init() throws SQLException {
		this.transactionFactory = new ManagedTransactionFactory();
	}

	public PlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
