package pro.taskana.rest;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pro.taskana.BulkOperationResults;
import pro.taskana.TaskanaCallable;
import pro.taskana.TaskanaTransactionProvider;

@Component
public class SpringTransactionProvider implements TaskanaTransactionProvider<BulkOperationResults<String, Exception>> {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BulkOperationResults<String, Exception> executeInTransaction(
        TaskanaCallable<BulkOperationResults<String, Exception>> action) {
        return action.call();
    }

}
