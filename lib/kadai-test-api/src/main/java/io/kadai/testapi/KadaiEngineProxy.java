package io.kadai.testapi;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.common.internal.KadaiEngineImpl;
import java.lang.reflect.Field;
import org.apache.ibatis.session.SqlSession;

/** Utility class to enable unit tests to access mappers directly. */
public class KadaiEngineProxy {

  private final InternalKadaiEngine engine;

  public KadaiEngineProxy(KadaiEngine kadaiEngine) throws Exception {
    Field internal = KadaiEngineImpl.class.getDeclaredField("internalKadaiEngineImpl");
    internal.setAccessible(true);
    engine = (InternalKadaiEngine) internal.get(kadaiEngine);
  }

  public InternalKadaiEngine getEngine() {
    return engine;
  }

  public SqlSession getSqlSession() {
    return engine.getSqlSession();
  }

  public void openConnection() {
    engine.openConnection();
  }

  public void returnConnection() {
    engine.returnConnection();
  }
}
