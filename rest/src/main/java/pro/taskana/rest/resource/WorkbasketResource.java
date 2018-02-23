package pro.taskana.rest.resource;

import javax.validation.constraints.NotNull;

import org.springframework.hateoas.ResourceSupport;

import pro.taskana.impl.WorkbasketType;

public class WorkbasketResource extends ResourceSupport {

    public String workbasketId;

    @NotNull
    public String key;

    @NotNull
    public String name;

    @NotNull
    public String domain;

    @NotNull
    public WorkbasketType type;

    public String created;      // ISO-8601
    public String modified;     // ISO-8601
    public String description;
    public String owner;
    public String custom1;
    public String custom2;
    public String custom3;
    public String custom4;
    public String orgLevel1;
    public String orgLevel2;
    public String orgLevel3;
    public String orgLevel4;

    public WorkbasketResource() {
    }

    public WorkbasketResource(String workbasketId, String key, String name, String domain, WorkbasketType type,
        String created,
        String modified, String description, String owner, String custom1, String custom2, String custom3,
        String custom4, String orgLevel1, String orgLevel2, String orgLevel3, String orgLevel4) {
        super();
        this.workbasketId = workbasketId;
        this.key = key;
        this.name = name;
        this.domain = domain;
        this.type = type;
        this.created = created;
        this.modified = modified;
        this.description = description;
        this.owner = owner;
        this.custom1 = custom1;
        this.custom2 = custom2;
        this.custom3 = custom3;
        this.custom4 = custom4;
        this.orgLevel1 = orgLevel1;
        this.orgLevel2 = orgLevel2;
        this.orgLevel3 = orgLevel3;
        this.orgLevel4 = orgLevel4;
    }
}
