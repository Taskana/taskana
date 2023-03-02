package pro.taskana.common.internal.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.api.exceptions.SystemException;

@Slf4j
public class FileLoaderUtil {

  private FileLoaderUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static boolean fileExistsOnSystem(String fileToLoad) {
    File f = new File(fileToLoad);
    return f.exists() && !f.isDirectory();
  }

  public static InputStream openFileFromClasspathOrSystem(String fileToLoad, Class<?> clazz) {
    if (fileExistsOnSystem(fileToLoad)) {
      try {
        InputStream inputStream = new FileInputStream(fileToLoad);
        log.debug("Load file {} from path", fileToLoad);
        return inputStream;
      } catch (FileNotFoundException e) {
        throw new SystemException(
            String.format("Could not find a file with provided path '%s'", fileToLoad));
      }
    }
    InputStream inputStream = clazz.getResourceAsStream(fileToLoad);
    if (inputStream == null) {
      throw new SystemException(
          String.format("Could not find a file in the classpath '%s'", fileToLoad));
    }
    log.debug("Load file {} from classpath", fileToLoad);
    return inputStream;
  }
}
