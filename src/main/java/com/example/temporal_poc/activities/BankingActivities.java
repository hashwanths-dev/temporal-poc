package com.example.temporal_poc.activities;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface BankingActivities {
    void transfer(String from, String to, double amount);
    void reverse_transfer(String from, String to, double amount);
}
