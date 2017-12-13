package pro.taskana.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import pro.taskana.Workbasket;

/**
 * Workbasket entity.
 */
public class WorkbasketImpl implements Workbasket {

    private String id;
    private Timestamp created;
    private Timestamp modified;
    private String name;
    private String description;
    private String owner;
    private List<Workbasket> distributionTargets = new ArrayList<>();

    WorkbasketImpl() { }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    @Override
    public Timestamp getModified() {
        return modified;
    }

    @Override
    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public List<Workbasket> getDistributionTargets() {
        return distributionTargets;
    }

    @Override
    public void setDistributionTargets(List<Workbasket> distributionTargets) {
        this.distributionTargets = distributionTargets;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Workbasket [id=");
        builder.append(id);
        builder.append(", created=");
        builder.append(created);
        builder.append(", modified=");
        builder.append(modified);
        builder.append(", name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", owner=");
        builder.append(owner);
        builder.append(", distributionTargets=");
        builder.append(distributionTargets);
        builder.append("]");
        return builder.toString();
    }
}


