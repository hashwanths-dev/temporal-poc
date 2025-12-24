package com.example.temporal_poc.activities;

import com.example.temporal_poc.models.RailType;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface BankingActivities {
    void transfer(String from, String to, double amount, RailType railType);
    void  compensate(String from, String to, double amount, RailType railType);
}
