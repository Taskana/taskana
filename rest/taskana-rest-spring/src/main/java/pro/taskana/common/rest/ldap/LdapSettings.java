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
  TASKANA_LDAP_USER_PHONE_ATTRIBUTE("taskana.ldap.userPhoneAttribute"),
  TASKANA_LDAP_USER_MOBILE_PHONE_ATTRIBUTE("taskana.ldap.userMobilePhoneAttribute"),
  TASKANA_LDAP_USER_EMAIL_ATTRIBUTE("taskana.ldap.userEmailAttribute"),
  TASKANA_LDAP_USER_ID_ATTRIBUTE("taskana.ldap.userIdAttribute"),
  TASKANA_LDAP_USER_ORG_LEVEL_1_ATTRIBUTE("taskana.ldap.userOrglevel1Attribute"),
  TASKANA_LDAP_USER_ORG_LEVEL_2_ATTRIBUTE("taskana.ldap.userOrglevel2Attribute"),
  TASKANA_LDAP_USER_ORG_LEVEL_3_ATTRIBUTE("taskana.ldap.userOrglevel3Attribute"),
  TASKANA_LDAP_USER_ORG_LEVEL_4_ATTRIBUTE("taskana.ldap.userOrglevel4Attribute"),
  TASKANA_LDAP_USER_MEMBER_OF_GROUP_ATTRIBUTE("taskana.ldap.userMemberOfGroupAttribute"),
  TASKANA_LDAP_USER_PERMISSIONS_ATTRIBUTE("taskana.ldap.userPermissionsAttribute"),
  TASKANA_LDAP_GROUP_SEARCH_BASE("taskana.ldap.groupSearchBase"),
  TASKANA_LDAP_BASE_DN("taskana.ldap.baseDn"),
  TASKANA_LDAP_GROUP_SEARCH_FILTER_NAME("taskana.ldap.groupSearchFilterName"),
  TASKANA_LDAP_GROUP_SEARCH_FILTER_VALUE("taskana.ldap.groupSearchFilterValue"),
  TASKANA_LDAP_GROUP_NAME_ATTRIBUTE("taskana.ldap.groupNameAttribute"),
  TASKANA_LDAP_MIN_SEARCH_FOR_LENGTH("taskana.ldap.minSearchForLength"),
  TASKANA_LDAP_MAX_NUMBER_OF_RETURNED_ACCESS_IDS("taskana.ldap.maxNumberOfReturnedAccessIds"),
  TASKANA_LDAP_GROUPS_OF_USER("taskana.ldap.groupsOfUser"),
  TASKANA_LDAP_GROUPS_OF_USER_NAME("taskana.ldap.groupsOfUser.name"),
  TASKANA_LDAP_GROUPS_OF_USER_TYPE("taskana.ldap.groupsOfUser.type");

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
