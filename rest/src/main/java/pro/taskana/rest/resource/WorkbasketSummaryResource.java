package pro.taskana.rest.resource;

import javax.validation.constraints.NotNull;

import org.springframework.hateoas.ResourceSupport;

import pro.taskana.impl.WorkbasketType;

public class WorkbasketSummaryResource extends ResourceSupport {

    public String workbasketId;

    @NotNull
    public String key;

    @NotNull
    public String name;

    @NotNull
    public String domain;

    @NotNull
    public WorkbasketType type;

    public String description;
    public String owner;
    public String orgLevel1;
    public String orgLevel2;
    public String orgLevel3;
    public String orgLevel4;

    public WorkbasketSummaryResource() {
    }

    public WorkbasketSummaryResource(String workbasketId, String key, String name, String description, String owner,
        String domain, WorkbasketType type, String orgLevel1, String orgLevel2, String orgLevel3, String orgLevel4) {
        super();
        this.workbasketId = workbasketId;
        this.key = key;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.domain = domain;
        this.type = type;
        this.orgLevel1 = orgLevel1;
        this.orgLevel2 = orgLevel2;
        this.orgLevel3 = orgLevel3;
        this.orgLevel4 = orgLevel4;
    }
}
