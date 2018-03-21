package pro.taskana.exceptions;

/**
 * This exception is thrown if a domain name is specified which is not found in the configuration.
 */
public class DomainNotFoundException extends NotFoundException {

    public DomainNotFoundException(String domain, String msg) {
        super(domain, msg);
    }

    private static final long serialVersionUID = 1L;

}
