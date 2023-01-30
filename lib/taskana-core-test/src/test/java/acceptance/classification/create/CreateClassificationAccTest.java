package acceptance.classification.create;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.api.SharedConstants.MASTER_DOMAIN;

import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.exceptions.MalformedServiceLevelException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.security.WithAccessId;

/** Acceptance test for all "create classification" scenarios. */
@TaskanaIntegrationTest
class CreateClassificationAccTest {

  @TaskanaInject ClassificationService classificationService;

  @WithAccessId(user = "businessadmin")
  @Test
  void should_OnlyCreateOneClassification_When_CreatingMasterClassification() throws Exception {
    Classification classification =
        classificationService.newClassification("Key0", MASTER_DOMAIN, "TASK");

    classification = classificationService.createClassification(classification);

    List<ClassificationSummary> classifications =
        classificationService.createClassificationQuery().keyIn("Key0").list();
    assertThat(classifications).containsExactly(classification.asSummary());
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_CreateMasterClassification_When_CreatingClassificationWithDomain() throws Exception {
    Classification classification =
        classificationService.newClassification("Key1", "DOMAIN_A", "TASK");

    classificationService.createClassification(classification);

    ClassificationImpl expectedMasterClassification =
        (ClassificationImpl) classification.copy("Key1");
    expectedMasterClassification.setDomain(MASTER_DOMAIN);

    List<ClassificationSummary> classifications =
        classificationService.createClassificationQuery().keyIn("Key1").list();

    assertThat(classifications)
        .allMatch(c -> nonNull(c.getId()))
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
        .containsExactlyInAnyOrder(
            classification.asSummary(), expectedMasterClassification.asSummary());
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_NotCreateMasterClassification_When_OneAlreadyExists() throws Exception {
    @SuppressWarnings("unused")
    Classification masterClassification =
        DefaultTestEntities.defaultTestClassification()
            .key("Key2")
            .domain(MASTER_DOMAIN)
            .type("TASK")
            .buildAndStore(classificationService);

    Instant before = Instant.now();
    //  Sometimes the test execution is too fast. Therefore, we have to slow it down.
    Thread.sleep(10);

    Classification classification =
        classificationService.newClassification("Key2", "DOMAIN_B", "TASK");
    classificationService.createClassification(classification);

    List<ClassificationSummary> classifications =
        classificationService
            .createClassificationQuery()
            .keyIn("Key2")
            .modifiedWithin(new TimeInterval(before, null))
            .list();

    assertThat(classifications).containsExactly(classification.asSummary());
  }

  @WithAccessId(user = "businessadmin")
  @TestFactory
  Stream<DynamicTest> should_CreateChildClassification() throws Exception {
    ClassificationSummary parentClassification =
        DefaultTestEntities.defaultTestClassification()
            .key("Key3")
            .domain("DOMAIN_A")
            .type("TASK")
            .buildAndStoreAsSummary(classificationService);

    List<Pair<String, Consumer<Classification>>> setterList =
        List.of(
            Pair.of("parent key", p -> p.setParentKey(parentClassification.getKey())),
            Pair.of("parent id", p -> p.setParentId(parentClassification.getId())));

    AtomicInteger i = new AtomicInteger();

    ThrowingConsumer<Pair<String, Consumer<Classification>>> test =
        pair -> {
          Classification childClassification =
              classificationService.newClassification("Key3_" + i.get(), "DOMAIN_A", "TASK");
          pair.getRight().accept(childClassification);

          classificationService.createClassification(childClassification);

          List<ClassificationSummary> classifications =
              classificationService
                  .createClassificationQuery()
                  .keyIn("Key3_" + i.getAndIncrement())
                  .list();

          assertThat(classifications)
              .allMatch(c -> nonNull(c.getId()))
              .contains(childClassification.asSummary());
        };

    return DynamicTest.stream(
        setterList.iterator(), p -> String.format("for %s", p.getLeft()), test);
  }

  @WithAccessId(user = "businessadmin")
  @TestFactory
  Stream<DynamicTest>
      should_ThrowException_When_TryingToCreateClassificationWithInvalidServiceLevel() {
    Iterator<String> iterator = Arrays.asList("P-1D", "abc").iterator();
    ThrowingConsumer<String> test =
        invalidServiceLevel -> {
          Classification classification =
              classificationService.newClassification("KeyErrCreation", "DOMAIN_A", "TASK");
          classification.setServiceLevel(invalidServiceLevel);
          MalformedServiceLevelException expectedException =
              new MalformedServiceLevelException(invalidServiceLevel, "KeyErrCreation", "DOMAIN_A");

          assertThatThrownBy(() -> classificationService.createClassification(classification))
              .isInstanceOf(MalformedServiceLevelException.class)
              .usingRecursiveComparison()
              .isEqualTo(expectedException);
        };

    return DynamicTest.stream(iterator, c -> String.format("for '%s'", c), test);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowException_When_TryingToCreateClassificationWithInvalidKey() {
    Classification classificationWithNullKey =
        classificationService.newClassification(null, "DOMAIN_A", "TASK");

    assertThatThrownBy(() -> classificationService.createClassification(classificationWithNullKey))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("Classification must contain a key");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowException_When_TryingToCreateClassificationWithInvalidDomain() {
    Classification classification =
        classificationService.newClassification("KeyErrCreation", "UNKNOWN_DOMAIN", "TASK");
    DomainNotFoundException expectedException = new DomainNotFoundException("UNKNOWN_DOMAIN");

    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(DomainNotFoundException.class)
        .usingRecursiveComparison()
        .isEqualTo(expectedException);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowException_When_TryingToCreateClassificationWithInvalidType() {
    Classification classification =
        classificationService.newClassification("KeyErrCreation", "DOMAIN_A", "UNKNOWN_TYPE");

    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage(
            "Given classification type "
                + "UNKNOWN_TYPE"
                + " is not valid according to the configuration.");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowException_When_TryingToCreateClassificationWithInvalidCategory() {
    Classification classification =
        classificationService.newClassification("KeyErrCreation", "DOMAIN_A", "TASK");
    classification.setCategory("UNKNOWN_CATEGORY");

    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage(
            "Given classification category "
                + "UNKNOWN_CATEGORY"
                + " with type TASK is not valid according to the configuration.");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowException_When_TryingToCreateClassificationWithInvalidParentKey() {
    Classification classification =
        classificationService.newClassification("KeyErrCreation", MASTER_DOMAIN, "TASK");
    classification.setParentKey("UNKNOWN_KEY");

    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("Parent classification could not be found.");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowException_When_TryingToCreateClassificationWithInvalidParentId() {
    Classification classification =
        classificationService.newClassification("KeyErr", MASTER_DOMAIN, "TASK");
    classification.setParentId("UNKNOWN_ID");

    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("Parent classification could not be found.");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowException_When_TryingToCreateClassificationWithExplicitId() {
    ClassificationImpl classification =
        (ClassificationImpl)
            classificationService.newClassification("KeyErrCreation", MASTER_DOMAIN, "TASK");
    classification.setId("EXPLICIT ID");

    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("ClassificationId should be null on creation");
  }

  @WithAccessId(user = "taskadmin")
  @WithAccessId(user = "user-1-1")
  @TestTemplate
  void should_ThrowException_When_UserRoleIsNotAdminOrBusinessAdmin(WithAccessId accessId) {
    Classification classification =
        classificationService.newClassification("KeyErrCreation", MASTER_DOMAIN, "TASK");
    MismatchedRoleException expectedException =
        new MismatchedRoleException(accessId.user(), TaskanaRole.BUSINESS_ADMIN, TaskanaRole.ADMIN);

    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(MismatchedRoleException.class)
        .usingRecursiveComparison()
        .isEqualTo(expectedException);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ThrowException_When_ClassificationWithKeyAlreadyExisting() throws Exception {
    String existingKey = "Key4";
    DefaultTestEntities.defaultTestClassification()
        .key(existingKey)
        .buildAndStore(classificationService);
    ClassificationAlreadyExistException expectedException =
        new ClassificationAlreadyExistException(existingKey, "DOMAIN_A");

    Classification classification =
        classificationService.newClassification(existingKey, "DOMAIN_A", "TASK");

    assertThatThrownBy(() -> classificationService.createClassification(classification))
        .isInstanceOf(ClassificationAlreadyExistException.class)
        .usingRecursiveComparison()
        .isEqualTo(expectedException);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_SetDefaultServiceLevel_When_TryingToCreateClassificationWithEmptyServiceLevel()
      throws Exception {
    Classification classification =
        classificationService.newClassification("Key5", MASTER_DOMAIN, "TASK");
    classification.setServiceLevel("");

    classification = classificationService.createClassification(classification);

    assertThat(classification.getServiceLevel()).isEqualTo("P0D");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_SetDefaultValues_When_CreatingClassificationWithoutSpecificValues() throws Exception {
    Classification classification =
        classificationService.newClassification("Key6", MASTER_DOMAIN, "TASK");
    classification = classificationService.createClassification(classification);

    assertThat(classification.getServiceLevel()).isEqualTo("P0D");
    assertThat(classification.getId()).isNotNull();
    assertThat(classification.getId()).isNotEmpty();
    assertThat(classification.getCreated()).isNotNull();
    assertThat(classification.getModified()).isNotNull();
    assertThat(classification.getParentId()).isEmpty();
    assertThat(classification.getParentKey()).isEmpty();
    assertThat(classification.getIsValidInDomain()).isFalse();
  }
}
