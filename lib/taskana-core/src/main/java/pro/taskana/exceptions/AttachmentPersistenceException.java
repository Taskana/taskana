package pro.taskana.exceptions;

/**
 * Thrown, when an attachment should be inserted to the DB, but it does already exist.<br>
 * This may happen when a not persisted attachment with ID will be added twice on a task. This can´t be happen it the
 * correct Task-Methods will be used instead the List ones.
 */
public class AttachmentPersistenceException extends TaskanaException {

    private static final long serialVersionUID = 123L;

    public AttachmentPersistenceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
