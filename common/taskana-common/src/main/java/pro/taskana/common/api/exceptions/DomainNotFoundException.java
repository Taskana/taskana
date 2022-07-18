package pro.taskana.common.api.exceptions;

import pro.taskana.common.internal.util.MapCreator;

/**
 * The DomainNotFoundException is thrown when the specified domain is not found in the
 * configuration.
 */
public class DomainNotFoundException extends NotFoundException {

  public static final String ERROR_KEY = "DOMAIN_NOT_FOUND";
  private final String domain;

  public DomainNotFoundException(String domain) {
    super(
        String.format("Domain '%s' does not exist in the configuration", domain),
        ErrorCode.of(ERROR_KEY, MapCreator.of("domain", domain)));
    this.domain = domain;
  }

  public String getDomain() {
    return domain;
  }
}
