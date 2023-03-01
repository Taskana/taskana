/*-
 * #%L
 * pro.taskana:taskana-core
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
package acceptance.task.query;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.common.api.BaseQuery.SortDirection.ASCENDING;
import static pro.taskana.common.api.BaseQuery.SortDirection.DESCENDING;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskCustomIntField;
import pro.taskana.task.api.TaskQueryColumnName;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for all "query tasks with sorting" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksWithSortingAccTest extends AbstractAccTest {

  @Nested
  class SortingTest {

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class TaskId {

      @WithAccessId(user = "admin")
      @Test
      void should_sortByTaskIdDesc() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results =
            taskService.createTaskQuery().orderByTaskId(SortDirection.DESCENDING).list();

        // test is only valid with at least 2 results
        assertThat(results).hasSizeGreaterThan(2);

        List<String> idsDesc =
            results.stream()
                .map(TaskSummary::getId)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        for (int i = 0; i < results.size(); i++) {
          assertThat(results.get(i).getId()).isEqualTo(idsDesc.get(i));
        }
      }

      @WithAccessId(user = "admin")
      @Test
      void should_sortByTaskIdAsc_When_SortingDirectionIsNull() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery().orderByTaskId(null).list();

        // test is only valid with at least 2 results
        assertThat(results).hasSizeGreaterThan(2);

        List<String> idsAsc =
            results.stream().map(TaskSummary::getId).sorted().collect(Collectors.toList());

        for (int i = 0; i < results.size(); i++) {
          assertThat(results.get(i).getId()).isEqualTo(idsAsc.get(i));
        }
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class WorkbasketName {
      @WithAccessId(user = "admin")
      @Test
      void should_sortByWorkbasketNameAsc() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results =
            taskService.createTaskQuery().orderByWorkbasketName(SortDirection.ASCENDING).list();

        assertThat(results)
            .hasSizeGreaterThan(2)
            .extracting(TaskSummary::getWorkbasketSummary)
            .extracting(WorkbasketSummary::getName)
            .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
      }

      @WithAccessId(user = "admin")
      @Test
      void should_sortByWorkbasketNameDsc() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results =
            taskService.createTaskQuery().orderByWorkbasketName(SortDirection.DESCENDING).list();

        assertThat(results)
            .hasSizeGreaterThan(2)
            .extracting(TaskSummary::getWorkbasketSummary)
            .extracting(WorkbasketSummary::getName)
            .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Received {
      @WithAccessId(user = "admin")
      @Test
      void should_SortByReceivedAsc_When_FilterIsApplied() {
        TaskService taskService = taskanaEngine.getTaskService();
        // we filter between EPOCH and null,to avoid null as a received value
        List<TaskSummary> results =
            taskService
                .createTaskQuery()
                .receivedWithin(new TimeInterval(Instant.EPOCH, null))
                .orderByReceived(SortDirection.ASCENDING)
                .list();

        assertThat(results)
            .extracting(TaskSummary::getReceived)
            .isSortedAccordingTo(Instant::compareTo);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class DomainNameAndCreated {
      @WithAccessId(user = "admin")
      @Test
      void should_SortByDomainNameAndCreated() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results =
            taskService
                .createTaskQuery()
                .orderByDomain(SortDirection.ASCENDING)
                .orderByName(SortDirection.ASCENDING)
                .orderByCreated(null)
                .list();

        assertThat(results)
            .hasSizeGreaterThan(2)
            .isSortedAccordingTo(
                Comparator.comparing(TaskSummary::getDomain, CASE_INSENSITIVE_ORDER)
                    .thenComparing(TaskSummary::getName, CASE_INSENSITIVE_ORDER)
                    .thenComparing(TaskSummary::getCreated));
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class PorSystemNoteDueAndOwner {
      @WithAccessId(user = "admin")
      @Test
      void should_SortByPorSystemNoteDueAndOwner_When_FilterIsApplied() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results =
            taskService
                .createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("USER-B-2", "DOMAIN_B"))
                .orderByPrimaryObjectReferenceSystem(SortDirection.DESCENDING)
                .orderByNote(null)
                .orderByDue(null)
                .orderByOwner(SortDirection.ASCENDING)
                .list();

        assertThat(results).hasSize(25);
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
          if (previousSummary != null) {
            assertThat(
                    taskSummary
                            .getPrimaryObjRef()
                            .getSystem()
                            .compareToIgnoreCase(previousSummary.getPrimaryObjRef().getSystem())
                        <= 0)
                .isTrue();
          }
          previousSummary = taskSummary;
        }
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class PorSystemInstanceParentBpiPlannedState {

      @WithAccessId(user = "admin")
      @Test
      void should_SortByPorSystemInstanceParentProcPlannedAndState_When_FilterIsApplied() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results =
            taskService
                .createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("USER-B-2", "DOMAIN_B"))
                .orderByPrimaryObjectReferenceSystemInstance(SortDirection.DESCENDING)
                .orderByParentBusinessProcessId(SortDirection.ASCENDING)
                .orderByPlanned(SortDirection.ASCENDING)
                .orderByState(SortDirection.ASCENDING)
                .list();

        assertThat(results).hasSize(25);
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
          if (previousSummary != null) {
            assertThat(
                    taskSummary
                            .getPrimaryObjRef()
                            .getSystemInstance()
                            .compareToIgnoreCase(
                                previousSummary.getPrimaryObjRef().getSystemInstance())
                        <= 0)
                .isTrue();
          }
          previousSummary = taskSummary;
        }
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class PorCompanyClaimed {
      @WithAccessId(user = "admin")
      @Test
      void should_SortByPorCompanyAndClaimed_When_FilterIsApplied() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results =
            taskService
                .createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("USER-B-2", "DOMAIN_B"))
                .orderByPrimaryObjectReferenceCompany(SortDirection.DESCENDING)
                .orderByClaimed(SortDirection.ASCENDING)
                .list();

        assertThat(results).hasSize(25);
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
          // System.out.println("porCompany: " + taskSummary.getPrimaryObjRef().getCompany() + ",
          // claimed: "
          // + taskSummary.getClaimed());
          if (previousSummary != null) {
            assertThat(
                    taskSummary
                            .getPrimaryObjRef()
                            .getCompany()
                            .compareToIgnoreCase(previousSummary.getPrimaryObjRef().getCompany())
                        <= 0)
                .isTrue();
          }
          previousSummary = taskSummary;
        }
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class WbKeyPriorityPorValueAndCompleted {
      @WithAccessId(user = "admin")
      @Test
      void should_SortByPrioPorValueAndCompleted_When_FilterIsApplied() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results =
            taskService
                .createTaskQuery()
                .stateIn(TaskState.READY)
                .workbasketIdIn("WBI:100000000000000000000000000000000015")
                .orderByPriority(SortDirection.DESCENDING)
                .orderByPrimaryObjectReferenceValue(SortDirection.ASCENDING)
                .orderByCompleted(SortDirection.DESCENDING)
                .list();

        assertThat(results).hasSize(22);
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
          if (previousSummary != null) {
            assertThat(
                    taskSummary
                            .getWorkbasketSummary()
                            .getKey()
                            .compareToIgnoreCase(previousSummary.getWorkbasketSummary().getKey())
                        >= 0)
                .isTrue();
          }
          previousSummary = taskSummary;
        }
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class BpiClassificationKeyAndPorType {
      @WithAccessId(user = "admin")
      @Test
      void should_SortBpIdClassificationKeyAndPorType_When_FilterIsApplied() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results =
            taskService
                .createTaskQuery()
                .stateIn(TaskState.READY)
                .workbasketIdIn("WBI:100000000000000000000000000000000015")
                .orderByBusinessProcessId(SortDirection.ASCENDING)
                .orderByClassificationKey(null)
                .orderByPrimaryObjectReferenceType(SortDirection.DESCENDING)
                .list();

        assertThat(results).hasSize(22);
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
          if (previousSummary != null) {
            assertThat(
                    taskSummary
                            .getBusinessProcessId()
                            .compareToIgnoreCase(previousSummary.getBusinessProcessId())
                        >= 0)
                .isTrue();
          }
          previousSummary = taskSummary;
        }
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ModifiedAndDomain {

      @WithAccessId(user = "admin")
      @Test
      void should_SortByModifiedAndDomain_When_FilterIsApplied() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results =
            taskService
                .createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("USER-B-2", "DOMAIN_B"))
                .orderByModified(SortDirection.DESCENDING)
                .orderByDomain(null)
                .list();

        assertThat(results).hasSize(25);
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
          if (previousSummary != null) {
            assertThat(previousSummary.getModified().isBefore(taskSummary.getModified())).isFalse();
          }
          previousSummary = taskSummary;
        }
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Creator {
      @WithAccessId(user = "admin")
      @Test
      void should_ReturnOrderedResult_When_OrderByCreatorDescIsSet() {
        List<TaskSummary> results =
            taskanaEngine.getTaskService().createTaskQuery().orderByCreator(DESCENDING).list();

        assertThat(results)
            .hasSizeGreaterThan(2)
            .extracting(TaskSummary::getCreator)
            .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class OwnerLongName {
      @WithAccessId(user = "admin")
      @Test
      void should_OrderByOwnerLongNameDesc() {
        List<TaskSummary> results =
            taskanaEngine
                .getTaskService()
                .createTaskQuery()
                .orderByOwnerLongName(DESCENDING)
                .list();
        assertThat(results)
            .filteredOn(r -> nonNull(r.getOwnerLongName()))
            .hasSizeGreaterThan(2)
            .extracting(TaskSummary::getOwnerLongName)
            .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
      }

      @WithAccessId(user = "admin")
      @Test
      void should_OrderByOwnerLongNameAsc() {
        List<TaskSummary> results =
            taskanaEngine.getTaskService().createTaskQuery().orderByOwnerLongName(ASCENDING).list();
        assertThat(results)
            .filteredOn(r -> nonNull(r.getOwnerLongName()))
            .hasSizeGreaterThan(2)
            .extracting(TaskSummary::getOwnerLongName)
            .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class WorkbasketId {
      @WithAccessId(user = "admin")
      @Test
      void should_ReturnOrderedResult_When_OrderByWorkbasketIdDescIsSet() {
        List<TaskSummary> results =
            taskanaEngine.getTaskService().createTaskQuery().orderByWorkbasketId(DESCENDING).list();

        assertThat(results)
            .hasSizeGreaterThan(2)
            .extracting(TaskSummary::getWorkbasketSummary)
            .extracting(WorkbasketSummary::getId)
            .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ClassificationName {
      @Disabled
      @WithAccessId(user = "admin")
      @Test
      void should_OrderByClassificationNameAndListClassificationNameColumn() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<String> columnValueList =
            taskService
                .createTaskQuery()
                .orderByClassificationName(ASCENDING)
                .listValues(TaskQueryColumnName.CLASSIFICATION_NAME, null);
        assertThat(columnValueList).hasSize(5);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class AttachmentClassificationName {
      @WithAccessId(user = "user-1-1")
      @Test
      void testSelectByAttachmentClassificationNameLike() {
        TaskService taskService = taskanaEngine.getTaskService();
        // find Task with attachment classification names
        List<TaskSummary> tasks =
            taskService
                .createTaskQuery()
                .attachmentClassificationNameLike("Widerruf", "Beratungsprotokoll", "Dynamik%")
                .orderByAttachmentClassificationName(ASCENDING)
                .list();
        assertThat(tasks).hasSize(10);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class CustomX {
      @WithAccessId(user = "admin")
      @TestFactory
      Stream<DynamicTest> should_ReturnOrderedResult_When_OrderByCustomXAscIsSet() {
        Iterator<TaskCustomField> iterator = Arrays.stream(TaskCustomField.values()).iterator();
        return DynamicTest.stream(
            iterator,
            s -> String.format("order by %s asc", s),
            s ->
                should_ReturnOrderedResult_When_OrderByCustomFieldInSortDirectionIsSet(
                    s, ASCENDING));
      }

      @WithAccessId(user = "admin")
      @TestFactory
      Stream<DynamicTest> should_ReturnOrderedResult_When_OrderByCustomXDescIsSet() {
        Iterator<TaskCustomField> iterator = Arrays.stream(TaskCustomField.values()).iterator();

        return DynamicTest.stream(
            iterator,
            s -> String.format("order by %s desc", s),
            s ->
                should_ReturnOrderedResult_When_OrderByCustomFieldInSortDirectionIsSet(
                    s, DESCENDING));
      }

      void should_ReturnOrderedResult_When_OrderByCustomFieldInSortDirectionIsSet(
          TaskCustomField customField, SortDirection sortDirection) {
        List<TaskSummary> results =
            taskanaEngine
                .getTaskService()
                .createTaskQuery()
                .orderByCustomAttribute(customField, sortDirection)
                .list();

        Comparator<String> comparator =
            sortDirection == ASCENDING ? CASE_INSENSITIVE_ORDER : CASE_INSENSITIVE_ORDER.reversed();

        assertThat(results)
            .hasSizeGreaterThan(2)
            .extracting(t -> t.getCustomField(customField))
            .filteredOn(Objects::nonNull)
            .isSortedAccordingTo(comparator);
      }

      @WithAccessId(user = "admin")
      @TestFactory
      Stream<DynamicTest> should_ReturnOrderedResult_When_OrderByCustomIntXAscIsSet() {
        Iterator<TaskCustomIntField> iterator =
            Arrays.stream(TaskCustomIntField.values()).iterator();

        return DynamicTest.stream(
            iterator,
            s -> String.format("order by %s asc", s),
            s -> {
              List<TaskSummary> results =
                  taskanaEngine
                      .getTaskService()
                      .createTaskQuery()
                      .orderByCustomIntAttribute(s, ASCENDING)
                      .list();

              assertThat(results)
                  .hasSizeGreaterThan(2)
                  .extracting(t -> t.getCustomIntField(s))
                  .filteredOn(Objects::nonNull)
                  .isSorted();
            });
      }

      @WithAccessId(user = "admin")
      @TestFactory
      Stream<DynamicTest> should_ReturnOrderedResult_When_OrderByCustomIntXDescIsSet() {
        Iterator<TaskCustomIntField> iterator =
            Arrays.stream(TaskCustomIntField.values()).iterator();

        return DynamicTest.stream(
            iterator,
            s -> String.format("order by %s desc", s),
            s -> {
              List<TaskSummary> results =
                  taskanaEngine
                      .getTaskService()
                      .createTaskQuery()
                      .orderByCustomIntAttribute(s, DESCENDING)
                      .list();

              assertThat(results)
                  .hasSizeGreaterThan(2)
                  .extracting(t -> t.getCustomIntField(s))
                  .filteredOn(Objects::nonNull)
                  .isSortedAccordingTo(Comparator.reverseOrder());
            });
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class AttachmentClassificationId {
      @WithAccessId(user = "admin")
      @Test
      void testQueryForOrderByAttachmentClassificationIdAsc() {
        List<TaskSummary> results =
            taskanaEngine
                .getTaskService()
                .createTaskQuery()
                .idIn(
                    "TKI:000000000000000000000000000000000009",
                    "TKI:000000000000000000000000000000000010",
                    "TKI:000000000000000000000000000000000011",
                    "TKI:000000000000000000000000000000000012")
                .orderByAttachmentClassificationId(ASCENDING)
                .list();

        assertThat(results)
            .hasSizeGreaterThan(2)
            .flatExtracting(TaskSummary::getAttachmentSummaries)
            .extracting(AttachmentSummary::getClassificationSummary)
            .extracting(ClassificationSummary::getId)
            .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
      }

      @WithAccessId(user = "admin")
      @Test
      void testQueryForOrderByAttachmentClassificationIdDesc() {
        List<TaskSummary> results =
            taskanaEngine
                .getTaskService()
                .createTaskQuery()
                .idIn(
                    "TKI:000000000000000000000000000000000009",
                    "TKI:000000000000000000000000000000000010",
                    "TKI:000000000000000000000000000000000011",
                    "TKI:000000000000000000000000000000000012")
                .orderByAttachmentClassificationId(DESCENDING)
                .list();

        assertThat(results)
            .hasSizeGreaterThan(2)
            .flatExtracting(TaskSummary::getAttachmentSummaries)
            .extracting(AttachmentSummary::getClassificationSummary)
            .extracting(ClassificationSummary::getId)
            .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class AttachmentClassificationKey {
      @WithAccessId(user = "admin")
      @Test
      void testQueryForOrderByAttachmentClassificationKeyAsc() {
        List<TaskSummary> results =
            taskanaEngine
                .getTaskService()
                .createTaskQuery()
                .idIn(
                    "TKI:000000000000000000000000000000000009",
                    "TKI:000000000000000000000000000000000011",
                    "TKI:000000000000000000000000000000000010",
                    "TKI:000000000000000000000000000000000012")
                .orderByAttachmentClassificationKey(ASCENDING)
                .list();

        assertThat(results)
            .hasSizeGreaterThan(2)
            .flatExtracting(TaskSummary::getAttachmentSummaries)
            .extracting(AttachmentSummary::getClassificationSummary)
            .extracting(ClassificationSummary::getKey)
            .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
      }

      @WithAccessId(user = "admin")
      @Test
      void testQueryForOrderByAttachmentClassificationKeyDesc() {
        List<TaskSummary> results =
            taskanaEngine
                .getTaskService()
                .createTaskQuery()
                .idIn(
                    "TKI:000000000000000000000000000000000009",
                    "TKI:000000000000000000000000000000000010",
                    "TKI:000000000000000000000000000000000011",
                    "TKI:000000000000000000000000000000000012")
                .orderByAttachmentClassificationKey(DESCENDING)
                .list();

        assertThat(results)
            .hasSizeGreaterThan(2)
            .flatExtracting(TaskSummary::getAttachmentSummaries)
            .extracting(AttachmentSummary::getClassificationSummary)
            .extracting(ClassificationSummary::getKey)
            .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class AttachmentReference {
      @WithAccessId(user = "admin")
      @Test
      void testQueryForOrderByAttachmentRefValueDesc() {
        List<TaskSummary> results =
            taskanaEngine
                .getTaskService()
                .createTaskQuery()
                .idIn(
                    "TKI:000000000000000000000000000000000010",
                    "TKI:000000000000000000000000000000000011",
                    "TKI:000000000000000000000000000000000012")
                .orderByAttachmentReference(DESCENDING)
                .list();

        assertThat(results)
            .hasSizeGreaterThan(2)
            .flatExtracting(TaskSummary::getAttachmentSummaries)
            .extracting(AttachmentSummary::getObjectReference)
            .extracting(ObjectReference::getValue)
            .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class AttachmentReceived {
      @WithAccessId(user = "admin")
      @Test
      void testQueryForOrderByAttachmentReceivedAsc() {
        List<TaskSummary> results =
            taskanaEngine
                .getTaskService()
                .createTaskQuery()
                .idIn(
                    "TKI:000000000000000000000000000000000008",
                    "TKI:000000000000000000000000000000000052",
                    "TKI:000000000000000000000000000000000054")
                .orderByAttachmentReceived(ASCENDING)
                .list();

        assertThat(results)
            .hasSize(3)
            .flatExtracting(TaskSummary::getAttachmentSummaries)
            .extracting(AttachmentSummary::getReceived)
            .isSorted();
      }

      @WithAccessId(user = "admin")
      @Test
      void testQueryForOrderByAttachmentReceivedDesc() {
        List<TaskSummary> results =
            taskanaEngine
                .getTaskService()
                .createTaskQuery()
                .idIn(
                    "TKI:000000000000000000000000000000000008",
                    "TKI:000000000000000000000000000000000052",
                    "TKI:000000000000000000000000000000000054")
                .orderByAttachmentReceived(DESCENDING)
                .list();

        assertThat(results)
            .hasSize(3)
            .flatExtracting(TaskSummary::getAttachmentSummaries)
            .extracting(AttachmentSummary::getReceived)
            .isSortedAccordingTo(Comparator.<Instant>naturalOrder().reversed());
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class AttachmentChannel {
      @WithAccessId(user = "admin")
      @Test
      void testQueryForOrderByAttachmentChannelAscAndReferenceDesc() {
        List<TaskSummary> results =
            taskanaEngine
                .getTaskService()
                .createTaskQuery()
                .idIn(
                    "TKI:000000000000000000000000000000000009",
                    "TKI:000000000000000000000000000000000010",
                    "TKI:000000000000000000000000000000000011",
                    "TKI:000000000000000000000000000000000012")
                .orderByAttachmentChannel(ASCENDING)
                .orderByAttachmentReference(DESCENDING)
                .list();

        assertThat(results)
            .hasSizeGreaterThan(2)
            .flatExtracting(TaskSummary::getAttachmentSummaries)
            .isSortedAccordingTo(
                Comparator.comparing(AttachmentSummary::getChannel, CASE_INSENSITIVE_ORDER)
                    .thenComparing(
                        a -> a.getObjectReference().getValue(), CASE_INSENSITIVE_ORDER.reversed()));
      }

      @WithAccessId(user = "admin")
      @Test
      void testQueryForAttachmentChannelLikeAndOrdering() {
        List<TaskSummary> results =
            taskanaEngine
                .getTaskService()
                .createTaskQuery()
                .attachmentChannelLike("CH%")
                .orderByClassificationKey(DESCENDING)
                .list();

        assertThat(results)
            .extracting(TaskSummary::getClassificationSummary)
            .extracting(ClassificationSummary::getKey)
            .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());

        results =
            taskanaEngine
                .getTaskService()
                .createTaskQuery()
                .attachmentChannelLike("CH%")
                .orderByClassificationKey(ASCENDING)
                .list();

        assertThat(results)
            .extracting(TaskSummary::getClassificationSummary)
            .extracting(ClassificationSummary::getKey)
            .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
      }
    }
  }
}
