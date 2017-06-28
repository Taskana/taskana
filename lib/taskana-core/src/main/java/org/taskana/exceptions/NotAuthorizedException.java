package org.taskana.exceptions;

@SuppressWarnings("serial")
public class NotAuthorizedException extends Exception {

	public NotAuthorizedException(String msg) {
		super(msg);
	}

}
