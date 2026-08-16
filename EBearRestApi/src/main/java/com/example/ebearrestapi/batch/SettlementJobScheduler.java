package com.example.ebearrestapi.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job dailySettlementJob;

    @Scheduled(cron = "0 0 0 * * ?")
    public void runDailySettlement() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("time", LocalDateTime.now().toString())
                    .toJobParameters();
            jobLauncher.run(dailySettlementJob, params);
        } catch (Exception e) {
            log.error("Settlement batch execution error: ", e);
        }
    }
}
