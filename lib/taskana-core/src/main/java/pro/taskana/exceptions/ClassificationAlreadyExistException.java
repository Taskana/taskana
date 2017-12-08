package pro.taskana.exceptions;

/**
 * This Exception will be thrown, when a classification does already exits,
 * but wanted to create with same ID+domain.
 */
public class ClassificationAlreadyExistException extends NotFoundException {

    private static final long serialVersionUID = 4716611657569005013L;

    public ClassificationAlreadyExistException(String classificationId) {
        super(classificationId);
    }
}
