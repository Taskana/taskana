package pro.taskana.common.test.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.common.internal.security.CurrentUserContextImpl;
import pro.taskana.common.test.security.JaasExtensionTestExtensions.ShouldThrowJunitException;
import pro.taskana.common.test.security.JaasExtensionTestExtensions.ShouldThrowParameterResolutionException;

@ExtendWith(JaasExtension.class)
class JaasExtensionTest {

  private static final String INSIDE_DYNAMIC_TEST_USER = "inside_dynamic_test";
  private static final CurrentUserContext CURRENT_USER_CONTEXT = new CurrentUserContextImpl(true);
  private static final DynamicTest NOT_NULL_DYNAMIC_TEST =
      dynamicTest("dynamic test", () -> assertThat(CURRENT_USER_CONTEXT.getUserid()).isNotNull());
  private static final DynamicTest NULL_DYNAMIC_TEST =
      dynamicTest("dynamic test", () -> assertThat(CURRENT_USER_CONTEXT.getUserid()).isNull());
  private static final DynamicTest DYNAMIC_TEST_USER_DYNAMIC_TEST =
      dynamicTest(
          "dynamic test",
          () -> assertThat(CURRENT_USER_CONTEXT.getUserid()).isEqualTo(INSIDE_DYNAMIC_TEST_USER));

  // region JaasExtension#interceptBeforeAllMethod

  @BeforeAll
  static void should_NotSetJaasSubject_When_AnnotationIsMissing_On_BeforeAll() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isNull();
  }

  @WithAccessId(user = "beforeall")
  @BeforeAll
  static void should_SetJaasSubject_When_AnnotationExists_On_BeforeAll() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isEqualTo("beforeall");
  }

  @WithAccessId(user = "beforeall")
  @WithAccessId(user = "beforeall2")
  @BeforeAll
  static void should_NotSetJaasSubject_When_MultipleAnnotationsExist_On_BeforeAll() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isNull();
  }

  // endregion

  // region JaasExtension#interceptBeforeEachMethod

  @BeforeEach
  void should_NotSetJaasSubject_When_AnnotationIsMissing_On_BeforeEach() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isNull();
  }

  @WithAccessId(user = "beforeeach")
  @BeforeEach
  void should_SetJaasSubject_When_AnnotationExists_On_BeforeEach() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isEqualTo("beforeeach");
  }

  @WithAccessId(user = "beforeeach")
  @WithAccessId(user = "beforeeach2")
  @BeforeEach
  void should_NotSetJaasSubject_When_MultipleAnnotationsExist_On_BeforeEach() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isNull();
  }

  // endregion

  // region JaasExtension#interceptAfterEachMethod

  @AfterEach
  void should_NotSetJaasSubject_When_AnnotationIsMissing_On_AfterEach() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isNull();
  }

  @WithAccessId(user = "aftereach")
  @AfterEach
  void should_SetJaasSubject_When_AnnotationExists_On_AfterEach() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isEqualTo("aftereach");
  }

  @WithAccessId(user = "aftereach")
  @WithAccessId(user = "afterach2")
  @AfterEach
  void should_NotSetJaasSubject_When_MultipleAnnotationsExist_On_AfterEach() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isNull();
  }

  // endregion

  // region JaasExtension#interceptAfterAllMethod

  @AfterAll
  static void should_NotSetJaasSubject_When_AnnotationIsMissing_On_AfterAll() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isNull();
  }

  @WithAccessId(user = "afterall")
  @AfterAll
  static void should_SetJaasSubject_When_AnnotationExists_On_AfterAll() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isEqualTo("afterall");
  }

  @WithAccessId(user = "afterall")
  @WithAccessId(user = "afterall2")
  @AfterAll
  static void should_NotSetJaasSubject_When_MultipleAnnotationsExist_On_AfterAll() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isNull();
  }

  // endregion

  // region JaasExtension#interceptTestMethod

  @Test
  void should_NotSetJaasSubject_When_AnnotationIsMissing_On_Test() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isNull();
  }

  @WithAccessId(user = "user")
  @Test
  void should_SetJaasSubject_When_AnnotationExists_On_Test() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isEqualTo("user");
    assertThat(CURRENT_USER_CONTEXT.getGroupIds()).isEmpty();
  }

  @WithAccessId(
      user = "user",
      groups = {"group1", "group2"})
  @Test
  void should_SetJaasSubjectWithGroups_When_AnnotationExistsWithGroups_On_Test() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isEqualTo("user");
    assertThat(CURRENT_USER_CONTEXT.getGroupIds()).containsExactlyInAnyOrder("group1", "group2");
  }

  @WithAccessId(user = "user")
  @Test
  @ExtendWith(ShouldThrowParameterResolutionException.class)
  void should_NotInjectParameter_When_TestTemplateIsNotUsed(
      @SuppressWarnings("unused") WithAccessId accessId) {
    // THIS IS NOT RELEVANT
    assertThat(true).isTrue();
  }

  @WithAccessId(user = "user")
  @WithAccessId(user = "user2")
  @Test
  @ExtendWith(ShouldThrowJunitException.class)
  void should_ThrowJunitException_When_MultipleAnnotationsExist_On_Test() {
    // THIS IS NOT RELEVANT
    assertThat(true).isTrue();
  }

  // endregion

  // region JaasExtension#interceptTestFactory

  @TestFactory
  List<DynamicTest> should_NotSetJaasSubject_When_AnnotationIsMissing_On_TestFactory() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isNull();
    return Collections.emptyList();
  }

  @WithAccessId(user = "testfactory")
  @TestFactory
  List<DynamicTest> should_SetJaasSubject_When_AnnotationExists_On_TestFactory() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isEqualTo("testfactory");
    return Collections.emptyList();
  }

  @WithAccessId(user = "testfactory1")
  @WithAccessId(user = "testfactory2")
  @TestFactory
  List<DynamicTest>
      should_SetJaasSubjectFromFirstAnnotation_When_MultipleAnnotationsExists_On_TestFactory() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isEqualTo("testfactory1");
    return Collections.emptyList();
  }

  // endregion

  // region JaasExtension#interceptTestTemplateMethod

  @WithAccessId(user = "testtemplate")
  @TestTemplate
  void should_SetJaasSubject_When_AnnotationExists_On_TestTemplate() {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isEqualTo("testtemplate");
  }

  @WithAccessId(user = "testtemplate1")
  @WithAccessId(user = "testtemplate2")
  @WithAccessId(user = "testtemplate3")
  @TestTemplate
  void should_SetMultipleJaasSubjects_When_MultipleAnnotationsExist_On_TestTemplate(
      WithAccessId accessId) {
    assertThat(CURRENT_USER_CONTEXT.getUserid()).isEqualTo(accessId.user());
  }

  @WithAccessId(user = "testtemplate1", groups = "abc")
  @TestTemplate
  void should_InjectCorrectAccessId_When_AnnotationExists_On_TestTemplate(WithAccessId accessId) {
    assertThat(accessId.user()).isEqualTo("testtemplate1");
    assertThat(accessId.groups()).containsExactly("abc");
  }

  // endregion

  // region JaasExtension#interceptDynamicTest

  // region RETURNING DynamicNode

  // WITH DynamicTest

  @TestFactory
  DynamicTest should_NotSetAccessIdForDynamicTest_When_AnnotationIsMissing() {
    return NULL_DYNAMIC_TEST;
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  DynamicTest should_SetAccessIdForDynamicTest_When_AnnotationExists() {
    return DYNAMIC_TEST_USER_DYNAMIC_TEST;
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  DynamicTest should_SetMultipleAccessIdForDynamicTest_When_AnnotationsExist() {
    return NOT_NULL_DYNAMIC_TEST;
  }

  // WITH DynamicContainer

  @TestFactory
  DynamicContainer should_NotSetAccessIdForDynamicContainer_When_AnnotationIsMissing() {
    return dynamicContainer("dynamic container", Stream.of(NULL_DYNAMIC_TEST, NULL_DYNAMIC_TEST));
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  DynamicContainer should_SetAccessIdForDynamicContainer_When_AnnotationExists() {
    return dynamicContainer(
        "dynamic container",
        Stream.of(DYNAMIC_TEST_USER_DYNAMIC_TEST, DYNAMIC_TEST_USER_DYNAMIC_TEST));
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  DynamicContainer should_SetMultipleAccessIdForDynamicContainer_When_AnnotationsExist() {
    return dynamicContainer(
        "dynamic container", Stream.of(NOT_NULL_DYNAMIC_TEST, NOT_NULL_DYNAMIC_TEST));
  }

  // WITH nested DynamicContainer

  @TestFactory
  DynamicContainer should_NotSetAccessIdForNestedDynamicContainer_When_AnnotationIsMissing() {
    DynamicContainer container =
        dynamicContainer("inside container", Stream.of(NULL_DYNAMIC_TEST, NULL_DYNAMIC_TEST));
    return dynamicContainer("outside container", Stream.of(container, NULL_DYNAMIC_TEST));
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  DynamicContainer should_SetAccessIdForNestedDynamicContainer_When_AnnotationExists() {
    DynamicContainer container =
        dynamicContainer(
            "nested container",
            Stream.of(DYNAMIC_TEST_USER_DYNAMIC_TEST, DYNAMIC_TEST_USER_DYNAMIC_TEST));
    return dynamicContainer(
        "outside container", Stream.of(container, DYNAMIC_TEST_USER_DYNAMIC_TEST));
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  DynamicContainer should_SetMultipleAccessIdForNestedDynamicContainer_When_AnnotationsExist() {
    DynamicContainer container =
        dynamicContainer(
            "inside container", Stream.of(NOT_NULL_DYNAMIC_TEST, NOT_NULL_DYNAMIC_TEST));
    return dynamicContainer("outside container", Stream.of(container, NOT_NULL_DYNAMIC_TEST));
  }

  // endregion

  // region RETURNING Stream<DynamicNode>

  @TestFactory
  Stream<DynamicTest> should_NotSetAccessIdForDynamicTestInStream_When_AnnotationIsMissing() {
    return Stream.of(NULL_DYNAMIC_TEST, NULL_DYNAMIC_TEST);
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Stream<DynamicTest> should_SetAccessIdForDynamicTestInStream_When_AnnotationExists() {
    return Stream.of(DYNAMIC_TEST_USER_DYNAMIC_TEST, DYNAMIC_TEST_USER_DYNAMIC_TEST);
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Stream<DynamicTest> should_SetMultipleAccessIdForDynamicTestInStream_When_AnnotationsExist() {
    return Stream.of(NOT_NULL_DYNAMIC_TEST, NOT_NULL_DYNAMIC_TEST);
  }

  // WITH DynamicContainer

  @TestFactory
  Stream<DynamicContainer>
      should_NotSetAccessIdForDynamicContainerInStream_When_AnnotationIsMissing() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer("dynamic container", Stream.of(NULL_DYNAMIC_TEST, NULL_DYNAMIC_TEST));
    return Stream.generate(supplier).limit(2);
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Stream<DynamicContainer> should_SetAccessIdForDynamicContainerInStream_When_AnnotationExists() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "dynamic container",
                Stream.of(DYNAMIC_TEST_USER_DYNAMIC_TEST, DYNAMIC_TEST_USER_DYNAMIC_TEST));
    return Stream.generate(supplier).limit(2);
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Stream<DynamicContainer>
      should_SetMultipleAccessIdForDynamicContainerInStream_When_AnnotationsExist() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "dynamic container", Stream.of(NOT_NULL_DYNAMIC_TEST, NOT_NULL_DYNAMIC_TEST));
    return Stream.generate(supplier).limit(2);
  }

  // WITH nested DynamicContainer

  @TestFactory
  Stream<DynamicContainer>
      should_NotSetAccessIdForNestedDynamicContainerInStream_When_AnnotationIsMissing() {
    Supplier<DynamicContainer> supplier =
        () -> dynamicContainer("inside container", Stream.of(NULL_DYNAMIC_TEST, NULL_DYNAMIC_TEST));
    Supplier<DynamicContainer> outsideSupplier =
        () -> dynamicContainer("outside container", Stream.of(supplier.get(), NULL_DYNAMIC_TEST));
    return Stream.generate(outsideSupplier).limit(2);
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Stream<DynamicContainer>
      should_SetAccessIdForNestedDynamicContainerInStream_When_AnnotationExists() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "inside container",
                Stream.of(DYNAMIC_TEST_USER_DYNAMIC_TEST, DYNAMIC_TEST_USER_DYNAMIC_TEST));
    Supplier<DynamicContainer> outsideSupplier =
        () ->
            dynamicContainer(
                "outside container", Stream.of(supplier.get(), DYNAMIC_TEST_USER_DYNAMIC_TEST));
    return Stream.generate(outsideSupplier).limit(2);
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Stream<DynamicContainer>
      should_SetMultipleAccessIdForNestedDynamicContainerInStream_When_AnnotationsExist() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "inside container", Stream.of(NOT_NULL_DYNAMIC_TEST, NOT_NULL_DYNAMIC_TEST));
    Supplier<DynamicContainer> outsideSupplier =
        () ->
            dynamicContainer("outside container", Stream.of(supplier.get(), NOT_NULL_DYNAMIC_TEST));
    return Stream.generate(outsideSupplier).limit(2);
  }

  // endregion

  // region RETURNING Iterable<DynamicNode>

  @TestFactory
  Iterable<DynamicTest> should_NotSetAccessIdForDynamicTestInIterable_When_AnnotationIsMissing() {
    return Stream.of(NULL_DYNAMIC_TEST, NULL_DYNAMIC_TEST).collect(Collectors.toList());
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Iterable<DynamicTest> should_SetAccessIdForDynamicTestInIterable_When_AnnotationExists() {
    return Stream.of(DYNAMIC_TEST_USER_DYNAMIC_TEST, DYNAMIC_TEST_USER_DYNAMIC_TEST)
        .collect(Collectors.toList());
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Iterable<DynamicTest> should_SetMultipleAccessIdForDynamicTestInIterable_When_AnnotationsExist() {
    return Stream.of(NOT_NULL_DYNAMIC_TEST, NOT_NULL_DYNAMIC_TEST).collect(Collectors.toList());
  }

  // WITH DynamicContainer

  @TestFactory
  Iterable<DynamicContainer>
      should_NotSetAccessIdForDynamicContainerInIterable_When_AnnotationIsMissing() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer("dynamic container", Stream.of(NULL_DYNAMIC_TEST, NULL_DYNAMIC_TEST));
    return Stream.generate(supplier).limit(2).collect(Collectors.toList());
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Iterable<DynamicContainer>
      should_SetAccessIdForDynamicContainerInIterable_When_AnnotationExists() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "dynamic container",
                Stream.of(DYNAMIC_TEST_USER_DYNAMIC_TEST, DYNAMIC_TEST_USER_DYNAMIC_TEST));
    return Stream.generate(supplier).limit(2).collect(Collectors.toList());
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Iterable<DynamicContainer>
      should_SetMultipleAccessIdForDynamicContainerInIterable_When_AnnotationsExist() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "dynamic container", Stream.of(NOT_NULL_DYNAMIC_TEST, NOT_NULL_DYNAMIC_TEST));
    return Stream.generate(supplier).limit(2).collect(Collectors.toList());
  }

  // WITH nested DynamicContainer

  @TestFactory
  Iterable<DynamicContainer>
      should_NotSetAccessIdForNestedDynamicContainerInIterable_When_AnnotationIsMissing() {
    Supplier<DynamicContainer> supplier =
        () -> dynamicContainer("inside container", Stream.of(NULL_DYNAMIC_TEST, NULL_DYNAMIC_TEST));
    Supplier<DynamicContainer> outsideSupplier =
        () -> dynamicContainer("outside container", Stream.of(supplier.get(), NULL_DYNAMIC_TEST));
    return Stream.generate(outsideSupplier).limit(2).collect(Collectors.toList());
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Iterable<DynamicContainer>
      should_SetAccessIdForNestedDynamicContainerInIterable_When_AnnotationExists() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "inside container",
                Stream.of(DYNAMIC_TEST_USER_DYNAMIC_TEST, DYNAMIC_TEST_USER_DYNAMIC_TEST));
    Supplier<DynamicContainer> outsideSupplier =
        () ->
            dynamicContainer(
                "outside container", Stream.of(supplier.get(), DYNAMIC_TEST_USER_DYNAMIC_TEST));
    return Stream.generate(outsideSupplier).limit(2).collect(Collectors.toList());
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Iterable<DynamicContainer>
      should_SetMultipleAccessIdForNestedDynamicContainerInIterable_When_AnnotationsExist() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "inside container", Stream.of(NOT_NULL_DYNAMIC_TEST, NOT_NULL_DYNAMIC_TEST));
    Supplier<DynamicContainer> outsideSupplier =
        () ->
            dynamicContainer("outside container", Stream.of(supplier.get(), NOT_NULL_DYNAMIC_TEST));
    return Stream.generate(outsideSupplier).limit(2).collect(Collectors.toList());
  }

  // endregion

  // region RETURNING Iterator<DynamicNode>

  @TestFactory
  Iterator<DynamicTest> should_NotSetAccessIdForDynamicTestInIterator_When_AnnotationIsMissing() {
    return Stream.of(NULL_DYNAMIC_TEST, NULL_DYNAMIC_TEST).iterator();
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Iterator<DynamicTest> should_SetAccessIdForDynamicTestInIterator_When_AnnotationExists() {
    return Stream.of(DYNAMIC_TEST_USER_DYNAMIC_TEST, DYNAMIC_TEST_USER_DYNAMIC_TEST).iterator();
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Iterator<DynamicTest> should_SetMultipleAccessIdForDynamicTestInIterator_When_AnnotationsExist() {
    return Stream.of(NOT_NULL_DYNAMIC_TEST, NOT_NULL_DYNAMIC_TEST).iterator();
  }

  // WITH DynamicContainer

  @TestFactory
  Iterator<DynamicContainer>
      should_NotSetAccessIdForDynamicContainerInIterator_When_AnnotationIsMissing() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer("dynamic container", Stream.of(NULL_DYNAMIC_TEST, NULL_DYNAMIC_TEST));
    return Stream.generate(supplier).limit(2).iterator();
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Iterator<DynamicContainer>
      should_SetAccessIdForDynamicContainerInIterator_When_AnnotationExists() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "dynamic container",
                Stream.of(DYNAMIC_TEST_USER_DYNAMIC_TEST, DYNAMIC_TEST_USER_DYNAMIC_TEST));
    return Stream.generate(supplier).limit(2).iterator();
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Iterator<DynamicContainer>
      should_SetMultipleAccessIdForDynamicContainerInIterator_When_AnnotationsExist() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "dynamic container", Stream.of(NOT_NULL_DYNAMIC_TEST, NOT_NULL_DYNAMIC_TEST));
    return Stream.generate(supplier).limit(2).iterator();
  }

  // WITH nested DynamicContainer

  @TestFactory
  Iterator<DynamicContainer>
      should_NotSetAccessIdForNestedDynamicContainerInIterator_When_AnnotationIsMissing() {
    Supplier<DynamicContainer> supplier =
        () -> dynamicContainer("inside container", Stream.of(NULL_DYNAMIC_TEST, NULL_DYNAMIC_TEST));
    Supplier<DynamicContainer> outsideSupplier =
        () -> dynamicContainer("outside container", Stream.of(supplier.get(), NULL_DYNAMIC_TEST));
    return Stream.generate(outsideSupplier).limit(2).iterator();
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Iterator<DynamicContainer>
      should_SetAccessIdForNestedDynamicContainerInIterator_When_AnnotationExists() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "inside container",
                Stream.of(DYNAMIC_TEST_USER_DYNAMIC_TEST, DYNAMIC_TEST_USER_DYNAMIC_TEST));
    Supplier<DynamicContainer> outsideSupplier =
        () ->
            dynamicContainer(
                "outside container", Stream.of(supplier.get(), DYNAMIC_TEST_USER_DYNAMIC_TEST));
    return Stream.generate(outsideSupplier).limit(2).iterator();
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  Iterator<DynamicContainer>
      should_SetMultipleAccessIdForNestedDynamicContainerInIterator_When_AnnotationsExist() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "inside container", Stream.of(NOT_NULL_DYNAMIC_TEST, NOT_NULL_DYNAMIC_TEST));
    Supplier<DynamicContainer> outsideSupplier =
        () ->
            dynamicContainer("outside container", Stream.of(supplier.get(), NOT_NULL_DYNAMIC_TEST));
    return Stream.generate(outsideSupplier).limit(2).iterator();
  }

  // endregion

  // region RETURNING DynamicNode[]

  @TestFactory
  DynamicTest[] should_NotSetAccessIdForDynamicTestInArray_When_AnnotationIsMissing() {
    return Stream.of(NULL_DYNAMIC_TEST, NULL_DYNAMIC_TEST).toArray(DynamicTest[]::new);
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  DynamicTest[] should_SetAccessIdForDynamicTestInArray_When_AnnotationExists() {
    return Stream.of(DYNAMIC_TEST_USER_DYNAMIC_TEST, DYNAMIC_TEST_USER_DYNAMIC_TEST)
        .toArray(DynamicTest[]::new);
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  DynamicTest[] should_SetMultipleAccessIdForDynamicTestInArray_When_AnnotationsExist() {
    return Stream.of(NOT_NULL_DYNAMIC_TEST, NOT_NULL_DYNAMIC_TEST).toArray(DynamicTest[]::new);
  }

  // WITH DynamicContainer

  @TestFactory
  DynamicContainer[] should_NotSetAccessIdForDynamicContainerInArray_When_AnnotationIsMissing() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer("dynamic container", Stream.of(NULL_DYNAMIC_TEST, NULL_DYNAMIC_TEST));
    return Stream.generate(supplier).limit(2).toArray(DynamicContainer[]::new);
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  DynamicContainer[] should_SetAccessIdForDynamicContainerInArray_When_AnnotationExists() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "dynamic container",
                Stream.of(DYNAMIC_TEST_USER_DYNAMIC_TEST, DYNAMIC_TEST_USER_DYNAMIC_TEST));
    return Stream.generate(supplier).limit(2).toArray(DynamicContainer[]::new);
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  DynamicContainer[] should_SetMultipleAccessIdForDynamicContainerInArray_When_AnnotationsExist() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "dynamic container", Stream.of(NOT_NULL_DYNAMIC_TEST, NOT_NULL_DYNAMIC_TEST));
    return Stream.generate(supplier).limit(2).toArray(DynamicContainer[]::new);
  }

  // WITH nested DynamicContainer

  @TestFactory
  DynamicContainer[]
      should_NotSetAccessIdForNestedDynamicContainerInArray_When_AnnotationIsMissing() {
    Supplier<DynamicContainer> supplier =
        () -> dynamicContainer("inside container", Stream.of(NULL_DYNAMIC_TEST, NULL_DYNAMIC_TEST));
    Supplier<DynamicContainer> outsideSupplier =
        () -> dynamicContainer("outside container", Stream.of(supplier.get(), NULL_DYNAMIC_TEST));
    return Stream.generate(outsideSupplier).limit(2).toArray(DynamicContainer[]::new);
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  DynamicContainer[] should_SetAccessIdForNestedDynamicContainerInArray_When_AnnotationExists() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "inside container",
                Stream.of(DYNAMIC_TEST_USER_DYNAMIC_TEST, DYNAMIC_TEST_USER_DYNAMIC_TEST));
    Supplier<DynamicContainer> outsideSupplier =
        () ->
            dynamicContainer(
                "outside container", Stream.of(supplier.get(), DYNAMIC_TEST_USER_DYNAMIC_TEST));
    return Stream.generate(outsideSupplier).limit(2).toArray(DynamicContainer[]::new);
  }

  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @WithAccessId(user = INSIDE_DYNAMIC_TEST_USER)
  @TestFactory
  DynamicContainer[]
      should_SetMultipleAccessIdForNestedDynamicContainerInArray_When_AnnotationsExist() {
    Supplier<DynamicContainer> supplier =
        () ->
            dynamicContainer(
                "inside container", Stream.of(NOT_NULL_DYNAMIC_TEST, NOT_NULL_DYNAMIC_TEST));
    Supplier<DynamicContainer> outsideSupplier =
        () ->
            dynamicContainer("outside container", Stream.of(supplier.get(), NOT_NULL_DYNAMIC_TEST));
    return Stream.generate(outsideSupplier).limit(2).toArray(DynamicContainer[]::new);
  }

  // endregion

  // region JaasExtension#interceptTestClassConstructor

  @Nested
  class ConstructorWithoutAccessId {
    ConstructorWithoutAccessId() {
      assertThat(CURRENT_USER_CONTEXT.getUserid()).isNull();
    }

    @Test
    void should_NotSetJaasSubject_When_AnnotationIsMissing_On_Constructor() {
      assertThat(CURRENT_USER_CONTEXT.getUserid()).isNull();
    }
  }

  @Nested
  class ConstructorWithAccessId {
    @WithAccessId(user = "constructor")
    ConstructorWithAccessId() {
      assertThat(CURRENT_USER_CONTEXT.getUserid()).isEqualTo("constructor");
    }

    @Test
    void should_SetJaasSubject_When_AnnotationExists_On_Constructor() {
      assertThat(CURRENT_USER_CONTEXT.getUserid()).isNull();
    }
  }

  // endregion

  // endregion
}
