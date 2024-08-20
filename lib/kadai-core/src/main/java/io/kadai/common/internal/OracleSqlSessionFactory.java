package io.kadai.common.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MyBatis SqlSession Factory for Oracle Databases.
 *
 * <p>After Connection the SQL session is alterd with SORT and COMP Settings for Oracle. PLease have
 * a look at alterSession Method
 */
public class OracleSqlSessionFactory extends DefaultSqlSessionFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(OracleSqlSessionFactory.class);

  public OracleSqlSessionFactory(Configuration configuration) {
    super(configuration);
  }

  @Override
  public SqlSession openSession() {
    SqlSession sqlSession = super.openSession();
    alterSession(sqlSession);
    return sqlSession;
  }

  @Override
  public SqlSession openSession(boolean autoCommit) {
    SqlSession session = super.openSession(autoCommit);
    alterSession(session);
    return session;
  }

  @Override
  public SqlSession openSession(Connection connection) {
    SqlSession session = super.openSession(connection);
    alterSession(session);
    return session;
  }

  @Override
  public SqlSession openSession(ExecutorType execType) {
    SqlSession session = super.openSession(execType);
    alterSession(session);
    return session;
  }

  @Override
  public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
    SqlSession session = super.openSession(execType, autoCommit);
    alterSession(session);
    return session;
  }

  @Override
  public SqlSession openSession(ExecutorType execType, Connection connection) {
    SqlSession session = super.openSession(execType, connection);
    alterSession(session);
    return session;
  }

  @Override
  public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
    SqlSession session = super.openSession(execType, level);
    alterSession(session);
    return session;
  }

  @Override
  public SqlSession openSession(TransactionIsolationLevel level) {
    SqlSession session = super.openSession(level);
    alterSession(session);
    return session;
  }

  private void alterSession(SqlSession session) {
    try (Statement statement = session.getConnection().createStatement()) {
      statement.addBatch("ALTER SESSION SET NLS_COMP = LINGUISTIC");
      // https://docs.oracle.com/cd/E11882_01/server.112/e10729/ch5lingsort.htm#NLSPG293
      statement.addBatch("ALTER SESSION SET NLS_SORT = BINARY_CI");
      statement.executeBatch();
      LOGGER.debug("Altered newly created session parameters.");
    } catch (SQLException e) {
      LOGGER.error("Alter session failed!", e);
    }
  }
}
