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
    private final Set<String> distroTargets;
    private final List<WorkbasketAccessItem> authorizations;

    public WorkbasketDefinition(Workbasket workbasket, Set<String> distroTargets, List<WorkbasketAccessItem> authorizations) {
        this.workbasket = workbasket;
        this.distroTargets = distroTargets;
        this.authorizations = authorizations;
    }

    public Workbasket getWorkbasket() {
        return workbasket;
    }

    public Set<String> getDistroTargets() {
        return distroTargets;
    }

    public List<WorkbasketAccessItem> getAuthorizations() {
        return authorizations;
    }

    @Override
    public String toString() {
        return "WorkbasketDefinition{"
                + "workbasket=" + workbasket.toString()
                + ", authorizations=" + authorizations.toString()
                + '}';
    }
}
