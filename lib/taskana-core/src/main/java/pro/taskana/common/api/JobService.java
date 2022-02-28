package pro.taskana.common.api;

import pro.taskana.common.internal.jobs.TaskanaJob;

/** Service to manage the {@linkplain TaskanaJob TaskanaJobs}. */
public interface JobService {

  /**
   * Initializes the given {@linkplain ScheduledJob} and inserts it into the database.
   *
   * @param job the {@linkplain ScheduledJob job} to be created
   * @return the created {@linkplain ScheduledJob job}
   */
  ScheduledJob createJob(ScheduledJob job);
}
