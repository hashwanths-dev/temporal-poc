package com.example.temporal_poc.workflow;

import com.example.temporal_poc.activities.BankingActivities;
import com.example.temporal_poc.constants.Constants;
import com.example.temporal_poc.models.TransactionNode;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.List;

@WorkflowImpl(taskQueues = Constants.BANKING_TASK_QUEUE)
public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

    private final BankingActivities activities = Workflow.newActivityStub(
            BankingActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setMaximumAttempts(3)
                            .build())
                    .build());

    @Override
    public void execute(List<TransactionNode> transactions) {
        for (TransactionNode transaction : transactions) {
            try {
                activities.transfer(transaction.getFromAccount(), transaction.getToAccount(), transaction.getAmount() );
            } catch (ActivityFailure e) {
                rollbackTransaction(transaction);
                throw e;
            }
        }
    }

    private void rollbackTransaction(TransactionNode failed_transaction) {
        double amountToRefund = failed_transaction.getAmount();
        TransactionNode current = failed_transaction;

        while(current != null) {
            try {
                activities.reverse_transfer(current.getFromAccount(), current.getToAccount(), amountToRefund);
            } catch (Exception e) {
                Workflow.getLogger(this.getClass()).error("Unable to rollback transaction {}", current.getToAccount(), e);
            }

            current = current.getParent();
        }
    }
}
