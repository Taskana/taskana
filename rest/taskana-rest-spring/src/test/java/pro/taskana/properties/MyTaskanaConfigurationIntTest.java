package pro.taskana.properties;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(
    classes = MyTaskanaTestConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyTaskanaConfigurationIntTest {

  @Autowired private ApplicationContext appContext;

  @Test
  void should_loadApplicationContextWithMyTaskanaProperties_When_ApplicationStarts() {
    assertThat(appContext.getBean("taskanaPropertiesFileName", String.class))
        .isEqualTo("/mytaskana.properties");
    assertThat(appContext.getBean("taskanaPropertiesDelimiter", String.class)).isEqualTo(";");
  }
}
