package pro.taskana.common.api;

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
  public int hashCode() {
    return Objects.hash(key, domain);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof KeyDomain)) {
      return false;
    }
    KeyDomain other = (KeyDomain) obj;
    return Objects.equals(key, other.key) && Objects.equals(domain, other.domain);
  }

  @Override
  public String toString() {
    return "KeyDomain [key=" + key + ", domain=" + domain + "]";
  }
}
