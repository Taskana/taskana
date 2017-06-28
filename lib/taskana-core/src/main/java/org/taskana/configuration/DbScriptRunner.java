package org.taskana.configuration;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbScriptRunner {

	private static final Logger logger = LoggerFactory.getLogger(DbScriptRunner.class);

	private static final String SQL = "/sql";
	private static final String DB_STRUCTURE = SQL + "/db-structure.sql";

	private DataSource dataSource;

	public DbScriptRunner(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	/**
	 * Run all db scripts
	 * 
	 * @throws SQLException
	 */
	public void run() throws SQLException {
		StringWriter outWriter = new StringWriter();
		PrintWriter logWriter = new PrintWriter(outWriter);

		StringWriter errorWriter = new StringWriter();
		PrintWriter errorLogWriter = new PrintWriter(errorWriter);

		ScriptRunner runner = new ScriptRunner(dataSource.getConnection());
		logger.debug(dataSource.getConnection().getMetaData().toString());

		runner.setStopOnError(true);
		runner.setLogWriter(logWriter);
		runner.setErrorLogWriter(errorLogWriter);

		runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream(DB_STRUCTURE)));
		runner.closeConnection();

		logger.debug(outWriter.toString());
		if (!errorWriter.toString().trim().isEmpty()) {
			logger.error(errorWriter.toString());
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
