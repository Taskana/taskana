package pro.taskana;

/**
 * This class encapsulates key - domain pairs for identification of workbaskets.
 *
 * @author bbr
 */
public class KeyDomain {

    private String key;
    private String domain;

    public KeyDomain(String key, String domain) {
        this.key = key;
        this.domain = domain;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KeyDomain [key=");
        builder.append(key);
        builder.append(", domain=");
        builder.append(domain);
        builder.append("]");
        return builder.toString();
    }

}
