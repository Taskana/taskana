package pro.taskana.common.rest.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.naming.directory.SearchControls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.rest.models.AccessIdRepresentationModel;

/**
 * Class for Ldap access.
 *
 * @author bbr
 */
@Component
public class LdapClient {

  static final String MISSING_CONFIGURATION_S =
      "LdapClient was called but is not active due to missing configuration: %s ";

  private static final Logger LOGGER = LoggerFactory.getLogger(LdapClient.class);

  private static final String CN = "cn";

  private boolean active = false;

  @Autowired private Environment env;

  @Autowired(required = false)
  private LdapTemplate ldapTemplate;

  private int minSearchForLength;

  private int maxNumberOfReturnedAccessIds;

  private String message;

  /**
   * Search LDAP for matching users or groups.
   *
   * @param name lookup string for names or groups
   * @return a list of AccessIdResources sorted by AccessId and limited to
   *     maxNumberOfReturnedAccessIds
   * @throws InvalidArgumentException if input is shorter than minSearchForLength
   */
  public List<AccessIdRepresentationModel> searchUsersAndGroups(final String name)
      throws InvalidArgumentException {
    LOGGER.debug("entry to searchUsersAndGroups(name = {})", name);
    isInitOrFail();
    testMinSearchForLength(name);

    List<AccessIdRepresentationModel> accessIds = new ArrayList<>();
    if (nameIsDn(name)) {
      AccessIdRepresentationModel groupByDn = searchGroupByDn(name);
      if (groupByDn != null) {
        accessIds.add(groupByDn);
      }
    } else {
      accessIds.addAll(searchUsersByNameOrAccessId(name));
      accessIds.addAll(searchGroupsByName(name));
    }
    sortListOfAccessIdResources(accessIds);
    List<AccessIdRepresentationModel> result = getFirstPageOfaResultList(accessIds);

    LOGGER.debug(
        "exit from searchUsersAndGroups(name = {}). Returning {} users and groups: {}",
        name,
        accessIds.size(),
        result);

    return result;
  }

  public List<AccessIdRepresentationModel> searchUsersByNameOrAccessId(final String name)
      throws InvalidArgumentException {
    LOGGER.debug("entry to searchUsersByNameOrAccessId(name = {}).", name);
    isInitOrFail();
    testMinSearchForLength(name);

    final AndFilter andFilter = new AndFilter();
    andFilter.and(new EqualsFilter(getUserSearchFilterName(), getUserSearchFilterValue()));
    final OrFilter orFilter = new OrFilter();

    orFilter.or(new WhitespaceWildcardsFilter(getUserFirstnameAttribute(), name));
    orFilter.or(new WhitespaceWildcardsFilter(getUserLastnameAttribute(), name));
    orFilter.or(new WhitespaceWildcardsFilter(getUserIdAttribute(), name));
    andFilter.and(orFilter);

    String[] userAttributesToReturn = {
      getUserFirstnameAttribute(), getUserLastnameAttribute(), getUserIdAttribute()
    };

    final List<AccessIdRepresentationModel> accessIds =
        ldapTemplate.search(
            getUserSearchBase(),
            andFilter.encode(),
            SearchControls.SUBTREE_SCOPE,
            userAttributesToReturn,
            new UserContextMapper());
    LOGGER.debug(
        "exit from searchUsersByNameOrAccessId. Retrieved the following users: {}.", accessIds);
    return accessIds;
  }

  public List<AccessIdRepresentationModel> getUsersByAccessId(final String accessId) {
    LOGGER.debug("entry to searchUsersByAccessId(name = {}).", accessId);
    isInitOrFail();

    final AndFilter andFilter = new AndFilter();
    andFilter.and(new EqualsFilter(getUserSearchFilterName(), getUserSearchFilterValue()));
    andFilter.and(new EqualsFilter(getUserIdAttribute(), accessId));

    String[] userAttributesToReturn = {
      getUserFirstnameAttribute(), getUserLastnameAttribute(), getUserIdAttribute()
    };

    final List<AccessIdRepresentationModel> accessIds =
        ldapTemplate.search(
            getUserSearchBase(),
            andFilter.encode(),
            SearchControls.SUBTREE_SCOPE,
            userAttributesToReturn,
            new UserContextMapper());
    LOGGER.debug("exit from searchUsersByAccessId. Retrieved the following users: {}.", accessIds);
    return accessIds;
  }

  public List<AccessIdRepresentationModel> searchGroupsByName(final String name)
      throws InvalidArgumentException {
    LOGGER.debug("entry to searchGroupsByName(name = {}).", name);
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

    final List<AccessIdRepresentationModel> accessIds =
        ldapTemplate.search(
            getGroupSearchBase(),
            andFilter.encode(),
            SearchControls.SUBTREE_SCOPE,
            getLookUpGoupAttributesToReturn(),
            new GroupContextMapper());
    LOGGER.debug("Exit from searchGroupsByName. Retrieved the following groups: {}", accessIds);
    return accessIds;
  }

  public AccessIdRepresentationModel searchGroupByDn(final String name) {
    LOGGER.debug("entry to searchGroupByDn(name = {}).", name);
    isInitOrFail();
    // Obviously Spring LdapTemplate does have a inconsistency and always adds the base name to the
    // given DN.
    // https://stackoverflow.com/questions/55285743/spring-ldaptemplate-how-to-lookup-fully-qualified-dn-with-configured-base-dn
    // Therefore we have to remove the base name from the dn before performing the lookup
    String nameWithoutBaseDn = getNameWithoutBaseDn(name);
    LOGGER.debug(
        "Removed baseDN {} from given DN. New DN to be used: {}", getBaseDn(), nameWithoutBaseDn);
    final AccessIdRepresentationModel accessId =
        ldapTemplate.lookup(
            nameWithoutBaseDn, getLookUpGoupAttributesToReturn(), new GroupContextMapper());
    LOGGER.debug("Exit from searchGroupByDn. Retrieved the following group: {}", accessId);
    return accessId;
  }

  public List<AccessIdRepresentationModel> searchGroupsofUsersIsMember(final String name)
      throws InvalidArgumentException {
    LOGGER.debug("entry to searchGroupsofUsersIsMember(name = {}).", name);
    isInitOrFail();
    testMinSearchForLength(name);

    final AndFilter andFilter = new AndFilter();
    andFilter.and(new WhitespaceWildcardsFilter(getGroupNameAttribute(), ""));
    andFilter.and(new EqualsFilter(getGroupsOfUser(), name));

    String[] userAttributesToReturn = {getUserIdAttribute(), getGroupNameAttribute()};

    final List<AccessIdRepresentationModel> accessIds =
        ldapTemplate.search(
            getGroupSearchBase(),
            andFilter.encode(),
            SearchControls.SUBTREE_SCOPE,
            userAttributesToReturn,
            new GroupContextMapper());
    LOGGER.debug(
        "exit from searchGroupsofUsersIsMember. Retrieved the following users: {}.", accessIds);
    return accessIds;
  }

  public String getUserSearchBase() {
    return LdapSettings.TASKANA_LDAP_USER_SEARCH_BASE.getValueFromEnv(env);
  }

  public String getUserSearchFilterName() {
    return LdapSettings.TASKANA_LDAP_USER_SEARCH_FILTER_NAME.getValueFromEnv(env);
  }

  public String getUserSearchFilterValue() {
    return LdapSettings.TASKANA_LDAP_USER_SEARCH_FILTER_VALUE.getValueFromEnv(env);
  }

  public String getUserFirstnameAttribute() {
    return LdapSettings.TASKANA_LDAP_USER_FIRSTNAME_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getUserLastnameAttribute() {
    return LdapSettings.TASKANA_LDAP_USER_LASTNAME_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getUserIdAttribute() {
    return LdapSettings.TASKANA_LDAP_USER_ID_ATTRIBUTE.getValueFromEnv(env);
  }

  public String getGroupSearchBase() {
    return LdapSettings.TASKANA_LDAP_GROUP_SEARCH_BASE.getValueFromEnv(env);
  }

  public String getBaseDn() {
    return LdapSettings.TASKANA_LDAP_BASE_DN.getValueFromEnv(env);
  }

  public String getGroupSearchFilterName() {
    return LdapSettings.TASKANA_LDAP_GROUP_SEARCH_FILTER_NAME.getValueFromEnv(env);
  }

  public String getGroupSearchFilterValue() {
    return LdapSettings.TASKANA_LDAP_GROUP_SEARCH_FILTER_VALUE.getValueFromEnv(env);
  }

  public String getGroupNameAttribute() {
    return LdapSettings.TASKANA_LDAP_GROUP_NAME_ATTRIBUTE.getValueFromEnv(env);
  }

  public int calcMinSearchForLength(int defaultValue) {
    String envValue = LdapSettings.TASKANA_LDAP_MIN_SEARCH_FOR_LENGTH.getValueFromEnv(env);
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
        LdapSettings.TASKANA_LDAP_MAX_NUMBER_OF_RETURNED_ACCESS_IDS.getValueFromEnv(env);
    if (envValue == null || envValue.isEmpty()) {
      return defaultValue;
    }
    return Integer.parseInt(envValue);
  }

  public int getMaxNumberOfReturnedAccessIds() {
    return maxNumberOfReturnedAccessIds;
  }

  public String getGroupsOfUser() {
    return LdapSettings.TASKANA_LDAP_GROUPS_OF_USER.getValueFromEnv(env);
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
      throw new SystemException(String.format(MISSING_CONFIGURATION_S, message));
    }
  }

  void sortListOfAccessIdResources(List<AccessIdRepresentationModel> accessIds) {
    accessIds.sort(
        Comparator.comparing(
            AccessIdRepresentationModel::getAccessId, String.CASE_INSENSITIVE_ORDER));
  }

  String getNameWithoutBaseDn(String name) {
    // (?i) --> case insensitive replacement
    return name.replaceAll("(?i)" + Pattern.quote("," + getBaseDn()), "");
  }

  String[] getLookUpGoupAttributesToReturn() {
    if (CN.equals(getGroupNameAttribute())) {
      return new String[] {CN};
    }
    return new String[] {getGroupNameAttribute(), CN};
  }

  @PostConstruct
  void init() {
    LOGGER.debug("Entry to init()");
    minSearchForLength = calcMinSearchForLength(3);
    maxNumberOfReturnedAccessIds = calcMaxNumberOfReturnedAccessIds(50);

    ldapTemplate.setDefaultCountLimit(maxNumberOfReturnedAccessIds);

    final List<LdapSettings> missingConfigurations = checkForMissingConfigurations();

    if (!missingConfigurations.isEmpty()) {
      message = String.format("LDAP configurations are missing: %s", missingConfigurations);
      throw new SystemException(message);
    }
    active = true;

    LOGGER.debug("Exit from init()");
  }

  List<LdapSettings> checkForMissingConfigurations() {
    return Arrays.stream(LdapSettings.values())
        // optional settings
        .filter(p -> !p.equals(LdapSettings.TASKANA_LDAP_MAX_NUMBER_OF_RETURNED_ACCESS_IDS))
        .filter(p -> !p.equals(LdapSettings.TASKANA_LDAP_MIN_SEARCH_FOR_LENGTH))
        .filter(p -> Objects.isNull(p.getValueFromEnv(env)))
        .collect(Collectors.toList());
  }

  void testMinSearchForLength(final String name) throws InvalidArgumentException {
    if (name == null || name.length() < minSearchForLength) {
      throw new InvalidArgumentException(
          String.format(
              "search for string %s is too short. Minimum Length is %s",
              name, getMinSearchForLength()));
    }
  }

  String getDnWithBaseDn(final String givenDn) {
    String dn = givenDn;
    if (!dn.toLowerCase().endsWith(getBaseDn().toLowerCase())) {
      dn = dn + "," + getBaseDn();
    }
    return dn;
  }

  /** Context Mapper for user entries. */
  class GroupContextMapper extends AbstractContextMapper<AccessIdRepresentationModel> {

    @Override
    public AccessIdRepresentationModel doMapFromContext(final DirContextOperations context) {
      final AccessIdRepresentationModel accessId = new AccessIdRepresentationModel();
      String dn = getDnWithBaseDn(context.getDn().toString());
      accessId.setAccessId(dn); // fully qualified dn
      accessId.setName(context.getStringAttribute(getGroupNameAttribute()));
      return accessId;
    }
  }

  /** Context Mapper for user entries. */
  class UserContextMapper extends AbstractContextMapper<AccessIdRepresentationModel> {

    @Override
    public AccessIdRepresentationModel doMapFromContext(final DirContextOperations context) {
      final AccessIdRepresentationModel accessId = new AccessIdRepresentationModel();
      accessId.setAccessId(context.getStringAttribute(getUserIdAttribute()));
      String firstName = context.getStringAttribute(getUserFirstnameAttribute());
      String lastName = context.getStringAttribute(getUserLastnameAttribute());
      accessId.setName(String.format("%s, %s", lastName, firstName));
      return accessId;
    }
  }
}
