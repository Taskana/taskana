package pro.taskana.rest.resource;

import java.util.List;
import java.util.Set;

import org.springframework.hateoas.ResourceSupport;

/**
 * this class represents a workbasket including its distro targets and authorisations.
 */
public class WorkbasketDefinition extends ResourceSupport {

    public Set<String> distributionTargets;
    public List<WorkbasketAccessItemResource> authorizations;
    public WorkbasketResource workbasketResource;

    public WorkbasketDefinition() {
        // necessary for de-serializing
    }

    public WorkbasketDefinition(WorkbasketResource workbasketResource,
        Set<String> distributionTargets,
        List<WorkbasketAccessItemResource> authorizations) {
        super();
        this.workbasketResource = workbasketResource;
        this.distributionTargets = distributionTargets;
        this.authorizations = authorizations;
    }
}
