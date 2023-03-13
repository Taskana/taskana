package acceptance.classification.query;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_1;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_2;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_3;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_4;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_5;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_6;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_7;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_8;
import static pro.taskana.classification.api.ClassificationQueryColumnName.CREATED;
import static pro.taskana.classification.api.ClassificationQueryColumnName.NAME;
import static pro.taskana.classification.api.ClassificationQueryColumnName.TYPE;
import static pro.taskana.classification.api.ClassificationQueryColumnName.VALID_IN_DOMAIN;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.function.ThrowingConsumer;
import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.security.WithAccessId;

/**
 * The QueryClassificationsFilteringAccTest for all "query classification with filtering" scenarios.
 *
 * <p>Note: Instead of working with a clean database for each test, we create a scenario. In that
 * scenario we use {@linkplain ClassificationCustomField#CUSTOM_1} to filter out all the other
 * created {@linkplain Classification Classifications}. Therefore, all tests regarding {@linkplain
 * ClassificationCustomField ClassificationCustomFields} are executed first.
 *
 * <p>The alternative is to create a "big" scenario as a test setup. But that would result to the
 * same issues we have with the sql file.
 */
@TaskanaIntegrationTest
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class ClassificationQueryFilteringAccTest {
  @TaskanaInject ClassificationService classificationService;
  ClassificationSummary classificationSummaryBeforeAll;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void createClassificationsWithDifferentRoles() throws Exception {
    classificationSummaryBeforeAll =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
  }

  @WithAccessId(user = "businessadmin")
  @TestFactory
  @Order(1)
  Stream<DynamicTest> should_FindClassifications_When_QueryingForCustomAttributeIn() {
    String testIdentifier = UUID.randomUUID().toString();

    Iterator<ClassificationCustomField> customFieldIterator =
        Arrays.stream(ClassificationCustomField.values()).iterator();
    ThrowingConsumer<ClassificationCustomField> test =
        customField -> {
          ClassificationSummary classificationSummary =
              defaultTestClassification()
                  .customAttribute(customField, testIdentifier)
                  .buildAndStoreAsSummary(classificationService);

          List<ClassificationSummary> classificationSummaryList =
              classificationService
                  .createClassificationQuery()
                  .customAttributeIn(customField, testIdentifier)
                  .list();

          ClassificationSummary masterClassificationSummary =
              classificationService
                  .getClassification(classificationSummary.getKey(), "")
                  .asSummary();
          assertThat(classificationSummaryList)
              .containsExactlyInAnyOrder(classificationSummary, masterClassificationSummary);
        };
    return DynamicTest.stream(
        customFieldIterator, c -> String.format("for %s", c.toString()), test);
  }

  @WithAccessId(user = "businessadmin")
  @TestFactory
  Stream<DynamicTest> should_FindClassifications_When_QueryingForCustomAttributeLike() {
    String testIdentifier = UUID.randomUUID().toString();

    // Value for Custom-Field has to be unique. Ensure uniqueness with testIdentifier
    Iterator<Pair<ClassificationCustomField, String>> identifierToCustomFieldsIterator =
        List.of(
                Pair.of(CUSTOM_1, testIdentifier + "_1CUSTOM1"),
                Pair.of(CUSTOM_2, testIdentifier + "_2CUSTOM2"),
                Pair.of(CUSTOM_3, testIdentifier + "_3CUSTOM3"),
                Pair.of(CUSTOM_4, testIdentifier + "_4CUSTOM4"),
                Pair.of(CUSTOM_5, testIdentifier + "_5CUSTOM5"),
                Pair.of(CUSTOM_6, testIdentifier + "_6CUSTOM6"),
                Pair.of(CUSTOM_7, testIdentifier + "_7CUSTOM7"),
                Pair.of(CUSTOM_8, testIdentifier + "_8CUSTOM8"))
            .iterator();

    ThrowingConsumer<Pair<ClassificationCustomField, String>> test =
        pair -> {
          ClassificationSummary classificationSummary =
              defaultTestClassification()
                  .customAttribute(pair.getLeft(), pair.getRight())
                  .buildAndStoreAsSummary(classificationService);

          List<ClassificationSummary> classificationSummaryList =
              classificationService
                  .createClassificationQuery()
                  .customAttributeLike(pair.getLeft(), testIdentifier + "%")
                  .domainIn("DOMAIN_A")
                  .list();

          assertThat(classificationSummaryList).containsExactlyInAnyOrder(classificationSummary);
        };

    return DynamicTest.stream(
        identifierToCustomFieldsIterator,
        p -> String.format("for %s", p.getLeft().toString()),
        test);
  }

  @Test
  void should_FindAllAccessibleClassifications_When_QueryingNotAuthenticated() throws Exception {
    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .idIn(classificationSummaryBeforeAll.getId())
            .list();

    assertThat(classificationSummaryList).containsExactly(classificationSummaryBeforeAll);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "businessadmin")
  @TestTemplate
  void should_FindAllAccessibleClassifications_When_UserInRoleAdminOrBusinessadmin() {
    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .idIn(classificationSummaryBeforeAll.getId())
            .list();

    assertThat(classificationSummaryList).containsExactly(classificationSummaryBeforeAll);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassifications_When_QueryingForCategoryInAndDomainIn() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    defaultTestClassification()
        .customAttribute(CUSTOM_1, testIdentifier)
        .category("EXTERNAL")
        .type("TASK")
        .buildAndStoreAsSummary(classificationService);
    ClassificationSummary classificationSummary =
        defaultTestClassification()
            .customAttribute(CUSTOM_1, testIdentifier)
            .category("MANUAL")
            .type("TASK")
            .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .domainIn("DOMAIN_A")
            .categoryIn("MANUAL")
            .list();

    assertThat(classificationSummaryList).containsExactly(classificationSummary);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindAClassification_When_QueryingForMultipleDomainsInAndKeyIn() throws Exception {
    defaultTestClassification().domain("DOMAIN_B").buildAndStoreAsSummary(classificationService);
    ClassificationSummary classificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .keyIn(classificationSummary.getKey())
            .domainIn("DOMAIN_A", "DOMAIN_B", "")
            .list();

    ClassificationSummary masterClassification =
        classificationService.getClassification(classificationSummary.getKey(), "").asSummary();
    assertThat(classificationSummaryList)
        .containsExactlyInAnyOrder(classificationSummary, masterClassification);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassifications_When_QueryingForTypeInAndParentIn() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    ClassificationSummary classificationSummary =
        defaultTestClassification()
            .customAttribute(CUSTOM_1, testIdentifier)
            .type("TASK")
            .buildAndStoreAsSummary(classificationService);
    ClassificationSummary parentClassificationSummary =
        defaultTestClassification()
            .customAttribute(CUSTOM_1, testIdentifier)
            .type("DOCUMENT")
            .buildAndStoreAsSummary(classificationService);
    defaultTestClassification()
        .customAttribute(CUSTOM_1, testIdentifier)
        .type("DOCUMENT")
        .parentId(parentClassificationSummary.getId())
        .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .typeIn("TASK", "DOCUMENT")
            .parentIdIn("")
            .list();

    ClassificationSummary masterClassification =
        classificationService.getClassification(classificationSummary.getKey(), "").asSummary();
    ClassificationSummary masterParentClassification =
        classificationService
            .getClassification(parentClassificationSummary.getKey(), "")
            .asSummary();

    assertThat(classificationSummaryList)
        .containsExactlyInAnyOrder(
            classificationSummary,
            parentClassificationSummary,
            masterClassification,
            masterParentClassification);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassifications_When_QueryingForKeyInAndCategoryIn() throws Exception {
    ClassificationSummary externalClassificationSummary =
        defaultTestClassification()
            .type("TASK")
            .category("EXTERNAL")
            .buildAndStoreAsSummary(classificationService);
    ClassificationSummary manualClassificationSummary =
        defaultTestClassification()
            .type("TASK")
            .category("MANUAL")
            .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .keyIn(externalClassificationSummary.getKey(), manualClassificationSummary.getKey())
            .categoryIn("EXTERNAL", "MANUAL")
            .list();

    ClassificationSummary masterClassificationExternal =
        classificationService
            .getClassification(externalClassificationSummary.getKey(), "")
            .asSummary();
    ClassificationSummary masterClassificationManual =
        classificationService
            .getClassification(manualClassificationSummary.getKey(), "")
            .asSummary();
    assertThat(classificationSummaryList)
        .containsExactlyInAnyOrder(
            externalClassificationSummary,
            manualClassificationSummary,
            masterClassificationExternal,
            masterClassificationManual);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassifications_When_QueryingForParentIdIn() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    ClassificationSummary parentClassificationSummary =
        defaultTestClassification()
            .customAttribute(CUSTOM_1, testIdentifier)
            .buildAndStoreAsSummary(classificationService);
    ClassificationSummary childClassificationSummary =
        defaultTestClassification()
            .customAttribute(CUSTOM_1, testIdentifier)
            .parentId(parentClassificationSummary.getId())
            .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .parentIdIn(parentClassificationSummary.getId())
            .list();

    ClassificationSummary masterChildClassification =
        classificationService
            .getClassification(childClassificationSummary.getKey(), "")
            .asSummary();
    assertThat(classificationSummaryList)
        .containsExactlyInAnyOrder(childClassificationSummary, masterChildClassification);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassifications_When_QueryingForParentKeyIn() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    ClassificationSummary parentClassificationSummary =
        defaultTestClassification()
            .customAttribute(CUSTOM_1, testIdentifier)
            .buildAndStoreAsSummary(classificationService);
    ClassificationSummary childClassificationSummary =
        defaultTestClassification()
            .customAttribute(CUSTOM_1, testIdentifier)
            .parentKey(parentClassificationSummary.getKey())
            .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .parentKeyIn(parentClassificationSummary.getKey())
            .list();

    ClassificationSummary masterChildClassification =
        classificationService
            .getClassification(childClassificationSummary.getKey(), "")
            .asSummary();
    assertThat(classificationSummaryList)
        .containsExactlyInAnyOrder(childClassificationSummary, masterChildClassification);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassification_When_QueryingForParentIdInAndCustom2In() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    ClassificationSummary parentClassificationSummary =
        defaultTestClassification()
            .customAttribute(CUSTOM_2, testIdentifier)
            .buildAndStoreAsSummary(classificationService);
    ClassificationSummary childClassificationSummary =
        defaultTestClassification()
            .customAttribute(CUSTOM_2, testIdentifier)
            .parentId(parentClassificationSummary.getId())
            .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_2, testIdentifier)
            .parentIdIn(parentClassificationSummary.getId())
            .list();

    ClassificationSummary masterChildClassification =
        classificationService
            .getClassification(childClassificationSummary.getKey(), "")
            .asSummary();
    assertThat(classificationSummaryList)
        .containsExactlyInAnyOrder(childClassificationSummary, masterChildClassification);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassification_When_QueryingForPriorityInAndValidInDomain() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    ClassificationSummary classificationSummary =
        defaultTestClassification()
            .priority(999)
            .isValidInDomain(true)
            .customAttribute(CUSTOM_1, testIdentifier)
            .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .validInDomainEquals(true)
            .priorityIn(999)
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .list();

    assertThat(classificationSummaryList).containsExactlyInAnyOrder(classificationSummary);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassification_When_QueryingForNameLike() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    ClassificationSummary classificationSummary =
        defaultTestClassification()
            .name(testIdentifier + "Name")
            .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService.createClassificationQuery().nameLike(testIdentifier + "%").list();

    ClassificationSummary masterClassificationSummary =
        classificationService.getClassification(classificationSummary.getKey(), "").asSummary();
    assertThat(classificationSummaryList)
        .containsExactlyInAnyOrder(classificationSummary, masterClassificationSummary);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassification_When_QueryingForNameIn() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    ClassificationSummary classificationSummary =
        defaultTestClassification()
            .name(testIdentifier)
            .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService.createClassificationQuery().nameIn(testIdentifier).list();

    ClassificationSummary masterClassificationSummary =
        classificationService.getClassification(classificationSummary.getKey(), "").asSummary();
    assertThat(classificationSummaryList)
        .containsExactlyInAnyOrder(classificationSummary, masterClassificationSummary);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassification_When_QueryingForDescriptionLike() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    ClassificationSummary classificationSummary =
        defaultTestClassification()
            .description(testIdentifier + "Description")
            .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .descriptionLike(testIdentifier + "%")
            .list();

    ClassificationSummary masterClassificationSummary =
        classificationService.getClassification(classificationSummary.getKey(), "").asSummary();
    assertThat(classificationSummaryList)
        .containsExactlyInAnyOrder(classificationSummary, masterClassificationSummary);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassification_When_QueryingForServiceLevelIn() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    ClassificationSummary classificationSummary =
        defaultTestClassification()
            .customAttribute(CUSTOM_1, testIdentifier)
            .serviceLevel("P99D")
            .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .serviceLevelIn("P99D")
            .list();

    ClassificationSummary masterClassificationSummary =
        classificationService.getClassification(classificationSummary.getKey(), "").asSummary();
    assertThat(classificationSummaryList)
        .containsExactlyInAnyOrder(classificationSummary, masterClassificationSummary);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassification_When_QueryingForServiceLevelLike() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    ClassificationSummary classificationSummary =
        defaultTestClassification()
            .customAttribute(CUSTOM_1, testIdentifier)
            .serviceLevel("P99D")
            .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .serviceLevelLike("P99%")
            .list();

    ClassificationSummary masterClassificationSummary =
        classificationService.getClassification(classificationSummary.getKey(), "").asSummary();
    assertThat(classificationSummaryList)
        .containsExactlyInAnyOrder(classificationSummary, masterClassificationSummary);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassification_When_QueryingForApplicationEntryPointIn() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    ClassificationSummary classificationSummary =
        defaultTestClassification()
            .applicationEntryPoint(testIdentifier)
            .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .applicationEntryPointIn(testIdentifier)
            .list();

    ClassificationSummary masterClassificationSummary =
        classificationService.getClassification(classificationSummary.getKey(), "").asSummary();
    assertThat(classificationSummaryList)
        .containsExactlyInAnyOrder(classificationSummary, masterClassificationSummary);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassification_When_QueryingForApplicationEntryPointLike() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    ClassificationSummary classificationSummary =
        defaultTestClassification()
            .applicationEntryPoint(testIdentifier + "ApplicationEntryPoint")
            .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .applicationEntryPointLike(testIdentifier + "%")
            .list();

    ClassificationSummary masterClassificationSummary =
        classificationService.getClassification(classificationSummary.getKey(), "").asSummary();
    assertThat(classificationSummaryList)
        .containsExactlyInAnyOrder(classificationSummary, masterClassificationSummary);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassifications_When_QueryingForCreatedTimestampWithin() throws Exception {
    final Instant now = Instant.now();
    Thread.sleep(10);
    String testIdentifier = UUID.randomUUID().toString();
    ClassificationSummary classificationSummary =
        defaultTestClassification()
            .customAttribute(CUSTOM_1, testIdentifier)
            .buildAndStoreAsSummary(classificationService);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .createdWithin(new TimeInterval(now, null))
            .list();

    ClassificationSummary masterClassificationSummary =
        classificationService.getClassification(classificationSummary.getKey(), "").asSummary();
    assertThat(classificationSummaryList)
        .containsExactlyInAnyOrder(classificationSummary, masterClassificationSummary);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassification_When_QueryingForModifiedTimestampWithin() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    Classification classification =
        defaultTestClassification()
            .customAttribute(CUSTOM_1, testIdentifier)
            .buildAndStore(classificationService);
    final Instant now = Instant.now();
    Thread.sleep(10);
    classificationService.updateClassification(classification);

    List<ClassificationSummary> classificationSummaryList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .modifiedWithin(new TimeInterval(now, null))
            .list();

    assertThat(classificationSummaryList).containsExactlyInAnyOrder(classification.asSummary());
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassifications_When_QueryingUsingListNameValues() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    defaultTestClassification()
        .customAttribute(CUSTOM_1, testIdentifier)
        .name("Classification Name")
        .buildAndStoreAsSummary(classificationService);
    defaultTestClassification().buildAndStoreAsSummary(classificationService);

    List<String> columnNameList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .listValues(NAME, null);
    List<String> columnNameMasterList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .domainIn("")
            .listValues(NAME, null);
    List<String> columnNameAllList =
        Stream.concat(columnNameList.stream(), columnNameMasterList.stream())
            .collect(Collectors.toList());

    assertThat(columnNameAllList).containsOnly("Classification Name");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassifications_When_QueryingUsingListTypeValues() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    defaultTestClassification()
        .customAttribute(CUSTOM_1, testIdentifier)
        .type("DOCUMENT")
        .buildAndStoreAsSummary(classificationService);
    defaultTestClassification().buildAndStoreAsSummary(classificationService);

    List<String> columnTypeList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .listValues(TYPE, null);
    List<String> columnTypeMasterList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .domainIn("")
            .listValues(TYPE, null);
    List<String> columnTypeAllList =
        Stream.concat(columnTypeList.stream(), columnTypeMasterList.stream())
            .collect(Collectors.toList());

    assertThat(columnTypeAllList).containsOnly("DOCUMENT");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassifications_When_QueryingUsingListIsValidInDomainValues() throws Exception {
    String testIdentifier = UUID.randomUUID().toString();
    defaultTestClassification()
        .customAttribute(CUSTOM_1, testIdentifier)
        .type("TASK")
        .isValidInDomain(false)
        .buildAndStoreAsSummary(classificationService);
    defaultTestClassification().buildAndStoreAsSummary(classificationService);

    List<String> columnIsValidInDomainList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .listValues(VALID_IN_DOMAIN, null);
    List<String> columnIsValidInDomainMasterList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .domainIn("")
            .listValues(TYPE, null);
    List<String> columnIsValidInDomainAllList =
        Stream.concat(columnIsValidInDomainList.stream(), columnIsValidInDomainMasterList.stream())
            .collect(Collectors.toList());

    // Expecting "0" for H2 (==> false in Oracle DBs) and "f" for POSTGRES (==> abbreviation of
    // false)
    assertThat(columnIsValidInDomainAllList).containsAnyOf("f", "0");
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_FindClassifications_When_QueryingUsingListCreatedValues() throws Exception {
    final DateTimeFormatterBuilder dateTimeFormatterBuilder =
        new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
    String testIdentifier = UUID.randomUUID().toString();
    final Instant now = Instant.now();
    Thread.sleep(10);
    defaultTestClassification()
        .customAttribute(CUSTOM_1, testIdentifier)
        .type("TASK")
        .isValidInDomain(false)
        .buildAndStoreAsSummary(classificationService);
    defaultTestClassification().buildAndStoreAsSummary(classificationService);

    List<String> columnCreatedList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .listValues(CREATED, null);
    List<String> columnCreatedMasterList =
        classificationService
            .createClassificationQuery()
            .customAttributeIn(CUSTOM_1, testIdentifier)
            .listValues(CREATED, null);
    List<String> columnCreatedAllList =
        Stream.concat(columnCreatedList.stream(), columnCreatedMasterList.stream())
            .collect(Collectors.toList());

    assertThat(columnCreatedAllList)
        .isNotEmpty()
        .allSatisfy(
            c ->
                assertThat(now)
                    .isBefore(
                        LocalDateTime.parse(c, dateTimeFormatterBuilder.toFormatter())
                            .toInstant(ZoneOffset.UTC)));
  }
}
