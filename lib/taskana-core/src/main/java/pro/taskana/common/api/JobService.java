package pro.taskana.common.api;

/** Service to manage the Taskana Jobs. */
public interface JobService {

  /**
   * Creates a new {@linkplain ScheduledJob}.
   *
   * @param job the Job to be created
   * @return the created Job
   */
  ScheduledJob createJob(ScheduledJob job);
}
