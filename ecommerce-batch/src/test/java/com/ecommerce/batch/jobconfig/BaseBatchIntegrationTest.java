package com.ecommerce.batch.jobconfig;

import com.ecommerce.batch.BatchApplication;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Sql("/sql/schema.sql")
@AutoConfigureObservability
@SpringBatchTest
@SpringJUnitConfig(classes = {BatchApplication.class})
public abstract class BaseBatchIntegrationTest {
    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;

    protected static void assertJobCompleted(JobExecution jobExecution) {
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }
}
