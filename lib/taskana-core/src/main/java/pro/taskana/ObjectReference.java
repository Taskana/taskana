package pro.taskana;

import java.util.Objects;

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
        return "ObjectReference ["
            + "id=" + this.id + ", company="
            + this.company + ", system=" + this.system
            + ", systemInstance=" + this.systemInstance
            + ", type=" + this.type + ", value=" + this.value + "]";
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

        return Objects.equals(id, o.id)
            && Objects.equals(company, o.company)
            && Objects.equals(system, o.system)
            && Objects.equals(systemInstance, o.systemInstance)
            && Objects.equals(type, o.type)
            && Objects.equals(value, o.value);
    }
}
