package org.taskana.security;

import java.security.AccessController;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the context information about the current (calling) user. The
 * context is gathered from the JAAS subject.
 * 
 * @author Holger Hagen
 *
 */
public class CurrentUserContext {

	private static final Logger logger = LoggerFactory.getLogger(CurrentUserContext.class);

	/**
	 * Returns the userid of the current user.
	 * 
	 * @return String the userid. null if there is no JAAS subject.
	 */
	public static String getUserid() {
		Subject subject = Subject.getSubject(AccessController.getContext());
		logger.debug("Subject of caller: {}", subject);
		if (subject != null) {
			Set<Object> publicCredentials = subject.getPublicCredentials();
			logger.debug("Public credentials of caller: {}", publicCredentials);
			for (Object pC : publicCredentials) {
				logger.debug("Returning the first public credential: {}", pC.toString());
				return pC.toString();
			}
		}
		logger.debug("No userid found in subject!");
		return null;
	}

	public static List<String> getGroupIds() {
		return null;
	}

}
