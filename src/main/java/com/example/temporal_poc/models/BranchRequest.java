package com.example.temporal_poc.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchRequest {
    private String railAccount;
    private double amount;
    private List<LeafRequest> leaves;
    private RailType type;
}
