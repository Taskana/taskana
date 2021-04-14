package pro.taskana.common.internal.util;

import java.io.File;

public class FileLoaderUtil {

  public static boolean loadFromClasspath(String fileToLoad) {
    boolean loadFromClasspath = true;
    File f = new File(fileToLoad);
    if (f.exists() && !f.isDirectory()) {
      loadFromClasspath = false;
    }
    return loadFromClasspath;
  }

}
