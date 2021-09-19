package acceptance.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static pro.taskana.common.internal.util.CheckedSupplier.wrap;
import static pro.taskana.workbasket.internal.builder.WorkbasketBuilder.newWorkbasket;

import java.time.Instant;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import testapi.TaskanaInject;
import testapi.TaskanaIntegrationTest;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.util.Quadruple;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.internal.builder.WorkbasketBuilder;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;

@TaskanaIntegrationTest
class WorkbasketBuilderTest {

  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject TaskanaEngine taskanaEngine;

  @WithAccessId(user = "businessadmin")
  @Test
  void should_PersistWorkbasket_When_UsingWorkbasketBuilder() throws Exception {
    Workbasket workbasket =
        newWorkbasket()
            .key("key0_G")
            .domain("DOMAIN_A")
            .name("Megabasket")
            .type(WorkbasketType.GROUP)
            .buildAndStore(workbasketService);
    Workbasket receivedWorkbasket = workbasketService.getWorkbasket(workbasket.getId());
    assertThat(receivedWorkbasket).isEqualTo(workbasket);
  }

  @Test
  void should_PersistWorkbasketAsUser_When_UsingWorkbasketBuilder() throws Exception {
    Workbasket workbasket =
        newWorkbasket()
            .domain("DOMAIN_A")
            .description("PPK User 2 KSC 1")
            .name("PPK User 2 KSC 1")
            .key("key1_G")
            .type(WorkbasketType.GROUP)
            .buildAndStore(workbasketService, "businessadmin");

    Workbasket receivedWorkbasket =
        taskanaEngine.runAsAdmin(wrap(() -> workbasketService.getWorkbasket(workbasket.getId())));
    assertThat(receivedWorkbasket).isEqualTo(workbasket);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_PopulateWorkbasket_When_UsingEveryBuilderFunction() throws Exception {
    final Workbasket workbasket =
        newWorkbasket()
            .key("new_workbasket_key")
            .name("This is a new workbasket")
            .description("A test description")
            .owner("user-1-1")
            .domain("DOMAIN_A")
            .type(WorkbasketType.PERSONAL)
            .customAttribute(WorkbasketCustomField.CUSTOM_1, "custom 1 value")
            .customAttribute(WorkbasketCustomField.CUSTOM_2, "custom 2 value")
            .customAttribute(WorkbasketCustomField.CUSTOM_3, "custom 3 value")
            .customAttribute(WorkbasketCustomField.CUSTOM_4, "custom 4 value")
            .orgLevel1("org level 1")
            .orgLevel2("org level 2")
            .orgLevel3("org level 3")
            .orgLevel4("org level 4")
            .markedForDeletion(true)
            .created(Instant.parse("2021-05-17T07:16:26.747Z"))
            .modified(Instant.parse("2021-05-18T07:16:26.747Z"))
            .buildAndStore(workbasketService);

    WorkbasketImpl expectedWorkbasket =
        (WorkbasketImpl) workbasketService.newWorkbasket("new_workbasket_key", "DOMAIN_A");
    expectedWorkbasket.setName("This is a new workbasket");
    expectedWorkbasket.setDescription("A test description");
    expectedWorkbasket.setOwner("user-1-1");
    expectedWorkbasket.setType(WorkbasketType.PERSONAL);
    expectedWorkbasket.setCustomAttribute(WorkbasketCustomField.CUSTOM_1, "custom 1 value");
    expectedWorkbasket.setCustomAttribute(WorkbasketCustomField.CUSTOM_2, "custom 2 value");
    expectedWorkbasket.setCustomAttribute(WorkbasketCustomField.CUSTOM_3, "custom 3 value");
    expectedWorkbasket.setCustomAttribute(WorkbasketCustomField.CUSTOM_4, "custom 4 value");
    expectedWorkbasket.setOrgLevel1("org level 1");
    expectedWorkbasket.setOrgLevel2("org level 2");
    expectedWorkbasket.setOrgLevel3("org level 3");
    expectedWorkbasket.setOrgLevel4("org level 4");
    expectedWorkbasket.setMarkedForDeletion(true);
    expectedWorkbasket.setCreated(Instant.parse("2021-05-17T07:16:26.747Z"));
    expectedWorkbasket.setModified(Instant.parse("2021-05-18T07:16:26.747Z"));

    assertThat(workbasket)
        .hasNoNullFieldsOrProperties()
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(expectedWorkbasket);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ResetWorkbasketId_When_StoringWorkbasketMultipleTimes() {
    Instant created = Instant.parse("2021-05-17T07:16:25.747Z");

    WorkbasketBuilder builder =
        newWorkbasket()
            .domain("DOMAIN_A")
            .name("Megabasket")
            .type(WorkbasketType.GROUP)
            .created(created);
    assertThatCode(
            () -> {
              builder.key("key4_G").buildAndStore(workbasketService);
              builder.key("key5_G").buildAndStore(workbasketService);
            })
        .doesNotThrowAnyException();
  }

  @WithAccessId(user = "businessadmin")
  @TestFactory
  Stream<DynamicContainer> should_PersistWorkbasket_When_CreatingEntityWithInvalidApiValues() {
    List<
            Quadruple<
                String,
                Object,
                BiFunction<WorkbasketBuilder, Object, WorkbasketBuilder>,
                Function<Workbasket, Object>>>
        list =
            List.of(
                Quadruple.of(
                    "created",
                    Instant.parse("2020-05-17T07:16:26.747Z"),
                    (b, v) -> b.created((Instant) v),
                    Workbasket::getCreated),
                Quadruple.of(
                    "modified",
                    Instant.parse("2019-05-17T07:16:26.747Z"),
                    (b, v) -> b.modified((Instant) v),
                    Workbasket::getModified));

    Stream<DynamicTest> applyBuilderFunction =
        DynamicTest.stream(
            list.iterator(),
            q -> String.format("for field: '%s'", q.getFirst()),
            q -> applyBuilderFunctionAndVerifyValue(q.getSecond(), q.getThird(), q.getFourth()));

    Stream<DynamicTest> overrideBuilderFunctionWithApiDefault =
        DynamicTest.stream(
            list.iterator(),
            q -> String.format("for field: '%s'", q.getFirst()),
            t -> applyAndOverrideWithApiDefaultValue(t.getSecond(), t.getThird(), t.getFourth()));

    return Stream.of(
        DynamicContainer.dynamicContainer(
            "set values which are invalid through API", applyBuilderFunction),
        DynamicContainer.dynamicContainer(
            "override with API default value", overrideBuilderFunctionWithApiDefault));
  }

  private <T> void applyBuilderFunctionAndVerifyValue(
      T value,
      BiFunction<WorkbasketBuilder, T, WorkbasketBuilder> builderfunction,
      Function<Workbasket, T> retriever)
      throws Exception {
    WorkbasketBuilder builder =
        newWorkbasket()
            .domain("DOMAIN_A")
            .name("workbasketName")
            .type(WorkbasketType.PERSONAL)
            .key("A" + builderfunction.hashCode());

    builderfunction.apply(builder, value);
    Workbasket classification = builder.buildAndStore(workbasketService);
    T retrievedValue = retriever.apply(classification);

    assertThat(retrievedValue).isEqualTo(value);
  }

  private <T> void applyAndOverrideWithApiDefaultValue(
      T value,
      BiFunction<WorkbasketBuilder, T, WorkbasketBuilder> builderfunction,
      Function<Workbasket, T> retriever)
      throws Exception {
    WorkbasketBuilder builder =
        newWorkbasket()
            .domain("DOMAIN_A")
            .name("workbasketName")
            .type(WorkbasketType.PERSONAL)
            .key("B" + builderfunction.hashCode());

    builderfunction.apply(builder, value);
    builderfunction.apply(builder, null);

    Workbasket classification = builder.buildAndStore(workbasketService);
    T retrievedValue = retriever.apply(classification);

    assertThat(retrievedValue).isNotNull().isNotEqualTo(value);
  }
}
