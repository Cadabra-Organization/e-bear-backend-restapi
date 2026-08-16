package com.example.ebearrestapi.batch;

import com.example.ebearrestapi.service.SettlementAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class SettlementBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SettlementAdminService settlementAdminService;

    @Bean
    public Job dailySettlementJob() {
        return new JobBuilder("dailySettlementJob", jobRepository)
                .start(aggregateSettlementStep())
                .build();
    }

    @Bean
    public Step aggregateSettlementStep() {
        return new StepBuilder("aggregateSettlementStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    settlementAdminService.executeSettlement(LocalDate.now());
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
