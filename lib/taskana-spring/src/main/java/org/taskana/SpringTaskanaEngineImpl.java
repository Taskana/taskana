package org.taskana;

import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.taskana.configuration.SpringTaskanaEngineConfiguration;
import org.taskana.impl.TaskanaEngineImpl;

/**
 * This class configures the TaskanaEngine for spring
 */
public class SpringTaskanaEngineImpl extends TaskanaEngineImpl {

	public SpringTaskanaEngineImpl(SpringTaskanaEngineConfiguration taskanaEngineConfiguration) {
		super(taskanaEngineConfiguration);
	}

	@PostConstruct
	public void init() throws SQLException {
		this.transactionFactory = new ManagedTransactionFactory();
	}

}
