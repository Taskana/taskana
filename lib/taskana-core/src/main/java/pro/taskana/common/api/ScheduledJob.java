package pro.taskana.common.api;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** This class holds all data that go into the Job table. */
@EqualsAndHashCode
@ToString
public class ScheduledJob {

  @Getter @Setter Map<String, String> arguments;
  @Getter @Setter private Integer jobId;
  @Getter @Setter private Integer priority;
  private Instant created;
  private Instant due;
  @Getter @Setter private State state;
  @Getter @Setter private String lockedBy;
  private Instant lockExpires;
  @Getter @Setter private String type;
  @Getter @Setter private int retryCount;

  public ScheduledJob() {
    created = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    state = State.READY;
    retryCount = 0;
  }

  public Instant getCreated() {
    return created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setCreated(Instant created) {
    this.created = created != null ? created.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public Instant getDue() {
    return due != null ? due.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setDue(Instant due) {
    this.due = due != null ? due.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public Instant getLockExpires() {
    return lockExpires != null ? lockExpires.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  public void setLockExpires(Instant lockExpires) {
    this.lockExpires = lockExpires != null ? lockExpires.truncatedTo(ChronoUnit.MILLIS) : null;
  }

  /** This enum tracks the state of a job. */
  public enum State {
    READY,
    FAILED
  }
}
