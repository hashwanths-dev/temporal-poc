package com.example.temporal_poc.activities;

import com.example.temporal_poc.constants.Constants;
import io.temporal.spring.boot.ActivityImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@ActivityImpl(taskQueues = Constants.BANKING_TASK_QUEUE)
@Slf4j
public class BankingActivitiesImpl implements BankingActivities {

    @Override
    public void transfer(String from, String to, double amount) {
        log.info("transfer {} from {} to {}", amount, from, to);
    }

    @Override
    public void reverse_transfer(String from, String to, double amount) {
        log.info("ROLLBACK: transfer {} from {} to {}", amount, from, to);
    }
}
