package pro.taskana.common.internal.security;

import static pro.taskana.TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds;
import static pro.taskana.common.internal.util.CheckedFunction.wrap;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.security.auth.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the context information about the current (calling) user. The context is gathered from
 * the JAAS subject.
 *
 * @author Holger Hagen
 */
public final class CurrentUserContext {

  private static final String GET_UNIQUE_SECURITY_NAME_METHOD = "getUniqueSecurityName";
  private static final String GET_CALLER_SUBJECT_METHOD = "getCallerSubject";
  private static final String WSSUBJECT_CLASSNAME = "com.ibm.websphere.security.auth.WSSubject";

  private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserContext.class);

  private static Boolean runningOnWebSphere = null;

  private CurrentUserContext() {}

  /**
   * Returns the userid of the current user.
   *
   * @return String the userid. null if there is no JAAS subject.
   */
  public static String getUserid() {
    if (runningOnWebSphere()) {
      return getUserIdFromWsSubject();
    } else {
      return getUserIdFromJaasSubject();
    }
  }

  public static List<String> getGroupIds() {
    Subject subject = Subject.getSubject(AccessController.getContext());
    LOGGER.trace("Subject of caller: {}", subject);
    if (subject != null) {
      Set<GroupPrincipal> groups = subject.getPrincipals(GroupPrincipal.class);
      LOGGER.trace("Public groups of caller: {}", groups);
      return groups.stream()
          .map(Principal::getName)
          .filter(Objects::nonNull)
          .map(CurrentUserContext::convertAccessId)
          .collect(Collectors.toList());
    }
    LOGGER.trace("No groupIds found in subject!");
    return Collections.emptyList();
  }

  public static List<String> getAccessIds() {
    List<String> accessIds = new ArrayList<>(getGroupIds());
    accessIds.add(getUserid());
    return accessIds;
  }

  /**
   * Returns the unique security name of the first public credentials found in the WSSubject as
   * userid.
   *
   * @return the userid of the caller. If the userid could not be obtained, null is returned.
   */
  private static String getUserIdFromWsSubject() {
    try {
      Class<?> wsSubjectClass = Class.forName(WSSUBJECT_CLASSNAME);
      Method getCallerSubjectMethod =
          wsSubjectClass.getMethod(GET_CALLER_SUBJECT_METHOD, (Class<?>[]) null);
      Subject callerSubject = (Subject) getCallerSubjectMethod.invoke(null, (Object[]) null);
      LOGGER.debug("Subject of caller: {}", callerSubject);
      if (callerSubject != null) {
        Set<Object> publicCredentials = callerSubject.getPublicCredentials();
        LOGGER.debug("Public credentials of caller: {}", publicCredentials);
        return publicCredentials.stream()
            .map(
                wrap(
                    credential ->
                        credential
                            .getClass()
                            .getMethod(GET_UNIQUE_SECURITY_NAME_METHOD, (Class<?>[]) null)
                            .invoke(credential, (Object[]) null)))
            .peek(
                o ->
                    LOGGER.debug(
                        "Returning the unique security name of first public credential: {}", o))
            .map(Object::toString)
            .map(CurrentUserContext::convertAccessId)
            .findFirst()
            .orElse(null);
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
        runningOnWebSphere = true;
      } catch (ClassNotFoundException e) {
        LOGGER.debug("No WSSubject detected. Using JAAS subject further on.");
        runningOnWebSphere = false;
      }
    }
    return runningOnWebSphere;
  }

  private static String getUserIdFromJaasSubject() {
    Subject subject = Subject.getSubject(AccessController.getContext());
    LOGGER.trace("Subject of caller: {}", subject);
    if (subject != null) {
      Set<Principal> principals = subject.getPrincipals();
      LOGGER.trace("Public principals of caller: {}", principals);
      return principals.stream()
          .filter(principal -> !(principal instanceof GroupPrincipal))
          .map(Principal::getName)
          .filter(Objects::nonNull)
          .map(CurrentUserContext::convertAccessId)
          .findFirst()
          .orElse(null);
    }
    LOGGER.trace("No userId found in subject!");
    return null;
  }

  private static String convertAccessId(String accessId) {
    String toReturn = accessId;
    if (shouldUseLowerCaseForAccessIds()) {
      toReturn = accessId.toLowerCase();
    }
    LOGGER.trace("Found AccessId '{}'. Returning AccessId '{}' ", accessId, toReturn);
    return toReturn;
  }
}
