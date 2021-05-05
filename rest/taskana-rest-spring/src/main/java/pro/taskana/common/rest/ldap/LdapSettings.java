package pro.taskana.common.rest.ldap;

import org.springframework.core.env.Environment;

/** Required settings to run ldap. */
enum LdapSettings {
  TASKANA_LDAP_USER_SEARCH_BASE("taskana.ldap.userSearchBase"),
  TASKANA_LDAP_USER_SEARCH_FILTER_NAME("taskana.ldap.userSearchFilterName"),
  TASKANA_LDAP_USER_SEARCH_FILTER_VALUE("taskana.ldap.userSearchFilterValue"),
  TASKANA_LDAP_USER_FIRSTNAME_ATTRIBUTE("taskana.ldap.userFirstnameAttribute"),
  TASKANA_LDAP_USER_LASTNAME_ATTRIBUTE("taskana.ldap.userLastnameAttribute"),
  TASKANA_LDAP_USER_FULLNAME_ATTRIBUTE("taskana.ldap.userFullnameAttribute"),
  TASKANA_LDAP_USER_ID_ATTRIBUTE("taskana.ldap.userIdAttribute"),
  TASKANA_LDAP_USER_MEMBER_OF_GROUP_ATTRIBUTE("taskana.ldap.userMemberOfGroupAttribute"),
  TASKANA_LDAP_GROUP_SEARCH_BASE("taskana.ldap.groupSearchBase"),
  TASKANA_LDAP_BASE_DN("taskana.ldap.baseDn"),
  TASKANA_LDAP_GROUP_SEARCH_FILTER_NAME("taskana.ldap.groupSearchFilterName"),
  TASKANA_LDAP_GROUP_SEARCH_FILTER_VALUE("taskana.ldap.groupSearchFilterValue"),
  TASKANA_LDAP_GROUP_NAME_ATTRIBUTE("taskana.ldap.groupNameAttribute"),
  TASKANA_LDAP_MIN_SEARCH_FOR_LENGTH("taskana.ldap.minSearchForLength"),
  TASKANA_LDAP_MAX_NUMBER_OF_RETURNED_ACCESS_IDS("taskana.ldap.maxNumberOfReturnedAccessIds"),
  TASKANA_LDAP_GROUPS_OF_USER("taskana.ldap.groupsOfUser");

  private final String key;

  LdapSettings(String key) {
    this.key = key;
  }

  String getKey() {
    return key;
  }

  String getValueFromEnv(Environment env) {
    if (env == null) {
      return null;
    }
    return env.getProperty(key);
  }

  @Override
  public String toString() {
    return key;
  }
}
