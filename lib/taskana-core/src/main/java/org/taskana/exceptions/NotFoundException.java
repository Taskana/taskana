package org.taskana.exceptions;

@SuppressWarnings("serial")
public class NotFoundException extends Exception {

	public NotFoundException(String id) {
		super(id);
	}
	
}
