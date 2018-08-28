package pro.taskana.rest.resource;

import org.springframework.hateoas.core.Relation;

import javax.validation.constraints.NotNull;

/**
 * Resource class for {@link pro.taskana.WorkbasketAccessItem}.
 */
@Relation(collectionRelation = "accessItems")
public class WorkbasketAccesItemExtendedResource extends WorkbasketAccessItemResource {

    @NotNull
    public String workbasketKey;

    public String getWorkbasketKey() {
        return workbasketKey;
    }

    public void setWorkbasketKey(String workbasketKey) {
        this.workbasketKey = workbasketKey;
    }
}
