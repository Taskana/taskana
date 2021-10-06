package pro.taskana.common.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResourceUtilTest {

  @Test
  void should_loadResource() throws Exception {
    String resourceAsString = ResourceUtil.readResourceAsString(getClass(), "fileInClasspath.txt");

    assertThat(resourceAsString).isEqualTo("This file is in the classpath");
  }

  @Test
  void should_ReturnNull_When_ResourceDoesNotExist() throws Exception {
    String resourceAsString = ResourceUtil.readResourceAsString(getClass(), "doesNotExist");

    assertThat(resourceAsString).isNull();
  }
}
