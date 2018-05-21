package pro.taskana.ldap;

/**
 * Utility class to hold access ids.
 *
 * @author bbr
 */
public class AccessId {

    private String accessId;
    private String name;

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
        builder.append("AccessId [accessId=");
        builder.append(accessId);
        builder.append(", name=");
        builder.append(name);
        builder.append("]");
        return builder.toString();
    }

}
