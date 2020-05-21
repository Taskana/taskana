package pro.taskana.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import pro.taskana.workbasket.api.models.Workbasket;

/** EntityModel class for {@link Workbasket} but without links property. */
@JsonIgnoreProperties(value = {"links"})
public class WorkbasketRepresentationModelWithoutLinks extends WorkbasketRepresentationModel {

  WorkbasketRepresentationModelWithoutLinks() {}

  public WorkbasketRepresentationModelWithoutLinks(Workbasket workbasket) {
    super(workbasket);
  }
}
