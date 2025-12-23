package com.example.temporal_poc.workflow;

import com.example.temporal_poc.models.TransactionNode;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.List;

@WorkflowInterface
public interface MoneyTransferWorkflow {

    @WorkflowMethod
    void execute(List<TransactionNode> transactions);
}
