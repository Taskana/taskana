package pro.taskana.common.api.exceptions;

/**
 * This exception is thrown if a domain name is specified which is not found in the configuration.
 */
public class DomainNotFoundException extends NotFoundException {

  public DomainNotFoundException(String domain, String msg) {
    super(domain, msg);
  }
}
