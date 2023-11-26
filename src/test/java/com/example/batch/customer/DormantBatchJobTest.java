package com.example.batch.customer;

import com.example.batch.batch.BatchStatus;
import com.example.batch.batch.JobExecution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DormantBatchJobTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DormantBatchJob dormantBatchJob;

    @BeforeEach
    public void setUp(){
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("three dominant accounts out of 5")
    void test1(){
        // given
        saveCustomer(364);
        saveCustomer(366);
        saveCustomer(366);
        saveCustomer(366);
        saveCustomer(366);

        // when
        final JobExecution result = dormantBatchJob.execute();

        final long dormantCount = customerRepository.findAll()
                .stream()
                .filter(it -> it.getStatus() == Status.DORMANT)
                .count();

        assertThat(dormantCount).isEqualTo(4);
        assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    @DisplayName("all 10 accounts must be dominant, otherwise the number of dominant account is 0")
    void test2(){
        saveCustomer(1);
        saveCustomer(1);
        saveCustomer(1);
        saveCustomer(300);
        saveCustomer(1);
        saveCustomer(100);
        saveCustomer(1);
        saveCustomer(1);
        saveCustomer(10);
        saveCustomer(10);

        final JobExecution result = dormantBatchJob.execute();

        final long dormantCount = customerRepository.findAll()
                .stream()
                .filter(it -> it.getStatus() == Status.DORMANT)
                .count();

        assertThat(dormantCount).isEqualTo(0 );
        assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    @DisplayName("batch process must be working in case of no accounts")
    void test3(){

        final JobExecution result = dormantBatchJob.execute();

        final long dormantCount = customerRepository.findAll()
                .stream()
                .filter(it -> it.getStatus() == Status.DORMANT)
                .count();

        assertThat(dormantCount).isEqualTo(0 );
        assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    @DisplayName("should return failed when the job is not successful")
    void test4(){
        // given
        final DormantBatchJob dormantBatchJob = new DormantBatchJob(null);
        // when
        final JobExecution result = dormantBatchJob.execute();
        // then
        assertThat(result.getStatus()).isEqualTo(BatchStatus.FAILED);
    }

    private void saveCustomer(long loginMinusDays) {

        final String uuid = UUID.randomUUID().toString();
        final Customer test = new Customer(uuid, uuid+"@gmail.com");
        test.setLoginAt(LocalDateTime.now().minusDays(loginMinusDays));
        customerRepository.save(test);
    }
}