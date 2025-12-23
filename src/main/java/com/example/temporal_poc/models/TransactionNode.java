package com.example.temporal_poc.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionNode {
    private String fromAccount;
    private String toAccount;
    private double amount;
    private TransactionNode parent;
}
