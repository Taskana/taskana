package io.kadai.properties;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(
    classes = MyKadaiTestConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyKadaiConfigurationIntTest {

  @Autowired private ApplicationContext appContext;

  @Test
  void should_loadApplicationContextWithMyKadaiProperties_When_ApplicationStarts() {
    assertThat(appContext.getBean("kadaiPropertiesFileName", String.class))
        .isEqualTo("/mykadai.properties");
    assertThat(appContext.getBean("kadaiPropertiesDelimiter", String.class)).isEqualTo(";");
  }
}
