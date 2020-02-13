package pro.taskana.rest.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import pro.taskana.workbasket.api.models.Workbasket;

/** Resource class for {@link Workbasket} but without links property. */
@JsonIgnoreProperties(value = {"links"})
public class WorkbasketResourceWithoutLinks extends WorkbasketResource {

  WorkbasketResourceWithoutLinks() {}

  WorkbasketResourceWithoutLinks(Workbasket workbasket) {
    super(workbasket);
  }
}
