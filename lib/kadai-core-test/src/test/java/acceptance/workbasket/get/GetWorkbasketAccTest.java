package acceptance.workbasket.get;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_1;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_2;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_3;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_4;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_5;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_6;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_7;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.internal.util.Triplet;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.builder.WorkbasketBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.exceptions.WorkbasketNotFoundException;
import io.kadai.workbasket.api.models.Workbasket;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.function.ThrowingConsumer;

@KadaiIntegrationTest
class GetWorkbasketAccTest {
  @KadaiInject ClassificationService classificationService;
  @KadaiInject WorkbasketService workbasketService;
  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary =
        WorkbasketBuilder.newWorkbasket()
            .domain("DOMAIN_A")
            .description("PPK User 2 KSC 1")
            .name("PPK User 2 KSC 1")
            .key("USER-1-2")
            .type(WorkbasketType.PERSONAL)
            .owner("user-1-2")
            .orgLevel1("versicherung")
            .orgLevel2("abteilung")
            .orgLevel3("projekt")
            .orgLevel4("team")
            .customAttribute(CUSTOM_1, "custom1")
            .customAttribute(CUSTOM_2, "custom2")
            .customAttribute(CUSTOM_3, "custom3")
            .customAttribute(CUSTOM_4, "custom4")
            .customAttribute(CUSTOM_5, "custom5")
            .customAttribute(CUSTOM_6, "custom6")
            .customAttribute(CUSTOM_7, "custom7")
            .customAttribute(CUSTOM_8, "custom8")
            .buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.TRANSFER)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    defaultObjectReference = defaultTestObjectReference().build();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ReturnWorkbasketWithId_When_IdIsValidAndUserHasPermissions() throws Exception {
    Workbasket workbasket = workbasketService.getWorkbasket(defaultWorkbasketSummary.getId());

    assertThat(workbasket.asSummary()).isEqualTo(defaultWorkbasketSummary);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "businessadmin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ReturnWorkbasketById_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {
    Workbasket workbasket = workbasketService.getWorkbasket(defaultWorkbasketSummary.getId());

    assertThat(workbasket.asSummary()).isEqualTo(defaultWorkbasketSummary);
  }

  @WithAccessId(user = "user-1-1", groups = "user-1-2")
  @Test
  void should_ReturnWorkbasketPermissions_When_IdIsValidAndUserHasPermissions() {
    List<WorkbasketPermission> permissions =
        workbasketService.getPermissionsForWorkbasket(defaultWorkbasketSummary.getId());

    assertThat(permissions)
        .containsExactlyInAnyOrder(
            WorkbasketPermission.READ,
            WorkbasketPermission.APPEND,
            WorkbasketPermission.TRANSFER,
            WorkbasketPermission.OPEN);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ReturnNoWorkbasketPermissions_When_ProvidingAnInvalidId() {
    List<WorkbasketPermission> permissions =
        workbasketService.getPermissionsForWorkbasket("WBI:invalid");

    assertThat(permissions).isEmpty();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ReturnWorkbasketSummary_When_IdIsValidAndSummaryIsRequested() throws Exception {
    WorkbasketSummary workbasketSummary =
        workbasketService.getWorkbasket(defaultWorkbasketSummary.getId()).asSummary();

    assertThat(workbasketSummary).isEqualTo(defaultWorkbasketSummary);
  }

  @Test
  void should_ThrowException_When_ProvidingAnInvalidId() {
    ThrowingCallable call = () -> workbasketService.getWorkbasket("INVALID_ID");

    WorkbasketNotFoundException e = catchThrowableOfType(WorkbasketNotFoundException.class, call);

    assertThat(e.getId()).isEqualTo("INVALID_ID");
  }

  @TestFactory
  Stream<DynamicTest> should_ThrowException_When_KeyOrDomainIsInvalid() {
    List<Triplet<String, String, String>> list =
        List.of(
            Triplet.of("With Invalid Domain", "USER-1-2", "INVALID_DOMAIN"),
            Triplet.of("With Invalid Key", "INVALID_ID", "DOMAIN_A"),
            Triplet.of("With Invalid Key and Domain", "INAVLID_ID", "INVALID_DOMAIN"));
    ThrowingConsumer<Triplet<String, String, String>> testGetWorkbasket =
        t -> {
          ThrowingCallable call =
              () -> workbasketService.getWorkbasket(t.getMiddle(), t.getRight());

          WorkbasketNotFoundException e =
              catchThrowableOfType(WorkbasketNotFoundException.class, call);

          assertThat(e.getKey()).isEqualTo(t.getMiddle());
          assertThat(e.getDomain()).isEqualTo(t.getRight());
        };
    return DynamicTest.stream(list.iterator(), Triplet::getLeft, testGetWorkbasket);
  }

  @Test
  void should_ThrowException_When_TryingToGetByIdWithoutPermissions() {
    ThrowingCallable call = () -> workbasketService.getWorkbasket(defaultWorkbasketSummary.getId());

    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call);

    assertThat(e.getWorkbasketId()).isEqualTo(defaultWorkbasketSummary.getId());
    assertThat(e.getCurrentUserId()).isNull();
    assertThat(e.getRequiredPermissions()).containsExactly(WorkbasketPermission.READ);
  }

  @Test
  void should_ThrowException_When_TryingToGetByKeyAndDomainWithoutPermissions() {
    ThrowingCallable call = () -> workbasketService.getWorkbasket("USER-1-2", "DOMAIN_A");

    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(NotAuthorizedOnWorkbasketException.class, call);

    assertThat(e.getWorkbasketKey()).isEqualTo("USER-1-2");
    assertThat(e.getDomain()).isEqualTo("DOMAIN_A");
    assertThat(e.getCurrentUserId()).isNull();
    assertThat(e.getRequiredPermissions()).containsExactly(WorkbasketPermission.READ);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_TryingToGetWithAnInvalidId() {
    ThrowingCallable call = () -> workbasketService.getWorkbasket("NOT EXISTING ID");

    WorkbasketNotFoundException e = catchThrowableOfType(WorkbasketNotFoundException.class, call);

    assertThat(e.getId()).isEqualTo("NOT EXISTING ID");
  }
}
