package org.taskana.model;

/**
 * ObjectReference entity.
 */
public class ObjectReference {

    private String id;
    private String tenantId;
    private String company;
    private String system;
    private String systemInstance;
    private String type;
    private String value;

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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getSystemInstance() {
        return systemInstance;
    }

    public void setSystemInstance(String systemInstance) {
        this.systemInstance = systemInstance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ObjectReference(");
        sb.append("id=" + id);
        sb.append(", tenantId=" + tenantId);
        sb.append(", company=" + company);
        sb.append(", system=" + system);
        sb.append(", systemInstance=" + systemInstance);
        sb.append(", type=" + type);
        sb.append(", value=" + value);
        return sb.toString();
    }
}
