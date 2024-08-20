package io.kadai.simplehistory.rest;

public class HistoryRestEndpoints {

  public static final String API_V1 = "/api/v1/";

  public static final String URL_HISTORY_EVENTS = API_V1 + "task-history-event";
  public static final String URL_HISTORY_EVENTS_ID = API_V1 + "task-history-event/{historyEventId}";

  private HistoryRestEndpoints() {}
}
