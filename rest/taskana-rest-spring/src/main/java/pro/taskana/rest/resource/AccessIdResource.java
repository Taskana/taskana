package pro.taskana.rest.resource;

/**
 * resource class for access id validation.
 *
 * @author bbr
 */
public class AccessIdResource {

    public String name;
    public String accessId;

    public AccessIdResource() {

    }

    public AccessIdResource(String name, String accessId) {
        this.accessId = accessId;
        this.name = name;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AccessIdResource [name=");
        builder.append(name);
        builder.append(", accessId=");
        builder.append(accessId);
        builder.append("]");
        return builder.toString();
    }

}
