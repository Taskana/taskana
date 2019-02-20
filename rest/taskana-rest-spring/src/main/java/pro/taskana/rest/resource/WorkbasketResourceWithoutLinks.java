package pro.taskana.rest.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Resource class for {@link pro.taskana.Workbasket} but without links property.
 */
@JsonIgnoreProperties(value = { "links" })
public class WorkbasketResourceWithoutLinks extends WorkbasketResource {

}
