package org.taskana.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Classification entity.
 */
public class Classification {

    private String id;
    private String tenantId;
    private String parentClassificationId;
    private String category;
    private String type;
    private Date created;
    private Date modified;
    private String name;
    private String description;
    private int priority;
    private String serviceLevel; // PddDThhHmmM
    private List<Classification> children = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getParentClassificationId() {
        return parentClassificationId;
    }

    public void setParentClassificationId(String parentClassificationId) {
        this.parentClassificationId = parentClassificationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(String serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    public List<Classification> getChildren() {
        return children;
    }

    public void addChild(Classification child) {
        children.add(child);
    }

    public void setChildren(List<Classification> children) {
        this.children = children;
    }

}
