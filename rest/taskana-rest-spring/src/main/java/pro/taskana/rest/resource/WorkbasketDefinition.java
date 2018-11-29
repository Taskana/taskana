package pro.taskana.rest.resource;

import java.util.List;
import java.util.Set;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;

/**
 * this class represents a workbasket including its distro targets and authorisations.
 */
public class WorkbasketDefinition {

    public Set<String> distributionTargets;
    public List<WorkbasketAccessItem> authorizations;
    public Workbasket workbasket;

    public WorkbasketDefinition() {
        // necessary for de-serializing
    }

    public WorkbasketDefinition(Workbasket workbasket,
        Set<String> distributionTargets,
        List<WorkbasketAccessItem> authorizations) {
        super();
        this.workbasket = workbasket;
        this.distributionTargets = distributionTargets;
        this.authorizations = authorizations;
    }
}
