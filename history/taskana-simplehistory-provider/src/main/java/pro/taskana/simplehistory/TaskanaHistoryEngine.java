package pro.taskana.simplehistory;

import pro.taskana.spi.history.api.TaskanaHistory;

/** The TaskanaHistoryEngine represents an overall set of all needed services. */
public interface TaskanaHistoryEngine {
  /**
   * The TaskanaHistory can be used for operations on all history events.
   *
   * @return the HistoryService
   */
  TaskanaHistory getTaskanaHistoryService();
}
