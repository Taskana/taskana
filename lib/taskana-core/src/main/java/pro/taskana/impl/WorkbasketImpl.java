package pro.taskana.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketSummary;
import pro.taskana.model.WorkbasketType;

/**
 * Workbasket entity.
 */
public class WorkbasketImpl implements Workbasket {

    private String id;
    private String key;
    private Instant created;
    private Instant modified;
    private String name;
    private String description;
    private String owner;
    private String domain;
    private WorkbasketType type;
    private List<WorkbasketSummary> distributionTargets = new ArrayList<>();
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
    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    @Override
    public Instant getModified() {
        return modified;
    }

    @Override
    public void setModified(Instant modified) {
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
    public List<WorkbasketSummary> getDistributionTargets() {
        return distributionTargets;
    }

    @Override
    public void setDistributionTargets(List<WorkbasketSummary> distributionTargets) {
        this.distributionTargets = distributionTargets;
    }

    @Override
    public String getCustom1() {
        return custom1;
    }

    @Override
    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    @Override
    public String getCustom2() {
        return custom2;
    }

    @Override
    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    @Override
    public String getCustom3() {
        return custom3;
    }

    @Override
    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    @Override
    public String getCustom4() {
        return custom4;
    }

    @Override
    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    @Override
    public String getOrgLevel1() {
        return orgLevel1;
    }

    @Override
    public void setOrgLevel1(String orgLevel1) {
        this.orgLevel1 = orgLevel1;
    }

    @Override
    public String getOrgLevel2() {
        return orgLevel2;
    }

    @Override
    public void setOrgLevel2(String orgLevel2) {
        this.orgLevel2 = orgLevel2;
    }

    @Override
    public String getOrgLevel3() {
        return orgLevel3;
    }

    @Override
    public void setOrgLevel3(String orgLevel3) {
        this.orgLevel3 = orgLevel3;
    }

    @Override
    public String getOrgLevel4() {
        return orgLevel4;
    }

    @Override
    public void setOrgLevel4(String orgLevel4) {
        this.orgLevel4 = orgLevel4;
    }

    @Override
    public WorkbasketSummary asSummary() {
        WorkbasketSummaryImpl result = new WorkbasketSummaryImpl();
        result.setId(this.getId());
        result.setKey(this.getKey());
        result.setName(this.getName());
        result.setDescription(this.getDescription());
        result.setOwner(this.getOwner());
        result.setDomain(this.getDomain());
        result.setType(this.getType());
        result.setOrgLevel1(this.getOrgLevel1());
        result.setOrgLevel2(this.getOrgLevel2());
        result.setOrgLevel3(this.getOrgLevel3());
        result.setOrgLevel4(this.getOrgLevel4());
        return result;
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
