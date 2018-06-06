package pro.taskana;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((company == null) ? 0 : company.hashCode());
        result = prime * result + ((system == null) ? 0 : system.hashCode());
        result = prime * result + ((systemInstance == null) ? 0 : systemInstance.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other.getClass() != getClass()) {
            return false;
        }
        ObjectReference o = (ObjectReference) other;

        if (id == null && o.id != null) {
            return false;
        }
        if (id != null && !(id.equals(o.id))) {
            return false;
        }
        if (company == null && o.company != null) {
            return false;
        }
        if (company != null && !(company.equals(o.company))) {
            return false;
        }
        if (system == null && o.system != null) {
            return false;
        }
        if (system != null && !(system.equals(o.system))) {
            return false;
        }
        if (systemInstance == null && o.systemInstance != null) {
            return false;
        }
        if (systemInstance != null && !(systemInstance.equals(o.systemInstance))) {
            return false;
        }
        if (type == null && o.type != null) {
            return false;
        }
        if (type != null && !(type.equals(o.type))) {
            return false;
        }
        if (value == null && o.value != null) {
            return false;
        }
        if (value != null && !(value.equals(o.value))) {
            return false;
        }
        return true;
    }
}
