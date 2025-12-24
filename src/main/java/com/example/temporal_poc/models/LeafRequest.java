package com.example.temporal_poc.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeafRequest {
    private String customerAccount;
    private double amount;
    private String referenceId;
}
