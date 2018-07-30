package pro.taskana.transaction;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SpringTransactionProvider implements TaskanaTransactionProvider<Object> {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object executeInTransaction(
        TaskanaCallable<Object> action) {
        return action.call();
    }

}
