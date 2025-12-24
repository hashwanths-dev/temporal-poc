package com.example.temporal_poc.controller;

import com.example.temporal_poc.constants.Constants;
import com.example.temporal_poc.models.*;
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
                        .setTaskQueue(Constants.BANKING_TASK_QUEUE)
                        .setWorkflowId("transfer-"+transactionId)
                        .setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_REJECT_DUPLICATE)
                        .build()
        );

        RootRequest rootRequest = buildRequest();

        WorkflowClient.start(workflow::execute, rootRequest);

        return "Transaction flow started in Temporal";
    }

    private RootRequest buildRequest() {
        LeafRequest leafRequest = new LeafRequest("C1", 1000, "123");
        LeafRequest leafRequest2 = new LeafRequest("C2", 1000, "123");
        LeafRequest leafRequest3 = new LeafRequest("C3", 1000, "123");
        LeafRequest leafRequest4 = new LeafRequest("C4", 1000, "123");
        BranchRequest b1 = new BranchRequest("GL-NEFT", 2000, List.of(leafRequest, leafRequest2), RailType.NEFT);
        BranchRequest b2 = new BranchRequest("GL-IFT", 1000, List.of(leafRequest3), RailType.IFT);
        BranchRequest b3 = new BranchRequest("GL-RTGS", 1000, List.of(leafRequest4), RailType.RTGS);
        return new RootRequest("Ag1", "GL1", 3000, List.of(b1, b2, b3));
    }
}
