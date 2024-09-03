package io.kadai.common.rest.ldap;

import static java.util.function.Predicate.not;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.util.LogSanitizer;
import io.kadai.common.rest.models.AccessIdRepresentationModel;
import io.kadai.user.api.models.User;
import io.kadai.user.internal.models.UserImpl;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.naming.InvalidNameException;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.NotPresentFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.filter.PresentFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Component;

/** Class for Ldap access. */
@Component
public class LdapClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(LdapClient.class);
  private static final String CN = "cn";

  private final KadaiConfiguration kadaiConfiguration;
  private final Environment env;
  private final LdapTemplate ldapTemplate;
  private final boolean useLowerCaseForAccessIds;
  private boolean active = false;
  private int minSearchForLength;
  private int maxNumberOfReturnedAccessIds;
  private String message;

  @Autowired
  public LdapClient(
      Environment env, LdapTemplate ldapTemplate, KadaiConfiguration kadaiConfiguration) {
    this.env = env;
    this.ldapTemplate = ldapTemplate;
    this.kadaiConfiguration = kadaiConfiguration;
    this.useLowerCaseForAccessIds = KadaiConfiguration.shouldUseLowerCaseForAccessIds();
  }

  /**
   * Search LDAP for matching users or groups or permissions.
   *
   * @param name lookup string for names or groups or permissions
   * @return a list of AccessIdResources sorted by AccessId and limited to
   *     maxNumberOfReturnedAccessIds
   * @throws InvalidArgumentException if input is shorter than minSearchForLength
   * @throws InvalidNameException thrown if name is not a valid dn
   */
  public List<AccessIdRepresentationModel> searchUsersAndGroupsAndPermissions(final String name)
      throws InvalidArgumentException, InvalidNameException {
    isInitOrFail();
    testMinSearchForLength(name);

    List<AccessIdRepresentationModel> accessIds = new ArrayList<>();
    if (nameIsDn(name)) {
      AccessIdRepresentationModel groupByDn = searchAccessIdByDn(name);
      if (groupByDn != null) {
        accessIds.add(groupByDn);
      }
    } else {
      accessIds.addAll(searchUsersByNameOrAccessId(name));
      accessIds.addAll(searchGroupsByName(name));
      accessIds.addAll(searchPermissionsByName(name));
    }
    sortListOfAccessIdResources(accessIds);
    return getFirstPageOfaResultList(accessIds);
  }

  public List<AccessIdRepresentationModel> searchUsersByNameOrAccessIdInUserRole(
      final String nameOrAccessId) throws InvalidArgumentException {

    LOGGER.debug(
        "entry to searchUsersByNameOrAccessIdInUserRoleGroups(nameOrAccessId = {}).",
        LogSanitizer.stripLineBreakingChars(nameOrAccessId));

    isInitOrFail();
    testMinSearchForLength(nameOrAccessId);

    final OrFilter userDetailsOrFilter = new OrFilter();
    userDetailsOrFilter.or(
        new WhitespaceWildcardsFilter(getUserFirstnameAttribute(), nameOrAccessId));
    userDetailsOrFilter.or(
        new WhitespaceWildcardsFilter(getUserLastnameAttribute(), nameOrAccessId));
    userDetailsOrFilter.or(
        new WhitespaceWildcardsFilter(getUserFullnameAttribute(), nameOrAccessId));
    userDetailsOrFilter.or(new WhitespaceWildcardsFilter(getUserIdAttribute(), nameOrAccessId));

    Set<String> userGroups = kadaiConfiguration.getRoleMap().get(KadaiRole.USER);

    final OrFilter groupMembershipOrFilter = new OrFilter();
    userGroups.forEach(
        group ->
            groupMembershipOrFilter.or(new EqualsFilter(getUserMemberOfGroupAttribute(), group)));

    final AndFilter andFilter = new AndFilter();
    andFilter.and(userDetailsOrFilter);
    andFilter.and(groupMembershipOrFilter);
    andFilter.and(new EqualsFilter(getUserSearchFilterName(), getUserSearchFilterValue()));

    final List<AccessIdRepresentationModel> accessIds =
        ldapTemplate.search(
            getUserSearchBase(),
            andFilter.encode(),
            SearchControls.SUBTREE_SCOPE,
            getLookUpUserAttributesToReturn(),
            new UserContextMapper());
    LOGGER.debug(
        "exit from searchUsersByNameOrAccessIdInUserRoleGroups. Retrieved the following users: {}.",
        accessIds);
    return accessIds;
  }

  public List<User> searchUsersInUserRole() {

    Set<String> userGroupsOrUser = kadaiConfiguration.getRoleMap().get(KadaiRole.USER);

    final OrFilter userOrGroupFilter = new OrFilter();
    userGroupsOrUser.forEach(
        userOrGroup -> {
          userOrGroupFilter.or(new EqualsFilter(getUserMemberOfGroupAttribute(), userOrGroup));
          userOrGroupFilter.or(new EqualsFilter(getUserIdAttribute(), userOrGroup));
        });

    final List<User> users =
        ldapTemplate.search(
            getUserSearchBase(),
            userOrGroupFilter.encode(),
            SearchControls.SUBTREE_SCOPE,
            getLookUpUserInfoAttributesToReturn(),
            new UserInfoContextMapper());

    LOGGER.debug("exit from searchUsersInUserRole. Retrieved the following users: {}.", users);

    return users;
  }

  public List<AccessIdRepresentationModel> searchUsersByNameOrAccessId(final String name)
      throws InvalidArgumentException {
    isInitOrFail();
    testMinSearchForLength(name);

    final AndFilter andFilter = new AndFilter();
    andFilter.and(new EqualsFilter(getUserSearchFilterName(), getUserSearchFilterValue()));
    final OrFilter orFilter = new OrFilter();

    orFilter.or(new WhitespaceWildcardsFilter(getUserFirstnameAttribute(), name));
    orFilter.or(new WhitespaceWildcardsFilter(getUserLastnameAttribute(), name));
    orFilter.or(new WhitespaceWildcardsFilter(getUserFullnameAttribute(), name));
    orFilter.or(new WhitespaceWildcardsFilter(getUserIdAttribute(), name));
    andFilter.and(orFilter);

    LOGGER.debug("Using filter '{}' for LDAP query.", andFilter);

    return ldapTemplate.search(
        getUserSearchBase(),
        andFilter.encode(),
        SearchControls.SUBTREE_SCOPE,
        getLookUpUserAttributesToReturn(),
        new UserContextMapper());
  }

  public List<AccessIdRepresentationModel> getUsersByAccessId(final String accessId) {
    isInitOrFail();

    final AndFilter andFilter = new AndFilter();
    andFilter.and(new EqualsFilter(getUserSearchFilterName(), getUserSearchFilterValue()));
    andFilter.and(new EqualsFilter(getUserIdAttribute(), accessId));

    String[] userAttributesToReturn = {
      getUserFirstnameAttribute(), getUserLastnameAttribute(), getUserIdAttribute()
    };

    LOGGER.debug("Using filter '{}' for LDAP query.", andFilter);

    return ldapTemplate.search(
        getUserSearchBase(),
        andFilter.encode(),
        SearchControls.SUBTREE_SCOPE,
        userAttributesToReturn,
        new UserContextMapper());
  }

  public List<AccessIdRepresentationModel> searchGroupsByName(final String name)
      throws InvalidArgumentException {
    isInitOrFail();
    testMinSearchForLength(name);

    final AndFilter andFilter = new AndFilter();
    andFilter.and(new EqualsFilter(getGroupSearchFilterName(), getGroupSearchFilterValue()));
    final OrFilter orFilter = new OrFilter();
    orFilter.or(new WhitespaceWildcardsFilter(getGroupNameAttribute(), name));
    if (!CN.equals(getGroupNameAttribute())) {
      orFilter.or(new WhitespaceWildcardsFilter(CN, name));
    }

    andFilter.and(orFilter);
    final AndFilter andFilter2;
    if (!getUserPermissionsAttribute().isEmpty()) {
      andFilter2 = new AndFilter();
      andFilter2.and(new NotPresentFilter(getUserPermissionsAttribute()));
      andFilter2.and(andFilter);
    }
    else {
      andFilter2 = andFilter;
    }

    LOGGER.debug("Using filter '{}' for LDAP query.", andFilter);

    return ldapTemplate.search(
        getGroupSearchBase(),
        andFilter2.encode(),
        SearchControls.SUBTREE_SCOPE,
        getLookUpGroupAttributesToReturn(),
        new GroupContextMapper());
  }

  public Map<String, List<String>> searchAccessIdForGroupsAndPermissionsByDn(List<String> dns)
      throws InvalidNameException {
    List<String> accessIdsOfGroupsAndPermissions = new ArrayList<>();
    List<AccessIdRepresentationModel> permissions = new ArrayList<>();
    List<String> accessIdsOfPermissions = new ArrayList<>();
    for (String groupOrPermission : dns) {
      accessIdsOfGroupsAndPermissions.add(searchAccessIdByDn(groupOrPermission).getAccessId());
    }
    for (String groupOrPermission : accessIdsOfGroupsAndPermissions) {
      permissions.addAll(searchPermissionsByName(groupOrPermission));
    }
    for (AccessIdRepresentationModel permission : permissions) {
      accessIdsOfPermissions.add(permission.getAccessId());
    }
    List<String> accessIdsOfGroups = new ArrayList<>(accessIdsOfGroupsAndPermissions);
    accessIdsOfGroups.removeAll(accessIdsOfPermissions);
    Map<String, List<String>> map = new HashMap<>();
    map.put("permissions", accessIdsOfPermissions);
    map.put("groups", accessIdsOfGroups);
    return map;
  }

  public List<AccessIdRepresentationModel> searchPermissionsByName(final String name)
      throws InvalidArgumentException {
    if (getUserPermissionsAttribute().isEmpty() || getPermissionSearchFilterName().isEmpty()) {
      return Collections.emptyList();
    }
    isInitOrFail();
    testMinSearchForLength(name);

    final AndFilter andFilter = new AndFilter();
    andFilter.and(
        new EqualsFilter(getPermissionSearchFilterName(), getPermissionSearchFilterValue()));
    final OrFilter orFilter = new OrFilter();
    orFilter.or(new WhitespaceWildcardsFilter(getUserPermissionsAttribute(), name));
    if (!CN.equals(getUserPermissionsAttribute())) {
      orFilter.or(new WhitespaceWildcardsFilter(CN, name));
    }
    final AndFilter andFilter2 = new AndFilter();
    andFilter2.and(new PresentFilter(getUserPermissionsAttribute()));
    andFilter.and(orFilter);
    andFilter2.and(andFilter);

    LOGGER.debug("Using filter '{}' for LDAP query.", andFilter);

    return ldapTemplate.search(
        getPermissionSearchBase(),
        andFilter2.encode(),
        SearchControls.SUBTREE_SCOPE,
        getLookUpPermissionAttributesToReturn(),
        new PermissionContextMapper());
  }

  public AccessIdRepresentationModel searchAccessIdByDn(final String dn)
      throws InvalidNameException {
    isInitOrFail();
    // Obviously Spring LdapTemplate does have a inconsistency and always adds the base name to the
    // given DN.
    // https://stackoverflow.com/questions/55285743/spring-ldaptemplate-how-to-lookup-fully-qualified-dn-with-configured-base-dn
    // Therefore we have to remove the base name from the dn before performing the lookup
    String nameWithoutBaseDn = getNameWithoutBaseDn(dn).toLowerCase();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Removed baseDN {} from given DN. New DN to be used: {}", getBaseDn(), nameWithoutBaseDn);
    }
    return ldapTemplate.lookup(
        new LdapName(nameWithoutBaseDn),
        getLookUpUserAndGroupAndPermissionAttributesToReturn(),
        new DnContextMapper());
  }

  public List<AccessIdRepresentationModel> searchGroupsAccessIdIsMemberOf(final String accessId)
      throws InvalidArgumentException, InvalidNameException {
    isInitOrFail();
    testMinSearchForLength(accessId);

    String dn = searchDnForAccessId(accessId);
    if (dn == null || dn.isEmpty()) {
      throw new InvalidArgumentException("The AccessId is invalid");
    }

    final AndFilter andFilter = new AndFilter();
    andFilter.and(new EqualsFilter(getGroupSearchFilterName(), getGroupSearchFilterValue()));
    final OrFilter orFilter = new OrFilter();
    if (!"DN".equalsIgnoreCase(getGroupsOfUserType())) {
      orFilter.or(new EqualsFilter(getGroupsOfUserName(), accessId));
    }
    orFilter.or(new EqualsFilter(getGroupsOfUserName(), dn));
    andFilter.and(orFilter);
    final AndFilter andFilter2;
    if (!getUserPermissionsAttribute().isEmpty()) {
      andFilter2 = new AndFilter();
      andFilter2.and(new NotPresentFilter(getUserPermissionsAttribute()));
      andFilter2.and(andFilter);
    }
    else {
      andFilter2 = andFilter;
    }

    String[] userAttributesToReturn = {getUserIdAttribute(), getGroupNameAttribute()};
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Using filter '{}' for LDAP query with group search base {}.",
          andFilter2,
          getGroupSearchBase());
    }

    return ldapTemplate.search(
        getGroupSearchBase(),
        andFilter2.encode(),
        SearchControls.SUBTREE_SCOPE,
        userAttributesToReturn,
        new GroupContextMapper());
  }

  public List<AccessIdRepresentationModel> searchPermissionsAccessIdHas(final String accessId)
      throws InvalidArgumentException, InvalidNameException {
    if (getUserPermissionsAttribute().isEmpty() || getPermissionSearchFilterName().isEmpty()) {
      return Collections.emptyList();
    }
    isInitOrFail();
    testMinSearchForLength(accessId);

    String dn = searchDnForAccessId(accessId);
    if (dn == null || dn.isEmpty()) {
      throw new InvalidArgumentException("The AccessId is invalid");
    }

    final AndFilter andFilter = new AndFilter();
    andFilter.and(
        new EqualsFilter(getPermissionSearchFilterName(), getPermissionSearchFilterValue()));
    final OrFilter orFilter = new OrFilter();
    if (!"DN".equalsIgnoreCase(getPermissionsOfUserType())) {
      orFilter.or(new EqualsFilter(getPermissionsOfUserName(), accessId));
    }
    orFilter.or(new EqualsFilter(getPermissionsOfUserName(), dn));
    final AndFilter andFilter2 = new AndFilter();
    andFilter2.and(new PresentFilter(getUserPermissionsAttribute()));
    andFilter.and(orFilter);
    andFilter2.and(andFilter);

    String[] userAttributesToReturn = {getUserIdAttribute(), getUserPermissionsAttribute()};
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Using filter '{}' for LDAP query with group search base {}.",
          andFilter2,
          getPermissionSearchBase());
    }

    return ldapTemplate.search(
        getPermissionSearchBase(),
        andFilter2.encode(),
        SearchControls.SUBTREE_SCOPE,
        userAttributesToReturn,
        new PermissionContextMapper());
  }

  /**
   * Performs a lookup to retrieve correct DN for the given access id.
   *
   * @param accessId The access id to lookup
   * @return the LDAP Distinguished Name for the access id
   * @throws InvalidArgumentException thrown if the given access id is ambiguous.
   * @throws InvalidNameException thrown if name is not a valid dn
   */
  public String searchDnForAccessId(String accessId)
      throws InvalidArgumentException, InvalidNameException {
    isInitOrFail();

    if (nameIsDn(accessId)) {
      AccessIdRepresentationModel groupByDn = searchAccessIdByDn(accessId);
      return groupByDn.getAccessId();
    } else {
      return searchDnForAccessIdIfAccessIdNameIsNotDn(accessId);
    }
  }

  private String searchDnForAccessIdIfAccessIdNameIsNotDn(String accessId) {
    final List<String> distinguishedNames = searchDnForUserAccessId(accessId);
    if (distinguishedNames == null || distinguishedNames.isEmpty()) {
      final List<String> distinguishedNamesPermissions = searchDnForPermissionAccessId(accessId);
      if (distinguishedNamesPermissions == null || distinguishedNamesPermissions.isEmpty()) {
        final List<String> distinguishedNamesGroups = searchDnForGroupAccessId(accessId);
        if (distinguishedNamesGroups == null || distinguishedNamesGroups.isEmpty()) {
          return null;
        } else if (distinguishedNamesGroups.size() > 1) {
          throw new InvalidArgumentException("Ambiguous access id found: " + accessId);
        } else {
          return distinguishedNamesGroups.get(0);
        }
      } else if (distinguishedNamesPermissions.size() > 1) {
        throw new InvalidArgumentException("Ambiguous access id found: " + accessId);
      } else {
        return distinguishedNamesPermissions.get(0);
      }
    } else if (distinguishedNames.size() > 1) {
      throw new InvalidArgumentException("Ambiguous access id found: " + accessId);
    } else {
      return distinguishedNames.get(0);
    }
  }

  private List<String> searchDnForUserAccessId(String accessId) {
    final AndFilter andFilter = new AndFilter();
    andFilter.and(new EqualsFilter(getUserSearchFilterName(), getUserSearchFilterValue()));
    final OrFilter orFilter = new OrFilter();
    orFilter.or(new EqualsFilter(getUserIdAttribute(), accessId));
    andFilter.and(orFilter);

    LOGGER.debug(
        "Using filter '{}' for LDAP query with user search base {}.",
        andFilter,
        getUserSearchBase());

    return ldapTemplate.search(
        getUserSearchBase(),
        andFilter.encode(),
        SearchControls.SUBTREE_SCOPE,
        null,
        new DnStringContextMapper());
  }

  private List<String> searchDnForPermissionAccessId(String accessId) {
    if (getUserPermissionsAttribute().isEmpty() || getPermissionSearchFilterName().isEmpty()) {
      return Collections.emptyList();
    }
    final AndFilter andFilter = new AndFilter();
    andFilter.and(
        new EqualsFilter(getPermissionSearchFilterName(), getPermissionSearchFilterValue()));
    final OrFilter orFilter = new OrFilter();
    orFilter.or(new EqualsFilter(getUserPermissionsAttribute(), accessId));
    final AndFilter andFilterPermission2 = new AndFilter();
    andFilter.and(new PresentFilter(getUserPermissionsAttribute()));
    andFilter.and(orFilter);
    andFilterPermission2.and(andFilter);

    LOGGER.debug(
        "Using filter '{}' for LDAP query with user search base {}.",
        andFilterPermission2,
        getPermissionSearchBase());

    return ldapTemplate.search(
        getPermissionSearchBase(),
        andFilterPermission2.encode(),
        SearchControls.SUBTREE_SCOPE,
        null,
        new DnStringContextMapper());
  }

  private List<String> searchDnForGroupAccessId(String accessId) {
    final AndFilter andFilter = new AndFilter();
    andFilter.and(new EqualsFilter(getGroupSearchFilterName(), getGroupSearchFilterValue()));
    final OrFilter orFilter = new OrFilter();
    orFilter.or(new EqualsFilter(getGroupNameAttribute(), accessId));
    final AndFilter andFilter2 = new AndFilter();
    andFilter2.and(new NotPresentFilter(getUserPermissionsAttribute()));
    andFilter.and(orFilter);
    andFilter2.and(andFilter);

    LOGGER.debug(
        "Using filter '{}' for LDAP query with user search base {}.",
        andFilter2,
        getPermissionSearchBase());

    return ldapTemplate.search(
        getPermissionSearchBase(),
        andFilter.encode(),
        SearchControls.SUBTREE_SCOPE,
        null,
        new DnStringContextMapper());
  }

  /**
   * Validates a given AccessId / name.
   *
   * @param name lookup string for names or groups
   * @return whether the given name is valid or not
   * @throws InvalidNameException thrown if name is not a valid dn
   */
  public boolean validateAccessId(final String name) throws InvalidNameException {
    isInitOrFail();

    if (nameIsDn(name)) {

      AccessIdRepresentationModel groupByDn = searchAccessIdByDn(name);

      return groupByDn != null;

    } else {

      final AndFilter andFilter = new AndFilter();
      andFilter.and(new EqualsFilter(getUserSearchFilterName(), getUserSearchFilterValue()));

      final OrFilter orFilter = new OrFilter();
      orFilter.or(new EqualsFilter(getUserIdAttribute(), name));

      andFilter.and(orFilter);

      final List<AccessIdRepresentationModel> accessIds =
          ldapTemplate.search(
              getUserSearchBase(),
              andFilter.encode(),
              SearchControls.SUBTREE_SCOPE,
              getLookUpUserAttributesToReturn(),
              new UserContextMapper());

      return !accessIds.isEmpty();
    }
  }

  public String getUserSearchBase() {
    return LdapSettings.KADAI_LDAP_USER_SEARCH_BASE.getValueFromEnv(env);
  }

  public String getUserSearchFilterName() {
    return LdapSettings.KADAI_LDAP_USER_SEARCH_FILTER_NAME.getValueFromEnv(env);
  }

  public String getUserSearchFilterValue() {
    return LdapSettings.KADAI_LDAP_USER_SEARCH_FILTER_VALUE.getValueFromEnv(env);
  }

  public String getUserFirstnameAttribute() {
    return LdapSettings.KADAI_LDAP_USER_FIRSTNAME_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getUserLastnameAttribute() {
    return LdapSettings.KADAI_LDAP_USER_LASTNAME_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getUserPhoneAttribute() {
    return LdapSettings.KADAI_LDAP_USER_PHONE_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getUserMobilePhoneAttribute() {
    return LdapSettings.KADAI_LDAP_USER_MOBILE_PHONE_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getUserEmailAttribute() {
    return LdapSettings.KADAI_LDAP_USER_EMAIL_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getUserOrgLevel1Attribute() {
    return LdapSettings.KADAI_LDAP_USER_ORG_LEVEL_1_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getUserOrgLevel2Attribute() {
    return LdapSettings.KADAI_LDAP_USER_ORG_LEVEL_2_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getUserOrgLevel3Attribute() {
    return LdapSettings.KADAI_LDAP_USER_ORG_LEVEL_3_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getUserOrgLevel4Attribute() {
    return LdapSettings.KADAI_LDAP_USER_ORG_LEVEL_4_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getUserIdAttribute() {
    return LdapSettings.KADAI_LDAP_USER_ID_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getUserMemberOfGroupAttribute() {
    return LdapSettings.KADAI_LDAP_USER_MEMBER_OF_GROUP_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getUserPermissionsAttribute() {
    return LdapSettings.KADAI_LDAP_USER_PERMISSIONS_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getPermissionSearchBase() {
    return LdapSettings.KADAI_LDAP_PERMISSION_SEARCH_BASE.getValueFromEnv(env);
  }

  public String getPermissionSearchFilterName() {
    return LdapSettings.KADAI_LDAP_PERMISSION_SEARCH_FILTER_NAME.getValueFromEnv(env);
  }

  public String getPermissionSearchFilterValue() {
    return LdapSettings.KADAI_LDAP_PERMISSION_SEARCH_FILTER_VALUE.getValueFromEnv(env);
  }

  public String getPermissionNameAttribute() {
    return LdapSettings.KADAI_LDAP_PERMISSION_NAME_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getGroupSearchBase() {
    return LdapSettings.KADAI_LDAP_GROUP_SEARCH_BASE.getValueFromEnv(env);
  }

  public String getBaseDn() {
    return LdapSettings.KADAI_LDAP_BASE_DN.getValueFromEnv(env);
  }

  public String getGroupSearchFilterName() {
    return LdapSettings.KADAI_LDAP_GROUP_SEARCH_FILTER_NAME.getValueFromEnv(env);
  }

  public String getGroupSearchFilterValue() {
    return LdapSettings.KADAI_LDAP_GROUP_SEARCH_FILTER_VALUE.getValueFromEnv(env);
  }

  public String getGroupNameAttribute() {
    return LdapSettings.KADAI_LDAP_GROUP_NAME_ATTRIBUTE.getValueFromEnv(env);
  }

  public int calcMinSearchForLength(int defaultValue) {
    String envValue = LdapSettings.KADAI_LDAP_MIN_SEARCH_FOR_LENGTH.getValueFromEnv(env);
    if (envValue == null || envValue.isEmpty()) {
      return defaultValue;
    }
    return Integer.parseInt(envValue);
  }

  public int getMinSearchForLength() {
    return minSearchForLength;
  }

  public int calcMaxNumberOfReturnedAccessIds(int defaultValue) {
    String envValue =
        LdapSettings.KADAI_LDAP_MAX_NUMBER_OF_RETURNED_ACCESS_IDS.getValueFromEnv(env);
    if (envValue == null || envValue.isEmpty()) {
      return defaultValue;
    }
    return Integer.parseInt(envValue);
  }

  public boolean useDnForGroups() {
    String envValue = LdapSettings.KADAI_LDAP_USE_DN_FOR_GROUPS.getValueFromEnv(env);
    if (envValue == null || envValue.isEmpty()) {
      return true;
    }
    return Boolean.parseBoolean(envValue);
  }

  public int getMaxNumberOfReturnedAccessIds() {
    return maxNumberOfReturnedAccessIds;
  }

  public String getGroupsOfUserName() {
    String groupsOfUser = LdapSettings.KADAI_LDAP_GROUPS_OF_USER_NAME.getValueFromEnv(env);
    if (groupsOfUser == null || groupsOfUser.isEmpty()) {
      groupsOfUser = LdapSettings.KADAI_LDAP_GROUPS_OF_USER.getValueFromEnv(env);
    }
    return groupsOfUser;
  }

  public String getGroupsOfUserType() {
    return LdapSettings.KADAI_LDAP_GROUPS_OF_USER_TYPE.getValueFromEnv(env);
  }

  public String getPermissionsOfUserName() {
    String permissionsOfUser =
        LdapSettings.KADAI_LDAP_PERMISSIONS_OF_USER_NAME.getValueFromEnv(env);
    if (permissionsOfUser == null || permissionsOfUser.isEmpty()) {
      permissionsOfUser = LdapSettings.KADAI_LDAP_PERMISSIONS_OF_USER.getValueFromEnv(env);
    }
    return permissionsOfUser;
  }

  public String getPermissionsOfUserType() {
    return LdapSettings.KADAI_LDAP_PERMISSIONS_OF_USER_TYPE.getValueFromEnv(env);
  }

  public boolean isUser(String accessId) {
    return !getUsersByAccessId(accessId).isEmpty();
  }

  boolean nameIsDn(String name) {
    return name.toLowerCase().endsWith(getBaseDn().toLowerCase());
  }

  List<AccessIdRepresentationModel> getFirstPageOfaResultList(
      List<AccessIdRepresentationModel> accessIds) {
    return accessIds.subList(0, Math.min(accessIds.size(), maxNumberOfReturnedAccessIds));
  }

  void isInitOrFail() {
    if (!active) {
      throw new SystemException(
          String.format(
              "LdapClient was called but is not active due to missing configuration: %s", message));
    }
  }

  /**
   * Sorts a list of AccessIds by their accessId, null values last.
   *
   * <p>IMPORTANT: The passed list has to implement the optional {@link List#sort} operation.
   * Otherwise an exception is thrown.
   *
   * @param accessIds the list which should be sorted
   */
  void sortListOfAccessIdResources(List<AccessIdRepresentationModel> accessIds) {
    accessIds.sort(
        Comparator.comparing(
            AccessIdRepresentationModel::getAccessId,
            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
  }

  String getNameWithoutBaseDn(String name) {
    // (?i) --> case insensitive replacement
    return name.replaceAll("(?i)" + Pattern.quote("," + getBaseDn()), "");
  }

  String[] getLookUpGroupAttributesToReturn() {
    if (CN.equals(getGroupNameAttribute())) {
      return new String[] {CN, getGroupSearchFilterName()};
    }
    return new String[] {getGroupNameAttribute(), CN, getGroupSearchFilterName()};
  }

  String[] getLookUpPermissionAttributesToReturn() {
    return new String[] {getUserPermissionsAttribute(), getPermissionSearchFilterName()};
  }

  String[] getLookUpUserAndGroupAndPermissionAttributesToReturn() {
    return Stream.concat(
            Stream.concat(
                Arrays.stream(getLookUpUserAttributesToReturn()),
                Arrays.stream(getLookUpGroupAttributesToReturn())),
            Arrays.stream(getLookUpPermissionAttributesToReturn()))
        .toArray(String[]::new);
  }

  String[] getLookUpUserAttributesToReturn() {
    return new String[] {
      getUserFirstnameAttribute(),
      getUserLastnameAttribute(),
      getUserIdAttribute(),
      getUserSearchFilterName()
    };
  }

  String[] getLookUpUserInfoAttributesToReturn() {
    return new String[] {
      getUserIdAttribute(),
      getUserMemberOfGroupAttribute(),
      getUserPermissionsAttribute(),
      getUserFirstnameAttribute(),
      getUserLastnameAttribute(),
      getUserFullnameAttribute(),
      getUserPhoneAttribute(),
      getUserMobilePhoneAttribute(),
      getUserEmailAttribute(),
      getUserOrgLevel1Attribute(),
      getUserOrgLevel2Attribute(),
      getUserOrgLevel3Attribute(),
      getUserOrgLevel4Attribute()
    };
  }

  @PostConstruct
  void init() {
    minSearchForLength = calcMinSearchForLength(3);
    maxNumberOfReturnedAccessIds = calcMaxNumberOfReturnedAccessIds(50);

    ldapTemplate.setDefaultCountLimit(maxNumberOfReturnedAccessIds);

    final List<LdapSettings> missingConfigurations = checkForMissingConfigurations();

    if (!missingConfigurations.isEmpty()) {
      message = String.format("LDAP configurations are missing: %s", missingConfigurations);
      throw new SystemException(message);
    }
    active = true;
  }

  List<LdapSettings> checkForMissingConfigurations() {
    return Arrays.stream(LdapSettings.values())
        // optional settings
        .filter(not(LdapSettings.KADAI_LDAP_MAX_NUMBER_OF_RETURNED_ACCESS_IDS::equals))
        .filter(not(LdapSettings.KADAI_LDAP_MIN_SEARCH_FOR_LENGTH::equals))
        .filter(not(LdapSettings.KADAI_LDAP_USER_EMAIL_ATTRIBUTE::equals))
        .filter(not(LdapSettings.KADAI_LDAP_USER_PHONE_ATTRIBUTE::equals))
        .filter(not(LdapSettings.KADAI_LDAP_USER_MOBILE_PHONE_ATTRIBUTE::equals))
        .filter(not(LdapSettings.KADAI_LDAP_USER_ORG_LEVEL_1_ATTRIBUTE::equals))
        .filter(not(LdapSettings.KADAI_LDAP_USER_ORG_LEVEL_2_ATTRIBUTE::equals))
        .filter(not(LdapSettings.KADAI_LDAP_USER_ORG_LEVEL_3_ATTRIBUTE::equals))
        .filter(not(LdapSettings.KADAI_LDAP_USER_ORG_LEVEL_4_ATTRIBUTE::equals))
        .filter(not(LdapSettings.KADAI_LDAP_GROUPS_OF_USER::equals))
        .filter(not(LdapSettings.KADAI_LDAP_GROUPS_OF_USER_NAME::equals))
        .filter(not(LdapSettings.KADAI_LDAP_GROUPS_OF_USER_TYPE::equals))
        .filter(not(LdapSettings.KADAI_LDAP_PERMISSIONS_OF_USER::equals))
        .filter(not(LdapSettings.KADAI_LDAP_PERMISSIONS_OF_USER_NAME::equals))
        .filter(not(LdapSettings.KADAI_LDAP_PERMISSIONS_OF_USER_TYPE::equals))
        .filter(p -> p.getValueFromEnv(env) == null)
        .toList();
  }

  void testMinSearchForLength(final String name) throws InvalidArgumentException {
    if (name == null || name.length() < minSearchForLength) {
      throw new InvalidArgumentException(
          String.format(
              "search for string %s is too short. Minimum Length is %s",
              name, getMinSearchForLength()));
    }
  }

  private String getUserFullnameAttribute() {
    return LdapSettings.KADAI_LDAP_USER_FULLNAME_ATTRIBUTE.getValueFromEnv(env);
  }

  private String getDnFromContext(final DirContextOperations context) {
    String dn = LdapNameBuilder.newInstance(getBaseDn()).add(context.getDn()).build().toString();
    if (useLowerCaseForAccessIds) {
      return dn.toLowerCase();
    } else {
      return dn;
    }
  }

  private String getUserIdFromContext(final DirContextOperations context) {
    String userId = context.getStringAttribute(getUserIdAttribute());
    if (userId != null && useLowerCaseForAccessIds) {
      return userId.toLowerCase();
    } else {
      return userId;
    }
  }

  private String getGroupIdFromContext(final DirContextOperations context) {
    String groupId = context.getStringAttribute(getGroupNameAttribute());
    if (groupId != null && useLowerCaseForAccessIds) {
      return groupId.toLowerCase();
    } else {
      return groupId;
    }
  }

  private Set<String> getGroupIdsFromContext(final DirContextOperations context) {
    String[] groupAttributes = context.getStringAttributes(getUserMemberOfGroupAttribute());
    Set<String> groups = groupAttributes != null ? Set.of(groupAttributes) : Collections.emptySet();
    if (useLowerCaseForAccessIds) {
      groups =
          groups.stream()
              .filter(Objects::nonNull)
              .map(String::toLowerCase)
              .collect(Collectors.toSet());
    }
    return groups;
  }

  private String getPermissionIdFromContext(final DirContextOperations context) {
    String permissionId = context.getStringAttribute(getUserPermissionsAttribute());
    if (permissionId != null && useLowerCaseForAccessIds) {
      return permissionId.toLowerCase();
    } else {
      return permissionId;
    }
  }

  private Set<String> getPermissionIdsFromContext(final DirContextOperations context) {
    String[] permissionAttributes = context.getStringAttributes(getUserPermissionsAttribute());
    Set<String> permissions =
        permissionAttributes != null ? Set.of(permissionAttributes) : Collections.emptySet();
    if (useLowerCaseForAccessIds) {
      permissions =
          permissions.stream()
              .filter(Objects::nonNull)
              .map(String::toLowerCase)
              .collect(Collectors.toSet());
    }
    return permissions;
  }

  /** Context Mapper for user entries. */
  class GroupContextMapper extends AbstractContextMapper<AccessIdRepresentationModel> {

    @Override
    public AccessIdRepresentationModel doMapFromContext(final DirContextOperations context) {
      final AccessIdRepresentationModel accessId = new AccessIdRepresentationModel();
      if (useDnForGroups()) {
        accessId.setAccessId(getDnFromContext(context)); // fully qualified dn
      } else {
        accessId.setAccessId(getGroupIdFromContext(context));
      }
      accessId.setName(context.getStringAttribute(getGroupNameAttribute()));
      return accessId;
    }
  }

  class PermissionContextMapper extends AbstractContextMapper<AccessIdRepresentationModel> {

    @Override
    public AccessIdRepresentationModel doMapFromContext(final DirContextOperations context) {
      final AccessIdRepresentationModel accessId = new AccessIdRepresentationModel();
      accessId.setAccessId(getPermissionIdFromContext(context)); // fully qualified dn
      accessId.setName(context.getStringAttribute(getUserPermissionsAttribute()));
      return accessId;
    }
  }

  /** Context Mapper for user info entries. */
  class UserInfoContextMapper extends AbstractContextMapper<User> {

    @Override
    public User doMapFromContext(final DirContextOperations context) {
      final User user = new UserImpl();
      user.setId(getUserIdFromContext(context));
      user.setGroups(getGroupIdsFromContext(context));
      user.setPermissions(getPermissionIdsFromContext(context));
      user.setFirstName(context.getStringAttribute(getUserFirstnameAttribute()));
      user.setLastName(context.getStringAttribute(getUserLastnameAttribute()));
      user.setFullName(context.getStringAttribute(getUserFullnameAttribute()));
      user.setPhone(context.getStringAttribute(getUserPhoneAttribute()));
      user.setMobilePhone(context.getStringAttribute(getUserMobilePhoneAttribute()));
      user.setEmail(context.getStringAttribute(getUserEmailAttribute()));
      user.setOrgLevel1(context.getStringAttribute(getUserOrgLevel1Attribute()));
      user.setOrgLevel2(context.getStringAttribute(getUserOrgLevel2Attribute()));
      user.setOrgLevel3(context.getStringAttribute(getUserOrgLevel3Attribute()));
      user.setOrgLevel4(context.getStringAttribute(getUserOrgLevel4Attribute()));

      return user;
    }
  }

  /** Context Mapper for user entries. */
  class UserContextMapper extends AbstractContextMapper<AccessIdRepresentationModel> {

    @Override
    public AccessIdRepresentationModel doMapFromContext(final DirContextOperations context) {
      final AccessIdRepresentationModel accessId = new AccessIdRepresentationModel();
      accessId.setAccessId(getUserIdFromContext(context));
      String firstName = context.getStringAttribute(getUserFirstnameAttribute());
      String lastName = context.getStringAttribute(getUserLastnameAttribute());
      accessId.setName(String.format("%s, %s", lastName, firstName));
      return accessId;
    }
  }

  /** General Context Mapper for DNs, which can be both, user or groups. */
  class DnContextMapper extends AbstractContextMapper<AccessIdRepresentationModel> {

    @Override
    public AccessIdRepresentationModel doMapFromContext(final DirContextOperations context) {
      final AccessIdRepresentationModel accessId = new AccessIdRepresentationModel();
      String[] objectClasses = context.getStringAttributes(getUserSearchFilterName());
      if (objectClasses != null
          && Arrays.asList(objectClasses).contains(getUserSearchFilterValue())) {
        accessId.setAccessId(getUserIdFromContext(context));
        String firstName = context.getStringAttribute(getUserFirstnameAttribute());
        String lastName = context.getStringAttribute(getUserLastnameAttribute());
        accessId.setName(String.format("%s, %s", lastName, firstName));
      } else if (getUserPermissionsAttribute().isEmpty() ||
          context.getStringAttribute(getUserPermissionsAttribute()) == null) {
        if (useDnForGroups()) {
          accessId.setAccessId(getDnFromContext(context)); // fully qualified dn
        } else {
          accessId.setAccessId(getGroupIdFromContext(context));
        }
        accessId.setName(context.getStringAttribute(getGroupNameAttribute()));
      } else {
        accessId.setAccessId(getPermissionIdFromContext(context));
        accessId.setName(context.getStringAttribute(getUserPermissionsAttribute()));
      }
      return accessId;
    }
  }

  class DnStringContextMapper extends AbstractContextMapper<String> {
    @Override
    public String doMapFromContext(DirContextOperations ctx) {
      return getDnFromContext(ctx);
    }
  }
}
