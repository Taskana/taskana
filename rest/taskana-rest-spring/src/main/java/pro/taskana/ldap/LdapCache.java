package pro.taskana.ldap;

import java.util.List;

import pro.taskana.rest.resource.AccessIdResource;

/**
 * This interface is used for caching Ldap data.
 *
 * @author bbr
 */
public interface LdapCache {

    /**
     * find access ids for users and groups that match specified search criteria.
     *
     * @param searchFor
     *            the search string. The search is performed over names and ids of users and groups.
     * @param maxNumerOfReturnedAccessIds
     *            the maximum number of results to return.
     * @return a List of access ids for users and group where the name or id contains the search string.
     */
    List<AccessIdResource> findMatchingAccessId(String searchFor, int maxNumerOfReturnedAccessIds);
}
