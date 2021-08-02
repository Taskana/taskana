package pro.taskana.common.internal.transaction;

import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** TODO. */
@Component
public class SpringTransactionProvider implements TaskanaTransactionProvider {

  @Override
  @Transactional(rollbackFor = Exception.class)
  public <T> T executeInTransaction(Supplier<T> supplier) {
    return supplier.get();
  }
}
