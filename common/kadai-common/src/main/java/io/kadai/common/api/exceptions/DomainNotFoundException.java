package io.kadai.common.api.exceptions;

import java.util.Map;

/** This exception is thrown when the specified domain is not found in the configuration. */
public class DomainNotFoundException extends KadaiException {

  public static final String ERROR_KEY = "DOMAIN_NOT_FOUND";
  private final String domain;

  public DomainNotFoundException(String domain) {
    super(
        String.format("Domain '%s' does not exist in the configuration", domain),
        ErrorCode.of(ERROR_KEY, Map.of("domain", ensureNullIsHandled(domain))));
    this.domain = domain;
  }

  public String getDomain() {
    return domain;
  }
}
