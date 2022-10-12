package pro.taskana.user.rest.models;

import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.user.api.models.User;

/** The entityModel class for {@linkplain User}. */
public class UserRepresentationModel extends RepresentationModel<UserRepresentationModel> {

  /** Unique Id. */
  @NotNull protected String userId;
  /** The groups of the User. */
  protected Set<String> groups;
  /** The first name of the User. */
  protected String firstName;
  /** The last name of the User. */
  protected String lastName;
  /** The full name of the User. */
  protected String fullName;
  /** The long name of the User. */
  protected String longName;
  /** The email of the User. */
  protected String email;
  /** The phone number of the User. */
  protected String phone;
  /** The mobile phone number of the User. */
  protected String mobilePhone;
  /** The fourth organisation level of the User. */
  protected String orgLevel4;
  /** The third organisation level of the User. */
  protected String orgLevel3;
  /** The second organisation level of the User. */
  protected String orgLevel2;
  /** The first organisation level of the User. */
  protected String orgLevel1;
  /** The data of the User. This field is used for additional information about the User. */
  protected String data;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String id) {
    this.userId = id;
  }

  public Set<String> getGroups() {
    return groups;
  }

  public void setGroups(Set<String> groups) {
    this.groups = groups;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getLongName() {
    return longName;
  }

  public void setLongName(String longName) {
    this.longName = longName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getMobilePhone() {
    return mobilePhone;
  }

  public void setMobilePhone(String mobilePhone) {
    this.mobilePhone = mobilePhone;
  }

  public String getOrgLevel4() {
    return orgLevel4;
  }

  public void setOrgLevel4(String orgLevel4) {
    this.orgLevel4 = orgLevel4;
  }

  public String getOrgLevel3() {
    return orgLevel3;
  }

  public void setOrgLevel3(String orgLevel3) {
    this.orgLevel3 = orgLevel3;
  }

  public String getOrgLevel2() {
    return orgLevel2;
  }

  public void setOrgLevel2(String orgLevel2) {
    this.orgLevel2 = orgLevel2;
  }

  public String getOrgLevel1() {
    return orgLevel1;
  }

  public void setOrgLevel1(String orgLevel1) {
    this.orgLevel1 = orgLevel1;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        userId,
        groups,
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
        data);
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
    if (!super.equals(obj)) {
      return false;
    }
    UserRepresentationModel other = (UserRepresentationModel) obj;
    return userId.equals(other.userId)
        && Objects.equals(groups, other.groups)
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
        && Objects.equals(data, other.data);
  }
}
