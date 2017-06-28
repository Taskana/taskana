package org.taskana.exceptions;

@SuppressWarnings("serial")
public class WorkbasketNotFoundException extends NotFoundException {

	public WorkbasketNotFoundException(String id) {
		super("Workbasket with '" + id + "' not found");
	}
}
