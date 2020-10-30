package pro.taskana.workbasket.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;
import pro.taskana.workbasket.rest.models.WorkbasketDefinitionRepresentationModel;

/** Test for {@link WorkbasketDefinitionRepresentationModelAssembler}. */
@TaskanaSpringBootTest
@ExtendWith(MockitoExtension.class)
class WorkbasketDefinitionRepresentationModelAssemblerTest {

  private final WorkbasketDefinitionRepresentationModelAssembler assembler;
  @SpyBean private WorkbasketService workbasketService;
  @SpyBean private WorkbasketAccessItemRepresentationModelAssembler accessItemAssembler;
  @SpyBean private WorkbasketRepresentationModelAssembler workbasketAssembler;

  @Autowired
  WorkbasketDefinitionRepresentationModelAssemblerTest(
      WorkbasketDefinitionRepresentationModelAssembler assembler) {
    this.assembler = assembler;
  }

  @Test
  void should_ReturnDefinitionEntity_When_ConvertingWorkbasketToDefinition() throws Exception {
    WorkbasketImpl workbasket = (WorkbasketImpl) workbasketService.newWorkbasket("1", "DOMAIN_A");
    String id = "ID1";
    workbasket.setId(id);
    List<WorkbasketAccessItem> workbasketAccessItems =
        List.of(
            workbasketService.newWorkbasketAccessItem(id, "a"),
            workbasketService.newWorkbasketAccessItem(id, "b"));

    WorkbasketImpl target1 = (WorkbasketImpl) workbasketService.newWorkbasket("2", "DOMAIN_A");
    WorkbasketImpl target2 = (WorkbasketImpl) workbasketService.newWorkbasket("3", "DOMAIN_A");
    target1.setId("target1");
    target2.setId("target2");
    List<WorkbasketSummary> workbasketSummaries = List.of(target1, target2);

    Mockito.doReturn(workbasketAccessItems).when(workbasketService).getWorkbasketAccessItems(id);
    Mockito.doReturn(workbasketSummaries).when(workbasketService).getDistributionTargets(id);

    Object[] mocks = {workbasketService, workbasketAssembler, accessItemAssembler};
    Mockito.clearInvocations(mocks);

    WorkbasketDefinitionRepresentationModel repModel = assembler.toModel(workbasket);

    assertThat(repModel).isNotNull();
    // workbasketAssembler does the conversion. Thus no further testing needed.
    assertThat(repModel.getWorkbasket()).isNotNull();
    // accessItemAssembler does the conversion. Thus no further testing needed.
    assertThat(repModel.getAuthorizations()).hasSize(2);
    assertThat(repModel.getDistributionTargets()).containsExactlyInAnyOrder("target1", "target2");
    InOrder inOrder = Mockito.inOrder(mocks);
    inOrder.verify(workbasketAssembler).toModel(workbasket);
    inOrder.verify(workbasketService).getWorkbasketAccessItems(id);
    inOrder.verify(accessItemAssembler).toCollectionModel(workbasketAccessItems);
    inOrder.verify(accessItemAssembler, times(2)).toModel(any());
    inOrder.verify(workbasketService).getDistributionTargets(id);
    inOrder.verifyNoMoreInteractions();
    Mockito.verifyNoMoreInteractions(mocks);
  }
}
