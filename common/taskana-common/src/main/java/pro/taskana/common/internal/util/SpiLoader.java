package pro.taskana.common.internal.util;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public class SpiLoader {

  private SpiLoader() {
    throw new IllegalStateException("Utility class");
  }

  public static <T> List<T> load(Class<T> clazz) {
    ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
    return StreamSupport.stream(serviceLoader.spliterator(), false).toList();
  }
}
