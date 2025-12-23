package com.example.temporal_poc.controller;

import com.example.temporal_poc.models.TransactionNode;
import com.example.temporal_poc.workflow.MoneyTransferWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banking")
public class BankingController {
    private final WorkflowClient workflowClient;

    public BankingController(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @PostMapping("/start")
    public String start() {
        String transactionId = "TX-2";
        MoneyTransferWorkflow workflow = workflowClient.newWorkflowStub(
                MoneyTransferWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("BankingTaskQueue")
                        .setWorkflowId("transfer-"+transactionId)
                        .setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_REJECT_DUPLICATE)
                        .build()
        );

        List<TransactionNode> transactions = buildTransactionTree();

        WorkflowClient.start(workflow::execute, transactions);

        return "Transaction flow started in Temporal";
    }

    private List<TransactionNode> buildTransactionTree() {
        TransactionNode t1 = new TransactionNode("Ag1", "GL1", 3000, null);
        TransactionNode t3 = new TransactionNode("GL1", "GL-IFT", 1000, t1);
        TransactionNode t7 = new TransactionNode("GL-IFT", "C3", 1000, t3);
        return List.of(t1, t3, t7);
    }
}
