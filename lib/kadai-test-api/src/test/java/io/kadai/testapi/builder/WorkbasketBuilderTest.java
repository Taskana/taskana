package io.kadai.testapi.builder;

import static io.kadai.common.internal.util.CheckedSupplier.wrap;
import static io.kadai.testapi.builder.WorkbasketBuilder.newWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.internal.util.Quadruple;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketCustomField;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.api.models.Workbasket;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import io.kadai.workbasket.internal.models.WorkbasketImpl;
import io.kadai.workbasket.internal.models.WorkbasketSummaryImpl;
import java.time.Instant;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

@KadaiIntegrationTest
class WorkbasketBuilderTest {

  @KadaiInject WorkbasketService workbasketService;
  @KadaiInject KadaiEngine kadaiEngine;

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
        kadaiEngine.runAsAdmin(wrap(() -> workbasketService.getWorkbasket(workbasket.getId())));
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
            .customAttribute(WorkbasketCustomField.CUSTOM_5, "custom 5 value")
            .customAttribute(WorkbasketCustomField.CUSTOM_6, "custom 6 value")
            .customAttribute(WorkbasketCustomField.CUSTOM_7, "custom 7 value")
            .customAttribute(WorkbasketCustomField.CUSTOM_8, "custom 8 value")
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
    expectedWorkbasket.setCustomField(WorkbasketCustomField.CUSTOM_1, "custom 1 value");
    expectedWorkbasket.setCustomField(WorkbasketCustomField.CUSTOM_2, "custom 2 value");
    expectedWorkbasket.setCustomField(WorkbasketCustomField.CUSTOM_3, "custom 3 value");
    expectedWorkbasket.setCustomField(WorkbasketCustomField.CUSTOM_4, "custom 4 value");
    expectedWorkbasket.setCustomField(WorkbasketCustomField.CUSTOM_5, "custom 5 value");
    expectedWorkbasket.setCustomField(WorkbasketCustomField.CUSTOM_6, "custom 6 value");
    expectedWorkbasket.setCustomField(WorkbasketCustomField.CUSTOM_7, "custom 7 value");
    expectedWorkbasket.setCustomField(WorkbasketCustomField.CUSTOM_8, "custom 8 value");
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
  @Test
  void should_ReturnWorkbasketImpl_When_BuildingWorkbasket() throws Exception {
    Workbasket workbasket =
        newWorkbasket()
            .key("key6_G")
            .domain("DOMAIN_A")
            .name("Megabasket")
            .type(WorkbasketType.GROUP)
            .buildAndStore(workbasketService);

    assertThat(workbasket.getClass()).isEqualTo(WorkbasketImpl.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ReturnWorkbasketSummaryImpl_When_BuildingWorkbasketAsSummary() throws Exception {
    WorkbasketSummary workbasketSummary =
        newWorkbasket()
            .key("key7_G")
            .domain("DOMAIN_A")
            .name("Megabasket")
            .type(WorkbasketType.GROUP)
            .buildAndStoreAsSummary(workbasketService);

    assertThat(workbasketSummary.getClass()).isEqualTo(WorkbasketSummaryImpl.class);
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
      BiFunction<WorkbasketBuilder, T, WorkbasketBuilder> builderFunction,
      Function<Workbasket, T> retriever)
      throws Exception {
    WorkbasketBuilder builder =
        newWorkbasket()
            .domain("DOMAIN_A")
            .name("workbasketName")
            .type(WorkbasketType.PERSONAL)
            .key("A" + builderFunction.hashCode());

    builderFunction.apply(builder, value);
    Workbasket classification = builder.buildAndStore(workbasketService);
    T retrievedValue = retriever.apply(classification);

    assertThat(retrievedValue).isEqualTo(value);
  }

  private <T> void applyAndOverrideWithApiDefaultValue(
      T value,
      BiFunction<WorkbasketBuilder, T, WorkbasketBuilder> builderFunction,
      Function<Workbasket, T> retriever)
      throws Exception {
    WorkbasketBuilder builder =
        newWorkbasket()
            .domain("DOMAIN_A")
            .name("workbasketName")
            .type(WorkbasketType.PERSONAL)
            .key("B" + builderFunction.hashCode());

    builderFunction.apply(builder, value);
    builderFunction.apply(builder, null);

    Workbasket classification = builder.buildAndStore(workbasketService);
    T retrievedValue = retriever.apply(classification);

    assertThat(retrievedValue).isNotNull().isNotEqualTo(value);
  }
}
