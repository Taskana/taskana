package io.kadai.user.api.models;

import io.kadai.KadaiConfiguration;
import java.util.Set;

/** The User holds some relevant information about the KADAI users. */
public interface User {

  /**
   * Returns the id of the User.
   *
   * @return userId
   */
  String getId();

  /**
   * Sets the id of the User.
   *
   * @param id the id of the User
   */
  void setId(String id);

  /**
   * Returns the groups of the User.
   *
   * @return userGroups
   */
  Set<String> getGroups();

  /**
   * Sets the groups of the User.
   *
   * @param groups the groups of the User
   */
  void setGroups(Set<String> groups);

  /**
   * Returns the permissions of the User.
   *
   * @return permissions
   */
  Set<String> getPermissions();

  /**
   * Sets the permissions of the User.
   *
   * @param permissions the permissions of the User
   */
  void setPermissions(Set<String> permissions);

  /**
   * Returns the first name of the User.
   *
   * @return firstName
   */
  String getFirstName();

  /**
   * Sets the first name of the User.
   *
   * @param firstName the first name of the User
   */
  void setFirstName(String firstName);

  /**
   * Returns the last name of the User.
   *
   * @return lastName
   */
  String getLastName();

  /**
   * Sets the last name of the User.
   *
   * @param lastName the last name of the User
   */
  void setLastName(String lastName);

  /**
   * Returns the full name of the User.
   *
   * @return fullName
   */
  String getFullName();

  /**
   * Sets the full name of the User.
   *
   * @param fullName the full name of the User
   */
  void setFullName(String fullName);

  /**
   * Returns the long name of the User.
   *
   * @return longName
   */
  String getLongName();

  /**
   * Sets the long name of the User.
   *
   * @param longName the long name of the User
   */
  void setLongName(String longName);

  /**
   * Returns the email address of the User.
   *
   * @return email
   */
  String getEmail();

  /**
   * Sets the email address of the User.
   *
   * @param email the email address of the User
   */
  void setEmail(String email);

  /**
   * Returns the phone number of the User.
   *
   * @return phone
   */
  String getPhone();

  /**
   * Sets the phone number of the User.
   *
   * @param phone the phone number of the User
   */
  void setPhone(String phone);

  /**
   * Returns the mobile phone number of the User.
   *
   * @return mobilePhone
   */
  String getMobilePhone();

  /**
   * Sets the mobile phone number of the User.
   *
   * @param mobilePhone the mobile phone number of the User
   */
  void setMobilePhone(String mobilePhone);

  /**
   * Returns the orgLevel4 of the User.
   *
   * @return orgLevel4
   */
  String getOrgLevel4();

  /**
   * Sets the orgLevel4 of the User.
   *
   * @param orgLevel4 the fourth organization level of the User
   */
  void setOrgLevel4(String orgLevel4);

  /**
   * Returns the orgLevel3 of the User.
   *
   * @return orgLevel3
   */
  String getOrgLevel3();

  /**
   * Sets the orgLevel3 of the User.
   *
   * @param orgLevel3 the third organization level of the User
   */
  void setOrgLevel3(String orgLevel3);

  /**
   * Returns the orgLevel2 of the User.
   *
   * @return orgLevel2
   */
  String getOrgLevel2();

  /**
   * Sets the orgLevel2 of the User.
   *
   * @param orgLevel2 the second organization level of the User
   */
  void setOrgLevel2(String orgLevel2);

  /**
   * Returns the orgLevel1 of the User.
   *
   * @return orgLevel1
   */
  String getOrgLevel1();

  /**
   * Sets the orgLevel1 of the User.
   *
   * @param orgLevel1 the first organization level of the User
   */
  void setOrgLevel1(String orgLevel1);

  /**
   * Returns the data of the User.
   *
   * @return data
   */
  String getData();

  /**
   * Sets the data of the User.
   *
   * @param data the data of the User
   */
  void setData(String data);

  /**
   * Returns the domains of the User.
   *
   * <p>The domains are derived from the {@linkplain io.kadai.workbasket.api.WorkbasketPermission
   * WorkbasketPermissions} and the according KADAI property {@linkplain
   * KadaiConfiguration#getMinimalPermissionsToAssignDomains()}.
   *
   * @return domains
   */
  Set<String> getDomains();

  /**
   * Duplicates this User.
   *
   * @return a copy of this User
   */
  User copy();
}
