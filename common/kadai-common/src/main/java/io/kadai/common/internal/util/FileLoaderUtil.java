package io.kadai.common.internal.util;

import io.kadai.common.api.exceptions.SystemException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileLoaderUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileLoaderUtil.class);

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
        LOGGER.debug("Load file {} from path", fileToLoad);
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
    LOGGER.debug("Load file {} from classpath", fileToLoad);
    return inputStream;
  }
}
