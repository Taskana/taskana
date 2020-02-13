package pro.taskana.common.internal.transaction;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pro.taskana.common.internal.transaction.TaskanaCallable;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;

/** TODO. */
@Component
public class SpringTransactionProvider implements TaskanaTransactionProvider<Object> {

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Object executeInTransaction(TaskanaCallable<Object> action) {
    return action.call();
  }
}
