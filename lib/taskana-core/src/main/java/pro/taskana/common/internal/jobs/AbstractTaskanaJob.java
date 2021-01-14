package pro.taskana.common.internal.jobs;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;

/** Abstract base for all background jobs of TASKANA. */
public abstract class AbstractTaskanaJob implements TaskanaJob {

  protected final Duration runEvery;
  protected TaskanaEngineImpl taskanaEngineImpl;
  protected TaskanaTransactionProvider<Object> txProvider;
  protected ScheduledJob scheduledJob;

  public AbstractTaskanaJob(
      TaskanaEngine taskanaEngine,
      TaskanaTransactionProvider<Object> txProvider,
      ScheduledJob job) {
    this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
    this.txProvider = txProvider;
    this.scheduledJob = job;
    this.runEvery = taskanaEngineImpl.getConfiguration().getCleanupJobRunEvery();
  }

  public static TaskanaJob createFromScheduledJob(
      TaskanaEngine engine, TaskanaTransactionProvider<Object> txProvider, ScheduledJob job)
      throws ClassNotFoundException, IllegalAccessException, InstantiationException,
          InvocationTargetException {

    return (TaskanaJob)
        Thread.currentThread()
            .getContextClassLoader()
            .loadClass(job.getType().getClazz())
            .getConstructors()[0]
            .newInstance(engine, txProvider, job);
  }

  protected <T> List<List<T>> partition(Collection<T> members, int maxSize) {
    List<List<T>> result = new ArrayList<>();
    List<T> internal = new ArrayList<>();
    for (T member : members) {
      internal.add(member);
      if (internal.size() == maxSize) {
        result.add(internal);
        internal = new ArrayList<>();
      }
    }
    if (!internal.isEmpty()) {
      result.add(internal);
    }
    return result;
  }

  protected Instant getNextDueForCleanupJob() {
    Instant nextRun = Instant.now();
    if (scheduledJob != null && scheduledJob.getDue() != null) {
      nextRun = scheduledJob.getDue();
    }

    while (nextRun.isBefore(Instant.now())) {
      nextRun = nextRun.plus(runEvery);
    }

    return nextRun;
  }
}
