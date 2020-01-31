package pro.taskana.common.api;

import pro.taskana.common.internal.jobs.ScheduledJob;

/** Service to manage the TASKANA jobs. */
public interface JobService {

  /**
   * Create a schedule a new job.
   *
   * @param job {@link ScheduledJob} The job to be created.
   * @return {@link ScheduledJob} The created job.
   */
  ScheduledJob createJob(ScheduledJob job);
}
