package pro.taskana;

import pro.taskana.jobs.ScheduledJob;

/**
 * Service to manage the TASKANA jobs.
 */
public interface JobService {

    /**
     * Create a schedule a new job.
     */
    ScheduledJob createJob(ScheduledJob job);

}
