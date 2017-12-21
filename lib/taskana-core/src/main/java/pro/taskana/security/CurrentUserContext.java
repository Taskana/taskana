package pro.taskana.security;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.configuration.TaskanaEngineConfiguration;

/**
 * Provides the context information about the current (calling) user. The context is gathered from the JAAS subject.
 *
 * @author Holger Hagen
 */
public final class CurrentUserContext {

    private static final String GET_UNIQUE_SECURITY_NAME_METHOD = "getUniqueSecurityName";
    private static final String GET_CALLER_SUBJECT_METHOD = "getCallerSubject";
    private static final String WSSUBJECT_CLASSNAME = "com.ibm.websphere.security.auth.WSSubject";

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserContext.class);

    private static Boolean runningOnWebSphere = null;

    private CurrentUserContext() {
    }

    /**
     * Returns the userid of the current user.
     *
     * @return String the userid. null if there is no JAAS subject.
     */
    public static String getUserid() {
        if (runningOnWebSphere()) {
            return getUseridFromWSSubject();
        } else {
            return getUseridFromJAASSubject();
        }
    }

    /**
     * Returns the unique security name of the first public credentials found in the WSSubject as userid.
     *
     * @return the userid of the caller. If the userid could not be obtained, null is returned.
     */
    private static String getUseridFromWSSubject() {
        try {
            Class<?> wsSubjectClass = Class.forName(WSSUBJECT_CLASSNAME);
            Method getCallerSubjectMethod = wsSubjectClass.getMethod(GET_CALLER_SUBJECT_METHOD, (Class<?>[]) null);
            Subject callerSubject = (Subject) getCallerSubjectMethod.invoke(null, (Object[]) null);
            LOGGER.debug("Subject of caller: {}", callerSubject);
            if (callerSubject != null) {
                Set<Object> publicCredentials = callerSubject.getPublicCredentials();
                LOGGER.debug("Public credentials of caller: {}", publicCredentials);
                for (Object pC : publicCredentials) {
                    Object o = pC.getClass().getMethod(GET_UNIQUE_SECURITY_NAME_METHOD, (Class<?>[]) null).invoke(pC,
                        (Object[]) null);
                    LOGGER.debug("Returning the unique security name of first public credential: {}", o);
                    String userIdFound = o.toString();
                    String userIdUsed = userIdFound;
                    if (TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds() && userIdFound != null) {
                        userIdUsed = userIdFound.toLowerCase();
                    }
                    LOGGER.trace("Found User id {}. Returning User id {} ", userIdFound, userIdUsed);
                    return userIdUsed;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not get user from WSSubject. Going ahead unauthorized.");
        }
        return null;
    }

    /**
     * Checks, whether Taskana is running on IBM WebSphere.
     *
     * @return true, if it is running on IBM WebSphere
     */
    private static boolean runningOnWebSphere() {
        if (runningOnWebSphere == null) {
            try {
                Class.forName(WSSUBJECT_CLASSNAME);
                LOGGER.debug("WSSubject detected. Assuming that Taskana runs on IBM WebSphere.");
                runningOnWebSphere = new Boolean(true);
            } catch (ClassNotFoundException e) {
                LOGGER.debug("No WSSubject detected. Using JAAS subject further on.");
                runningOnWebSphere = new Boolean(false);
            }
        }
        return runningOnWebSphere;
    }

    private static String getUseridFromJAASSubject() {
        Subject subject = Subject.getSubject(AccessController.getContext());
        LOGGER.trace("Subject of caller: {}", subject);
        if (subject != null) {
            Set<Principal> principals = subject.getPrincipals();
            LOGGER.trace("Public principals of caller: {}", principals);
            for (Principal pC : principals) {
                if (!(pC instanceof Group)) {
                    String userIdFound = pC.getName();
                    String userIdUsed = userIdFound;
                    if (TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds() && userIdFound != null) {
                        userIdUsed = userIdFound.toLowerCase();
                    }
                    LOGGER.trace("Found User id {}. Returning User id {} ", userIdFound, userIdUsed);
                    return userIdUsed;
                }
            }
        }
        LOGGER.trace("No userid found in subject!");
        return null;
    }

    public static List<String> getGroupIds() {
        Subject subject = Subject.getSubject(AccessController.getContext());
        LOGGER.trace("Subject of caller: {}", subject);
        List<String> groupIds = new ArrayList<>();
        if (subject != null) {
            Set<Group> groups = subject.getPrincipals(Group.class);
            LOGGER.trace("Public groups of caller: {}", groups);
            for (Principal group : groups) {
                String groupNameFound = group.getName();
                String groupNameReturned = groupNameFound;
                if (TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds() && groupNameFound != null) {
                    groupNameReturned = groupNameFound.toLowerCase();
                }
                LOGGER.trace("Found group id {}. Returning group Id: {}", groupNameFound, groupNameReturned);
                groupIds.add(groupNameReturned);
            }
            return groupIds;
        }
        LOGGER.trace("No groupids found in subject!");
        return groupIds;
    }

    public static List<String> getAccessIds() {
        List<String> accessIds = new ArrayList<>();
        List<String> groupIds = getGroupIds();
        accessIds.add(getUserid());
        if (!groupIds.isEmpty()) {
            accessIds.addAll(groupIds);
        }
        return accessIds;
    }
}
