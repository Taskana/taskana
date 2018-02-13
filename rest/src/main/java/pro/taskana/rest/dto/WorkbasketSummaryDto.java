package pro.taskana.rest.dto;

import org.springframework.hateoas.ResourceSupport;
import pro.taskana.model.WorkbasketType;

public class WorkbasketSummaryDto extends ResourceSupport{

    private String workBasketId;
    private String key;
    private String name;
    private String description;
    private String owner;
    private String domain;
    private WorkbasketType type;
    private String orgLevel1;
    private String orgLevel2;
    private String orgLevel3;
    private String orgLevel4;

    public String getWorkBasketId() {
        return workBasketId;
    }

    public void setWorkBasketId(String workBasketId) {
        this.workBasketId = workBasketId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public WorkbasketType getType() {
        return type;
    }

    public void setType(WorkbasketType type) {
        this.type = type;
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
}
