package pro.taskana.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.internal.security.CurrentUserContext;

@ExtendWith(JaasExtension.class)
public class JaasExtensionTest {

  // region JaasExtension#interceptTestClassConstructor
  @WithAccessId(user = "constructor")
  public JaasExtensionTest() {
    assertThat(CurrentUserContext.getUserid()).isEqualTo("constructor");
  }
  // endregion

  @WithAccessId(user = "beforeall")
  @BeforeAll
  static void should_SetJaasSubject_When_AnnotationExists_On_BeforeAll() {
    assertThat(CurrentUserContext.getUserid()).isEqualTo("beforeall");
  }

  @WithAccessId(user = "beforeall")
  @WithAccessId(user = "beforeall2")
  @BeforeAll
  static void should_NotSetJaasSubject_When_MultipleAnnotationsExist_On_BeforeAll() {
    assertThat(CurrentUserContext.getUserid()).isEqualTo(null);
  }

  @BeforeAll
  static void should_NotSetJaasSubject_When_AnnotationIsMissing_On_BeforeAll() {
    assertThat(CurrentUserContext.getUserid()).isEqualTo(null);
  }

  @BeforeEach
  void should_NotJaasSubject_When_AnnotationIsMissing_On_BeforeEach() {
    assertThat(CurrentUserContext.getUserid()).isEqualTo(null);
  }

  @WithAccessId(user = "beforeeach")
  @BeforeEach
  void should_SetJaasSubject_When_AnnotationExists_On_BeforeEach() {
    assertThat(CurrentUserContext.getUserid()).isEqualTo("beforeeach");
  }

  @WithAccessId(user = "beforeeach")
  @WithAccessId(user = "beforeeach2")
  @BeforeEach
  void should_NotSetJaasSubject_When_MultipleAnnotationsExist_On_BeforeEach() {
    assertThat(CurrentUserContext.getUserid()).isEqualTo(null);
  }

  @WithAccessId(user = "user")
  @Test
  void should_SetJaasSubject_When_AnnotationExists_On_Test() {
    assertThat(CurrentUserContext.getUserid()).isEqualTo("user");
  }

  @WithAccessId(user = "user")
  @WithAccessId(user = "user2")
  @Test
  @Disabled("can we make this work somehow?")
  void should_NotSetJaasSubject_When_MultipleAnnotationsExist_On_Test() {
    assertThat(CurrentUserContext.getUserid()).isEqualTo(null);
  }

  @Test
  void should_NotSetJaasSubject_When_AnnotationIsMissing_On_Test() {
    assertThat(CurrentUserContext.getUserid()).isEqualTo(null);
  }

  @WithAccessId(user = "testtemplate1")
  @WithAccessId(user = "testtemplate2")
  @WithAccessId(user = "testtemplate3")
  @TestTemplate
  void should_SetMultipleJaasSubjects_When_MultipleAnnotationsExist_On_TestTemplate() {
    assertThat(CurrentUserContext.getUserid()).isNotNull();
  }

  @Nested
  class ConstructorWithoutAccessId {
    ConstructorWithoutAccessId() {
      assertThat(CurrentUserContext.getUserid()).isEqualTo(null);
    }

    @Test
    void should_NotSetJaasSubject_When_AnnotationIsMissing_On_Constructor() {
      assertThat(CurrentUserContext.getUserid()).isNull();
    }
  }

  @Nested
  class ConstructorWithAccessId {
    @WithAccessId(user = "constructor")
    ConstructorWithAccessId() {
      assertThat(CurrentUserContext.getUserid()).isEqualTo("constructor");
    }

    @Test
    void should_SetJaasSubject_When_AnnotationExists_On_Constructor() {
      assertThat(CurrentUserContext.getUserid()).isNull();
    }
  }
}
