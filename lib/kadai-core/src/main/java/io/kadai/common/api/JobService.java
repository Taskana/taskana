package io.kadai.common.api;

import io.kadai.common.internal.jobs.KadaiJob;

/** Service to manage the {@linkplain KadaiJob KadaiJobs}. */
public interface JobService {

  /**
   * Initializes the given {@linkplain ScheduledJob} and inserts it into the database.
   *
   * @param job the {@linkplain ScheduledJob job} to be created
   * @return the created {@linkplain ScheduledJob job}
   */
  ScheduledJob createJob(ScheduledJob job);
}
