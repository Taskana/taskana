package pro.taskana.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import pro.taskana.Workbasket;
import pro.taskana.model.WorkbasketType;

/**
 * Workbasket entity.
 */
public class WorkbasketImpl implements Workbasket {

    private String id;
    private String key;
    private Timestamp created;
    private Timestamp modified;
    private String name;
    private String description;
    private String owner;
    private String domain;
    private WorkbasketType type;
    private List<Workbasket> distributionTargets = new ArrayList<>();
    private String custom1;
    private String custom2;
    private String custom3;
    private String custom4;
    private String orgLevel1;
    private String orgLevel2;
    private String orgLevel3;
    private String orgLevel4;

    WorkbasketImpl() {
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
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
    public String getDomain() {
        return domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public WorkbasketType getType() {
        return type;
    }

    @Override
    public void setType(WorkbasketType type) {
        this.type = type;
    }

    @Override
    public List<Workbasket> getDistributionTargets() {
        return distributionTargets;
    }

    @Override
    public void setDistributionTargets(List<Workbasket> distributionTargets) {
        this.distributionTargets = distributionTargets;
    }

    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    public String getCustom2() {
        return custom2;
    }

    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    public String getCustom3() {
        return custom3;
    }

    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    public String getCustom4() {
        return custom4;
    }

    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    public String getOrgLevel1() {
        return orgLevel1;
    }

    public void setOrgLevel1(String orgLevel1) {
        this.orgLevel1 = orgLevel1;
    }

    public String getOrgLevel2() {
        return orgLevel2;
    }

    public void setOrgLevel2(String orgLevel2) {
        this.orgLevel2 = orgLevel2;
    }

    public String getOrgLevel3() {
        return orgLevel3;
    }

    public void setOrgLevel3(String orgLevel3) {
        this.orgLevel3 = orgLevel3;
    }

    public String getOrgLevel4() {
        return orgLevel4;
    }

    public void setOrgLevel4(String orgLevel4) {
        this.orgLevel4 = orgLevel4;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Workbasket [id=");
        builder.append(id);
        builder.append(", key=");
        builder.append(key);
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
        builder.append(", domain=");
        builder.append(domain);
        builder.append(", type=");
        builder.append(type);
        builder.append(", distributionTargets=");
        builder.append(distributionTargets);
        builder.append(", custom1=");
        builder.append(custom1);
        builder.append(", custom2=");
        builder.append(custom2);
        builder.append(", custom3=");
        builder.append(custom3);
        builder.append(", custom4=");
        builder.append(custom4);
        builder.append(", orgLevel1=");
        builder.append(orgLevel1);
        builder.append(", orgLevel2=");
        builder.append(orgLevel2);
        builder.append(", orgLevel3=");
        builder.append(orgLevel3);
        builder.append(", orgLevel4=");
        builder.append(orgLevel4);
        builder.append("]");
        return builder.toString();
    }
}
