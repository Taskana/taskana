package pro.taskana.user.internal.models;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import pro.taskana.user.api.models.User;

public class UserImpl implements User {
  private String id;
  private Set<String> groups = Collections.emptySet();
  private Set<String> permissions = Collections.emptySet();
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

  public UserImpl() {}

  protected UserImpl(UserImpl copyFrom) {
    this.id = copyFrom.id;
    this.groups = copyFrom.groups;
    this.permissions = copyFrom.permissions;
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
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public Set<String> getGroups() {
    return groups;
  }

  @Override
  public void setGroups(Set<String> groups) {
    this.groups = groups;
  }

  @Override
  public Set<String> getPermissions() {
    return permissions;
  }

  @Override
  public void setPermissions(Set<String> permissions) {
    this.permissions = permissions;
  }

  @Override
  public String getFirstName() {
    return firstName;
  }

  @Override
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @Override
  public String getLastName() {
    return lastName;
  }

  @Override
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Override
  public String getFullName() {
    return fullName;
  }

  @Override
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @Override
  public String getLongName() {
    return longName;
  }

  @Override
  public void setLongName(String longName) {
    this.longName = longName;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String getPhone() {
    return phone;
  }

  @Override
  public void setPhone(String phone) {
    this.phone = phone;
  }

  @Override
  public String getMobilePhone() {
    return mobilePhone;
  }

  @Override
  public void setMobilePhone(String mobilePhone) {
    this.mobilePhone = mobilePhone;
  }

  @Override
  public String getOrgLevel4() {
    return orgLevel4;
  }

  @Override
  public void setOrgLevel4(String orgLevel4) {
    this.orgLevel4 = orgLevel4;
  }

  @Override
  public String getOrgLevel3() {
    return orgLevel3;
  }

  @Override
  public void setOrgLevel3(String orgLevel3) {
    this.orgLevel3 = orgLevel3;
  }

  @Override
  public String getOrgLevel2() {
    return orgLevel2;
  }

  @Override
  public void setOrgLevel2(String orgLevel2) {
    this.orgLevel2 = orgLevel2;
  }

  @Override
  public String getOrgLevel1() {
    return orgLevel1;
  }

  @Override
  public void setOrgLevel1(String orgLevel1) {
    this.orgLevel1 = orgLevel1;
  }

  @Override
  public String getData() {
    return data;
  }

  @Override
  public void setData(String data) {
    this.data = data;
  }

  @Override
  public Set<String> getDomains() {
    return domains;
  }

  public void setDomains(Set<String> domains) {
    this.domains = domains;
  }

  @Override
  public UserImpl copy() {
    return new UserImpl(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        groups,
        permissions,
        firstName,
        lastName,
        fullName,
        longName,
        email,
        phone,
        mobilePhone,
        orgLevel4,
        orgLevel3,
        orgLevel2,
        orgLevel1,
        data,
        domains);
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
    UserImpl other = (UserImpl) obj;
    return Objects.equals(id, other.id)
        && Objects.equals(groups, other.groups)
        && Objects.equals(permissions, other.permissions)
        && Objects.equals(firstName, other.firstName)
        && Objects.equals(lastName, other.lastName)
        && Objects.equals(fullName, other.fullName)
        && Objects.equals(longName, other.longName)
        && Objects.equals(email, other.email)
        && Objects.equals(phone, other.phone)
        && Objects.equals(mobilePhone, other.mobilePhone)
        && Objects.equals(orgLevel4, other.orgLevel4)
        && Objects.equals(orgLevel3, other.orgLevel3)
        && Objects.equals(orgLevel2, other.orgLevel2)
        && Objects.equals(orgLevel1, other.orgLevel1)
        && Objects.equals(data, other.data)
        && Objects.equals(domains, other.domains);
  }

  @Override
  public String toString() {
    return "UserImpl [id="
        + id
        + ", groups="
        + groups
        + ", permissions="
        + permissions
        + ", firstName="
        + firstName
        + ", lastName="
        + lastName
        + ", fullName="
        + fullName
        + ", longName="
        + longName
        + ", email="
        + email
        + ", phone="
        + phone
        + ", mobilePhone="
        + mobilePhone
        + ", orgLevel4="
        + orgLevel4
        + ", orgLevel3="
        + orgLevel3
        + ", orgLevel2="
        + orgLevel2
        + ", orgLevel1="
        + orgLevel1
        + ", data="
        + data
        + ", domains="
        + domains
        + "]";
  }
}
