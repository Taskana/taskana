package pro.taskana.common.internal.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import pro.taskana.common.api.exceptions.SystemException;

@DisabledOnOs(
    value = OS.WINDOWS,
    disabledReason = "Deleting Windows temp dir is a junit bug. Fixed with Junit 5.8.0")
class FileLoaderUtilTest {
  @TempDir Path tempDir;

  @Test
  void should_DetectFile_When_FileIsPresentOnSystem() throws Exception {
    Path file = Files.createFile(tempDir.resolve("systemTest.txt"));
    boolean fileExists = FileLoaderUtil.fileExistsOnSystem(file.toAbsolutePath().toString());
    assertThat(fileExists).isTrue();
  }

  @Test
  void should_NotDetectFile_When_FileDoesNotExist() {
    boolean fileExists = FileLoaderUtil.fileExistsOnSystem("doesnotexist");
    assertThat(fileExists).isFalse();
  }

  @Test
  void should_NotDetectFile_When_FileExistsOnClasspath() {
    boolean fileExists = FileLoaderUtil.fileExistsOnSystem("fileInClasspath.txt");
    assertThat(fileExists).isFalse();
  }

  @Test
  void should_OpenFile_When_FileIsPresentOnSystem() throws Exception {
    Path file = Files.createFile(tempDir.resolve("systemTest.txt"));
    String expectedFileContent = "This file is in the file system";
    Files.write(file, List.of(expectedFileContent), StandardCharsets.UTF_8);

    InputStream stream =
        FileLoaderUtil.openFileFromClasspathOrSystem(file.toAbsolutePath().toString(), getClass());

    String fileContent = convertToString(stream);
    assertThat(fileContent).isEqualTo(expectedFileContent);
  }

  @Test
  void should_ThrowSystemException_When_FileDoesNotExist() {
    Class<?> clazz = getClass();
    assertThatThrownBy(() -> FileLoaderUtil.openFileFromClasspathOrSystem("doesnotexist", clazz))
        .isInstanceOf(SystemException.class)
        .hasMessage("Could not find a file in the classpath 'doesnotexist'");
  }

  @Test
  void should_OpenFile_When_FileExistsOnClasspath() {
    InputStream stream =
        FileLoaderUtil.openFileFromClasspathOrSystem("fileInClasspath.txt", getClass());

    String fileContent = convertToString(stream);
    assertThat(fileContent).isEqualTo("This file is in the classpath");
  }

  private String convertToString(InputStream stream) {
    return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
  }
}
