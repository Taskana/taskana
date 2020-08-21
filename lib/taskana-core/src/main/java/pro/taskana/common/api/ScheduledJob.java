package pro.taskana.common.api;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import pro.taskana.classification.internal.jobs.ClassificationChangedJob;
import pro.taskana.task.internal.jobs.TaskCleanupJob;
import pro.taskana.task.internal.jobs.TaskRefreshJob;
import pro.taskana.workbasket.internal.jobs.WorkbasketCleanupJob;

/**
 * This class holds all data that go into the Job table.
 *
 * @author bbr
 */
public class ScheduledJob {

  Map<String, String> arguments;
  private Integer jobId;
  private Integer priority;
  private Instant created;
  private Instant due;
  private State state;
  private String lockedBy;
  private Instant lockExpires;
  private Type type;
  private int retryCount;

  public ScheduledJob() {
    created = Instant.now();
    state = State.READY;
    retryCount = 0;
  }

  public Integer getJobId() {
    return jobId;
  }

  public void setJobId(Integer jobId) {
    this.jobId = jobId;
  }

  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  public Instant getDue() {
    return due;
  }

  public void setDue(Instant due) {
    this.due = due;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public String getLockedBy() {
    return lockedBy;
  }

  public void setLockedBy(String lockedBy) {
    this.lockedBy = lockedBy;
  }

  public Instant getLockExpires() {
    return lockExpires;
  }

  public void setLockExpires(Instant lockExpires) {
    this.lockExpires = lockExpires;
  }

  public Map<String, String> getArguments() {
    return arguments;
  }

  public void setArguments(Map<String, String> arguments) {
    this.arguments = arguments;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public int getRetryCount() {
    return retryCount;
  }

  public void setRetryCount(int retryCount) {
    this.retryCount = retryCount;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        arguments, created, due, jobId, lockExpires, lockedBy, priority, retryCount, state, type);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ScheduledJob)) {
      return false;
    }
    ScheduledJob other = (ScheduledJob) obj;
    return Objects.equals(arguments, other.arguments)
        && Objects.equals(created, other.created)
        && Objects.equals(due, other.due)
        && Objects.equals(jobId, other.jobId)
        && Objects.equals(lockExpires, other.lockExpires)
        && Objects.equals(lockedBy, other.lockedBy)
        && Objects.equals(priority, other.priority)
        && retryCount == other.retryCount
        && state == other.state
        && type == other.type;
  }

  @Override
  public String toString() {
    return "ScheduledJob [jobId="
        + jobId
        + ", priority="
        + priority
        + ", created="
        + created
        + ", due="
        + due
        + ", state="
        + state
        + ", lockedBy="
        + lockedBy
        + ", lockExpires="
        + lockExpires
        + ", type="
        + type
        + ", retryCount="
        + retryCount
        + ", arguments="
        + arguments
        + "]";
  }

  /**
   * This enum tracks the state of a job.
   *
   * @author bbr
   */
  public enum State {
    READY,
    FAILED
  }

  /** This enum controls the type of a job. */
  public enum Type {
    CLASSIFICATIONCHANGEDJOB(ClassificationChangedJob.class.getName()),
    UPDATETASKSJOB(TaskRefreshJob.class.getName()),
    TASKCLEANUPJOB(TaskCleanupJob.class.getName()),
    WORKBASKETCLEANUPJOB(WorkbasketCleanupJob.class.getName()),
    HISTORYCLEANUPJOB("pro.taskana.simplehistory.impl.jobs.HistoryCleanupJob");

    private String clazz;

    Type(String clazz) {
      this.clazz = clazz;
    }

    public String getClazz() {
      return clazz;
    }
  }
}
