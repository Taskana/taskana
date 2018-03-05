package pro.taskana.exceptions;

/**
 * This exception will be thrown if a specific workbasket is not in the database.
 */
public class WorkbasketNotFoundException extends NotFoundException {

    private String key;
    private String domain;

    public WorkbasketNotFoundException(String id, String msg) {
        super(id, msg);
    }

    public WorkbasketNotFoundException(String key, String domain, String msg) {
        super(null, msg);
        this.key = key;
        this.domain = domain;
    }

    public String getKey() {
        return key;
    }

    public String getDomain() {
        return domain;
    }

    private static final long serialVersionUID = 1L;
}
