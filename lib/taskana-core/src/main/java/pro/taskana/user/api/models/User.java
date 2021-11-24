package pro.taskana.user.api.models;

/** The User holds some relevant information about the TASKANA users. */
public interface User {

  /**
   * Gets the id of the User.
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
   * Gets the first name of the User.
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
   * Gets the last name of the User.
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
   * Gets the full name of the User.
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
   * Gets the long name of the User.
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
   * Gets the email address of the User.
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
   * Gets the phone number of the User.
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
   * Gets the mobile phone number of the User.
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
   * Gets the orgLevel4 of the User.
   *
   * @return orgLevel4
   */
  String getOrgLevel4();

  /**
   * Sets the fourth organization level of the User.
   *
   * @param orgLevel4 the fourth organization level of the User
   */
  void setOrgLevel4(String orgLevel4);

  /**
   * Gets the orgLevel3 of the User.
   *
   * @return orgLevel3
   */
  String getOrgLevel3();

  /**
   * Sets the third organization level of the User.
   *
   * @param orgLevel3 the third organization level of the User
   */
  void setOrgLevel3(String orgLevel3);

  /**
   * Gets the orgLevel2 of the User.
   *
   * @return orgLevel2
   */
  String getOrgLevel2();

  /**
   * Sets the second organization level of the User.
   *
   * @param orgLevel2 the second organization level of the User
   */
  void setOrgLevel2(String orgLevel2);

  /**
   * Gets the orgLevel1 of the User.
   *
   * @return orgLevel1
   */
  String getOrgLevel1();

  /**
   * Sets the first organization level of the User.
   *
   * @param orgLevel1 the first organization level of the User
   */
  void setOrgLevel1(String orgLevel1);

  /**
   * Gets the data of the User.
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

  User copy();
}
