package pro.taskana.ldap;

import java.util.List;

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

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.rest.resource.AccessIdResource;

/**
 * Class for Ldap access.
 *
 * @author bbr
 */
@Component
public class LdapClient {

    public static final String TASKANA_USE_LDAP_PROP_NAME = "taskana.ldap.useLdap";
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapClient.class);
    private boolean active = false;

    @Autowired
    private Environment env;

    @Autowired(required = false)
    private LdapTemplate ldapTemplate;

    private String userSearchBase;
    private String userSearchFilterName;
    private String userSearchFilterValue;
    private String userFirstnameAttribute;
    private String userLastnameAttribute;
    private String userIdAttribute;
    private String groupSearchBase;
    private String groupSearchFilterName;
    private String groupSearchFilterValue;
    private String groupNameAttribute;
    private int minSearchForLength;
    private int maxNumberOfReturnedAccessIds;

    private static final String CN = "cn";
    private String message;

    @PostConstruct
    private void init() {

        String strMinSearchForLength = getMinSearchForLengthAsString();
        if (strMinSearchForLength == null || strMinSearchForLength.isEmpty()) {
            minSearchForLength = 3;
        } else {
            minSearchForLength = Integer.parseInt(strMinSearchForLength);
        }

        String strMaxNumberOfReturnedAccessIds = getMaxNumberOfReturnedAccessIdsAsString();
        if (strMaxNumberOfReturnedAccessIds == null || strMaxNumberOfReturnedAccessIds.isEmpty()) {
            maxNumberOfReturnedAccessIds = 50;
        } else {
            maxNumberOfReturnedAccessIds = Integer.parseInt(strMaxNumberOfReturnedAccessIds);
        }

        if (useLdap()) {
            userSearchBase = getUserSearchBase();
            userSearchFilterName = getUserSearchFilterName();
            userSearchFilterValue = getUserSearchFilterValue();
            userFirstnameAttribute = getUserFirstnameAttribute();
            userLastnameAttribute = getUserLastnameAttribute();
            userIdAttribute = getUserIdAttribute();
            groupSearchBase = getGroupSearchBase();
            groupSearchFilterName = getGroupSearchFilterName();
            groupSearchFilterValue = getGroupSearchFilterValue();
            groupNameAttribute = getGroupNameAttribute();

            ldapTemplate.setDefaultCountLimit(maxNumberOfReturnedAccessIds);

            final String emptyMessage = "taskana.ldap.useLdap is set to true, but";
            message = emptyMessage;
            if (userSearchBase == null) {
                message += " taskana.ldap.userSearchBase is not configured.";
            }
            if (userSearchFilterName == null) {
                message += " taskana.ldap.userSearchFilterName is not configured.";
            }
            if (userSearchFilterValue == null) {
                message += " taskana.ldap.userSearchFilterValue is not configured.";
            }
            if (userFirstnameAttribute == null) {
                message += " taskana.ldap.userFirstnameAttribute is not configured.";
            }
            if (userLastnameAttribute == null) {
                message += " taskana.ldap.userLastnameAttribute is not configured.";
            }
            if (userIdAttribute == null) {
                message += " taskana.ldap.userIdAttribute is not configured.";
            }
            if (groupSearchBase == null) {
                message += " taskana.ldap.groupSearchBase is not configured.";
            }
            if (groupSearchFilterName == null) {
                message += " taskana.ldap.groupSearchFilterName is not configured.";
            }
            if (groupSearchFilterValue == null) {
                message += " taskana.ldap.groupSearchFilterValue is not configured.";
            }
            if (groupNameAttribute == null) {
                message += " taskana.ldap.groupNameAttribute is not configured.";
            }
            if (!message.equals(emptyMessage)) {
                throw new SystemException(message);
            }
            active = true;
        }
    }

    public List<AccessIdResource> searchUsersAndGroups(final String name) throws InvalidArgumentException {
        LOGGER.debug("entry to searchUsersAndGroups(name = {})", name);
        if (!active) {
            throw new SystemException(
                "LdapClient was called but is not active due to missing configuration: " + message);
        }
        if (name == null || name.length() < minSearchForLength) {
            throw new InvalidArgumentException("searchFor string " + name + " is too short. Minimum Length = "
                + getMinSearchForLength());
        }

        List<AccessIdResource> users = searchUsersByName(name);
        users.addAll(searchGroupsByName(name));
        users.sort((AccessIdResource a, AccessIdResource b) -> {
            return a.getAccessId().compareToIgnoreCase(b.getAccessId());
        });

        List<AccessIdResource> result = users.subList(0, Math.min(users.size(), maxNumberOfReturnedAccessIds));
        LOGGER.debug("exit from searchUsersAndGroups(name = {}). Returning {} users and groups: {}", name, users.size(),
            LoggerUtils.listToString(result));

        return result;
    }

    public List<AccessIdResource> searchUsersByName(final String name) throws InvalidArgumentException {
        LOGGER.debug("entry to searchUsersByName(name = {}).", name);
        if (!active) {
            throw new SystemException(
                "LdapClient was called but is not active due to missing configuration: " + message);
        }
        if (name == null || name.length() < minSearchForLength) {
            throw new InvalidArgumentException("searchFor string " + name + " is too short. Minimum Length = "
                + getMinSearchForLength());
        }

        final AndFilter andFilter = new AndFilter();
        andFilter.and(new EqualsFilter(getUserSearchFilterName(), getUserSearchFilterValue()));
        final OrFilter orFilter = new OrFilter();

        orFilter.or(new WhitespaceWildcardsFilter(getUserFirstnameAttribute(), name));
        orFilter.or(new WhitespaceWildcardsFilter(getUserLastnameAttribute(), name));
        orFilter.or(new WhitespaceWildcardsFilter(getUserIdAttribute(), name));
        andFilter.and(orFilter);

        String[] userAttributesToReturn = {getUserFirstnameAttribute(), getUserLastnameAttribute(),
            getUserIdAttribute()};

        final List<AccessIdResource> accessIds = ldapTemplate.search(getUserSearchBase(), andFilter.encode(),
            SearchControls.SUBTREE_SCOPE, userAttributesToReturn, new UserContextMapper());
        LOGGER.debug("exit from searchUsersByName. Retrieved the following users: {}.",
            LoggerUtils.listToString(accessIds));
        return accessIds;

    }

    public List<AccessIdResource> searchGroupsByName(final String name) throws InvalidArgumentException {
        LOGGER.debug("entry to searchGroupsByName(name = {}).", name);
        if (!active) {
            throw new SystemException(
                "LdapClient was called but is not active due to missing configuration: " + message);
        }
        if (name == null || name.length() < minSearchForLength) {
            throw new InvalidArgumentException("searchFor string " + name + " is too short. Minimum Length = "
                + getMinSearchForLength());
        }

        final AndFilter andFilter = new AndFilter();
        andFilter.and(new EqualsFilter(getGroupSearchFilterName(), getGroupSearchFilterValue()));
        final OrFilter orFilter = new OrFilter();
        orFilter.or(new WhitespaceWildcardsFilter(getGroupNameAttribute(), name));
        if (!CN.equals(groupNameAttribute)) {
            orFilter.or(new WhitespaceWildcardsFilter(CN, name));
        }
        andFilter.and(orFilter);

        String[] groupAttributesToReturn;
        if (CN.equals(groupNameAttribute)) {
            groupAttributesToReturn = new String[] {CN};
        } else {
            groupAttributesToReturn = new String[] {getGroupNameAttribute(), CN};
        }

        final List<AccessIdResource> accessIds = ldapTemplate.search(getGroupSearchBase(), andFilter.encode(),
            SearchControls.SUBTREE_SCOPE, groupAttributesToReturn, new GroupContextMapper());
        LOGGER.debug("Exit from searchGroupsByName. Retrieved the following groups: {}",
            LoggerUtils.listToString(accessIds));
        return accessIds;

    }

    public boolean useLdap() {
        String useLdap = env.getProperty(TASKANA_USE_LDAP_PROP_NAME);
        if (useLdap == null || useLdap.isEmpty()) {
            return false;
        } else {
            return Boolean.parseBoolean(useLdap);
        }
    }

    public String getUserSearchBase() {
        return env.getProperty("taskana.ldap.userSearchBase");
    }

    public String getUserSearchFilterName() {
        return env.getProperty("taskana.ldap.userSearchFilterName");
    }

    public String getUserSearchFilterValue() {
        return env.getProperty("taskana.ldap.userSearchFilterValue");
    }

    public String getUserFirstnameAttribute() {
        return env.getProperty("taskana.ldap.userFirstnameAttribute");
    }

    public String getUserLastnameAttribute() {
        return env.getProperty("taskana.ldap.userLastnameAttribute");
    }

    public String getUserIdAttribute() {
        return env.getProperty("taskana.ldap.userIdAttribute");
    }

    public String getGroupSearchBase() {
        return env.getProperty("taskana.ldap.groupSearchBase");
    }

    public String getGroupSearchFilterName() {
        return env.getProperty("taskana.ldap.groupSearchFilterName");
    }

    public String getGroupSearchFilterValue() {
        return env.getProperty("taskana.ldap.groupSearchFilterValue");
    }

    public String getGroupNameAttribute() {
        return env.getProperty("taskana.ldap.groupNameAttribute");
    }

    public String getMinSearchForLengthAsString() {
        return env.getProperty("taskana.ldap.minSearchForLength");
    }

    public int getMinSearchForLength() {
        return minSearchForLength;
    }

    public String getMaxNumberOfReturnedAccessIdsAsString() {
        return env.getProperty("taskana.ldap.maxNumberOfReturnedAccessIds");
    }

    public int getMaxNumberOfReturnedAccessIds() {
        return maxNumberOfReturnedAccessIds;
    }

    /**
     * Context Mapper for user entries.
     */
    private class UserContextMapper extends AbstractContextMapper<AccessIdResource> {

        @Override
        public AccessIdResource doMapFromContext(final DirContextOperations context) {
            final AccessIdResource accessId = new AccessIdResource();
            accessId.setAccessId(context.getStringAttribute(getUserIdAttribute()));
            String firstName = context.getStringAttribute(getUserFirstnameAttribute());
            String lastName = context.getStringAttribute(getUserLastnameAttribute());
            accessId.setName(lastName + ", " + firstName);
            return accessId;
        }
    }

    /**
     * Context Mapper for user entries.
     */
    private class GroupContextMapper extends AbstractContextMapper<AccessIdResource> {

        @Override
        public AccessIdResource doMapFromContext(final DirContextOperations context) {
            final AccessIdResource accessId = new AccessIdResource();
            accessId.setAccessId(context.getNameInNamespace()); // fully qualified dn
            accessId.setName(context.getStringAttribute(getGroupNameAttribute()));
            return accessId;
        }
    }
}
