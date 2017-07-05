package org.taskana.exceptions;

/**
 * This exception will be thrown if a specific object is not in the database.
 */
@SuppressWarnings("serial")
public class NotFoundException extends Exception {

    public NotFoundException(String id) {
        super(id);
    }
}
