package pro.taskana.user.internal.models;

import java.util.Collections;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.user.api.models.User;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserImpl implements User {
  private String id;
  private Set<String> groups = Collections.emptySet();
  private String firstName;
  private String lastName;
  private String fullName;
  private String longName;
  private String email;
  private String phone;
  private String mobilePhone;
  private String orgLevel4;
  private String orgLevel3;
  private String orgLevel2;
  private String orgLevel1;
  private String data;
  private Set<String> domains = Collections.emptySet();

  protected UserImpl(UserImpl copyFrom) {
    this.id = copyFrom.id;
    this.groups = copyFrom.groups;
    this.firstName = copyFrom.firstName;
    this.lastName = copyFrom.lastName;
    this.fullName = copyFrom.fullName;
    this.longName = copyFrom.longName;
    this.email = copyFrom.email;
    this.phone = copyFrom.phone;
    this.mobilePhone = copyFrom.mobilePhone;
    this.orgLevel4 = copyFrom.orgLevel4;
    this.orgLevel3 = copyFrom.orgLevel3;
    this.orgLevel2 = copyFrom.orgLevel2;
    this.orgLevel1 = copyFrom.orgLevel1;
    this.data = copyFrom.data;
    this.domains = copyFrom.domains;
  }

  @Override
  public UserImpl copy() {
    return new UserImpl(this);
  }
}
