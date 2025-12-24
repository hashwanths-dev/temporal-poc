package com.example.temporal_poc.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RootRequest {
    String agentAccount;
    String poolAccount;
    double totalAmount;
    List<BranchRequest> branches;
}
