package pro.taskana.model;

/**
 * ObjectReference entity.
 */
public class ObjectReference {

    private String id;
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
        StringBuilder builder = new StringBuilder();
        builder.append("ObjectReference [id=");
        builder.append(id);
        builder.append(", company=");
        builder.append(company);
        builder.append(", system=");
        builder.append(system);
        builder.append(", systemInstance=");
        builder.append(systemInstance);
        builder.append(", type=");
        builder.append(type);
        builder.append(", value=");
        builder.append(value);
        builder.append("]");
        return builder.toString();
    }

}
