package io.kadai.monitor.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.monitor.api.reports.header.PriorityColumnHeader;
import io.kadai.monitor.rest.models.PriorityColumnHeaderRepresentationModel;
import io.kadai.rest.test.KadaiSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@KadaiSpringBootTest
class PriorityColumnHeaderRepresentationModelAssemblerTest {

  private final PriorityColumnHeaderRepresentationModelAssembler assembler;

  @Autowired
  public PriorityColumnHeaderRepresentationModelAssemblerTest(
      PriorityColumnHeaderRepresentationModelAssembler assembler) {
    this.assembler = assembler;
  }

  @Test
  void should_convertEntityToRepresentationModel() {
    PriorityColumnHeader columnHeader = new PriorityColumnHeader(10, 20);
    PriorityColumnHeaderRepresentationModel expectedRepModel =
        new PriorityColumnHeaderRepresentationModel(10, 20);

    PriorityColumnHeaderRepresentationModel repModel = assembler.toModel(columnHeader);

    assertThat(repModel).usingRecursiveComparison().isEqualTo(expectedRepModel);
  }

  @Test
  void should_convertRepresentationModelToEntity() {
    PriorityColumnHeaderRepresentationModel repModel =
        new PriorityColumnHeaderRepresentationModel(10, 20);
    PriorityColumnHeader expectedColumnHeader = new PriorityColumnHeader(10, 20);

    PriorityColumnHeader columnHeader = assembler.toEntityModel(repModel);

    assertThat(columnHeader).usingRecursiveComparison().isEqualTo(expectedColumnHeader);
  }
}
