package pro.taskana.exceptions;

import pro.taskana.security.CurrentUserContext;

/**
 * This exception is used to communicate a not authorized user.
 */
public class NotAuthorizedException extends TaskanaException {

    public NotAuthorizedException(String msg) {
        super(msg + " - [CURRENT USER: {'" + CurrentUserContext.getUserid() + "'}]");
    }

    private static final long serialVersionUID = 21235L;
}
