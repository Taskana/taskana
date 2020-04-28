package pro.taskana.rest.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import pro.taskana.workbasket.api.models.Workbasket;

/** EntityModel class for {@link Workbasket} but without links property. */
@JsonIgnoreProperties(value = {"links"})
public class WorkbasketRepresentationModelWithoutLinks extends WorkbasketRepresentationModel {

  WorkbasketRepresentationModelWithoutLinks() {}

  WorkbasketRepresentationModelWithoutLinks(Workbasket workbasket) {
    super(workbasket);
  }
}
