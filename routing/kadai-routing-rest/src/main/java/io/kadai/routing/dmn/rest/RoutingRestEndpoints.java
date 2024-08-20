package io.kadai.routing.dmn.rest;

public class RoutingRestEndpoints {

  public static final String API_V1 = "/api/v1/";

  public static final String URL_ROUTING_RULES = API_V1 + "routing-rules";
  public static final String URL_ROUTING_RULES_DEFAULT = URL_ROUTING_RULES + "/default";
  public static final String ROUTING_REST_ENABLED = URL_ROUTING_RULES + "/routing-rest-enabled";

  private RoutingRestEndpoints() {}
}
