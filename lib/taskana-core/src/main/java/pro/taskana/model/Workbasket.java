package pro.taskana.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Workbasket entity.
 */
public class Workbasket {

    private String id;
    private Timestamp created;
    private Timestamp modified;
    private String name;
    private String description;
    private String owner;
    private List<Workbasket> distributionTargets = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<Workbasket> getDistributionTargets() {
        return distributionTargets;
    }

    public void setDistributionTargets(List<Workbasket> distributionTargets) {
        this.distributionTargets = distributionTargets;
    }
}
