package org.taskana.exceptions;

/**
 * This exception will be thrown if a specific task is not in the database.
 */
@SuppressWarnings("serial")
public class TaskNotFoundException extends NotFoundException {

    public TaskNotFoundException(String id) {
        super("Task '" + id + "' not found");
    }
}
