/*-
 * #%L
 * pro.taskana:taskana-core-test
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package acceptance.classification.get;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_1;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_2;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_3;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_4;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_5;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_6;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_7;
import static pro.taskana.classification.api.ClassificationCustomField.CUSTOM_8;
import static pro.taskana.common.api.SharedConstants.MASTER_DOMAIN;

import java.time.Instant;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.ClassificationBuilder;
import pro.taskana.testapi.security.WithAccessId;

@TaskanaIntegrationTest
class GetClassificationAccTest {

  @TaskanaInject ClassificationService classificationService;

  Classification defaultClassification;
  Classification classificationWithAllProperties;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassification =
        DefaultTestEntities.defaultTestClassification().buildAndStore(classificationService);
    classificationWithAllProperties =
        ClassificationBuilder.newClassification()
            .key("Key")
            .domain("DOMAIN_A")
            .name("Name")
            .category("AUTOMATIC")
            .parentId(defaultClassification.getId())
            .type("TASK")
            .priority(2)
            .applicationEntryPoint("EntryPoint")
            .created(Instant.now())
            .serviceLevel("P2D")
            .description("Description")
            .modified(Instant.now())
            .customAttribute(CUSTOM_1, "custom1")
            .customAttribute(CUSTOM_2, "custom2")
            .customAttribute(CUSTOM_3, "custom3")
            .customAttribute(CUSTOM_4, "custom4")
            .customAttribute(CUSTOM_5, "custom5")
            .customAttribute(CUSTOM_6, "custom6")
            .customAttribute(CUSTOM_7, "custom7")
            .customAttribute(CUSTOM_8, "custom8")
            .buildAndStore(classificationService);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_GetClassificationById_When_AllPropertiesSet() throws Exception {
    Classification classification =
        classificationService.getClassification(classificationWithAllProperties.getId());

    assertThat(classification)
        .hasNoNullFieldsOrProperties()
        .isEqualTo(classificationWithAllProperties);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_GetClassificationByKeyAndDomain_When_AllPropertiesSet() throws Exception {
    Classification classification =
        classificationService.getClassification(
            classificationWithAllProperties.getKey(), classificationWithAllProperties.getDomain());

    assertThat(classification)
        .hasNoNullFieldsOrProperties()
        .isEqualTo(classificationWithAllProperties);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_ClassificationIdIsNull() {
    ThrowingCallable test = () -> classificationService.getClassification(null);

    assertThatThrownBy(test).isInstanceOf(ClassificationNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_ClassificationIdDoesNotExist() {
    ThrowingCallable test = () -> classificationService.getClassification("CLI:NonExistingId");

    assertThatThrownBy(test).isInstanceOf(ClassificationNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_ClassificationKeyIsNull() {
    ThrowingCallable test =
        () ->
            classificationService.getClassification(
                null, classificationWithAllProperties.getDomain());

    assertThatThrownBy(test).isInstanceOf(ClassificationNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_ClassificationKeyDoesNotExist() {
    ThrowingCallable test =
        () ->
            classificationService.getClassification(
                "WrongKey", classificationWithAllProperties.getDomain());

    assertThatThrownBy(test).isInstanceOf(ClassificationNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ReturnMasterClassification_When_ClassificationDomainDoesNotExist() throws Exception {
    Classification classification =
        classificationService.getClassification(
            classificationWithAllProperties.getKey(), "WrongDomain");

    assertThat(classification).isNotNull().hasNoNullFieldsOrProperties();
    assertThat(classification.getDomain()).isEqualTo(MASTER_DOMAIN);
    assertThat(classification.getKey()).isEqualTo(classificationWithAllProperties.getKey());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_GetSpecialCharacterCorrectly_When_AFieldContainsASpecialCharacter() throws Exception {
    Classification classification =
        DefaultTestEntities.defaultTestClassification()
            .name("Zustimmungserklärung")
            .buildAndStore(classificationService, "businessadmin");

    Classification result = classificationService.getClassification(classification.getId());

    assertThat(result.getName()).isEqualTo("Zustimmungserklärung");
  }

  @Test
  void should_GetClassification_For_MasterDomain() throws Exception {
    Classification classification =
        classificationService.getClassification(
            classificationWithAllProperties.getKey(), MASTER_DOMAIN);

    assertThat(classification).isNotNull().hasNoNullFieldsOrProperties();
    assertThat(classification.getDomain()).isEqualTo(MASTER_DOMAIN);
    assertThat(classification.getKey()).isEqualTo(classificationWithAllProperties.getKey());
  }
}
