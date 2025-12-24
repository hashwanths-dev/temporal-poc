package com.example.temporal_poc.workflow;

import com.example.temporal_poc.activities.BankingActivities;
import com.example.temporal_poc.constants.Constants;
import com.example.temporal_poc.models.*;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

@WorkflowImpl(taskQueues = Constants.BANKING_TASK_QUEUE)
public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

    private static final Logger log = Workflow.getLogger(MoneyTransferWorkflowImpl.class);

    private final BankingActivities activities = Workflow.newActivityStub(
            BankingActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setMaximumAttempts(3)
                            .setBackoffCoefficient(2.0)
                            .build())
                    .build());

    @Override
    public void execute(RootRequest request) {
        log.info("Starting Batch Workflow for Agent: {}", request.getAgentAccount());

        activities.transfer(
                request.getAgentAccount(),
                request.getPoolAccount(),
                request.getTotalAmount(),
                RailType.INTERNAL
        );

        List<Promise<Void>> branchPromises = new ArrayList<>();

        for (BranchRequest branch : request.getBranches()) {
            branchPromises.add(Async.procedure(() ->
                    processBranch(
                            request.getAgentAccount(),
                            request.getPoolAccount(),
                            branch)
            ));
        }

        Promise.allOf(branchPromises).get();

        log.info("Batch Workflow completed for Agent: {}", request.getAgentAccount());
    }

    private void processBranch(String agentAccount, String poolAccount, BranchRequest branch) {
        Saga saga = new Saga(new Saga.Options.Builder().setParallelCompensation(false).build());
        try {
            saga.addCompensation(
                    activities::compensate,
                    poolAccount,
                    agentAccount,
                    branch.getAmount(),
                    RailType.INTERNAL
            );

            activities.transfer(
                    poolAccount,
                    branch.getRailAccount(),
                    branch.getAmount(),
                    RailType.INTERNAL
            );

            saga.addCompensation(
                    activities::compensate,
                    branch.getRailAccount(),
                    poolAccount,
                    branch.getAmount(),
                    RailType.INTERNAL
            );

            processLeaves(branch, agentAccount, poolAccount);

        } catch (ActivityFailure e) {
            log.error("Branch level failure for {}. Reversing branch total.", branch.getType());
            saga.compensate();
        }
    }

    private void processLeaves(BranchRequest branch, String agentAccount, String poolAccount) {
        for (LeafRequest leaf : branch.getLeaves()) {
            try {
                activities.transfer(
                        branch.getRailAccount(),
                        leaf.getCustomerAccount(),
                        leaf.getAmount(),
                        branch.getType()
                );

            } catch (ActivityFailure e) {
                log.error("Customer Transaction failed for customer: {} . Reverting lineage", leaf.getCustomerAccount());
                activities.compensate(branch.getRailAccount(), poolAccount, leaf.getAmount(), RailType.INTERNAL);
                activities.compensate(poolAccount, agentAccount, leaf.getAmount(), RailType.INTERNAL);
            }
        }
    }
}
