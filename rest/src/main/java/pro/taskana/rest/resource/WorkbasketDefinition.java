package pro.taskana.rest.resource;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketAccessItem;

import java.util.List;
import java.util.Set;

/**
 * this class represents a workbasket including its distro targets and authorisations.
 */
public class WorkbasketDefinition {

    private final Workbasket workbasket;
    private final Set<String> distributionTargets;
    private final List<WorkbasketAccessItem> authorizations;

    public WorkbasketDefinition(Workbasket workbasket, Set<String> distributionTargets,
        List<WorkbasketAccessItem> authorizations) {
        this.workbasket = workbasket;
        this.distributionTargets = distributionTargets;
        this.authorizations = authorizations;
    }

    public Workbasket getWorkbasket() {
        return workbasket;
    }

    public Set<String> getDistributionTargets() {
        return distributionTargets;
    }

    public List<WorkbasketAccessItem> getAuthorizations() {
        return authorizations;
    }

    @Override public String toString() {
        return "WorkbasketDefinition{" +
            "workbasket=" + workbasket +
            ", distributionTargets=" + distributionTargets +
            ", authorizations=" + authorizations +
            '}';
    }
}
