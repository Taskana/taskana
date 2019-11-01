package pro.taskana;

import java.util.Objects;

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
        return "KeyDomain ["
            + "key=" + this.key
            + ", domain=" + this.domain
            + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((domain == null) ? 0 : domain.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        KeyDomain other = (KeyDomain) obj;

        return Objects.equals(domain, other.domain)
            && Objects.equals(key, other.key);
    }

}
