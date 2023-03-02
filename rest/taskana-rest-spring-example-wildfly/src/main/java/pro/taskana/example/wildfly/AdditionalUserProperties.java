package pro.taskana.example.wildfly;

import java.util.List;

public class AdditionalUserProperties {
  private Boolean enableUserIdHeader;
  private List<String> authorizedUsers;

  public Boolean getEnableUserIdHeader() {
    return enableUserIdHeader;
  }

  public void setEnableUserIdHeader(Boolean enableUserIdHeader) {
    this.enableUserIdHeader = enableUserIdHeader;
  }

  public List<String> getAuthorizedUsers() {
    return authorizedUsers;
  }

  public void setAuthorizedUsers(List<String> authorizedUsers) {
    this.authorizedUsers = authorizedUsers;
  }
}
