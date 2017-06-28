package org.taskana.exceptions;

@SuppressWarnings("serial")
public class TaskNotFoundException extends NotFoundException {

    public TaskNotFoundException(String id) {
        super("Task '" + id + "' not found");
    }
}
