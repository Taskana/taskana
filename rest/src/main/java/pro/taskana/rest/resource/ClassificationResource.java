package pro.taskana.rest.resource;

import java.time.Instant;

import org.springframework.hateoas.ResourceSupport;

public class ClassificationResource extends ResourceSupport {
    public String classificationId;
    public String key;
    public String parentId;
    public String category;
    public String type;
    public String domain;
    public boolean isValidInDomain;
    public Instant created;
    public String name;
    public String description;
    public int priority;
    public String serviceLevel; // PddDThhHmmM
    public String applicationEntryPoint;
    public String custom1;
    public String custom2;
    public String custom3;
    public String custom4;
    public String custom5;
    public String custom6;
    public String custom7;
    public String custom8;

    public ClassificationResource(String classificationId, String key, String parentId, String category, String type, String domain,
                                  boolean isValidInDomain, Instant created, String name, String description,
                                  int priority, String serviceLevel, String applicationEntryPoint, String custom1,
                                  String custom2, String custom3, String custom4, String custom5, String custom6,
                                  String custom7, String custom8) {
        super();
        this.classificationId = classificationId;
        this.key = key;
        this.parentId = parentId;
        this.category = category;
        this.type = type;
        this.domain = domain;
        this.isValidInDomain = isValidInDomain;
        this.created = created;
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.serviceLevel = serviceLevel;
        this.applicationEntryPoint = applicationEntryPoint;
        this.custom1 = custom1;
        this.custom2 = custom2;
        this.custom3 = custom3;
        this.custom4 = custom4;
        this.custom5 = custom5;
        this.custom6 = custom6;
        this.custom7 = custom7;
        this.custom8 = custom8;
    }
}
