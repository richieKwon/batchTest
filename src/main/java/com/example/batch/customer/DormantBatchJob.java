package com.example.batch.customer;

import com.example.batch.batch.BatchStatus;
import com.example.batch.batch.JobExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DormantBatchJob {
    private final CustomerRepository customerRepository;
    private final EmailProvider emailProvider;

    public DormantBatchJob(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.emailProvider = new EmailProvider.Fake();
    }

    public JobExecution execute(){

        final JobExecution jobExecution = new JobExecution();
        jobExecution.setStatus(BatchStatus.STARTING);
        jobExecution.setStartTime(LocalDateTime.now());

        int pageNo = 0;

        try {
            while (true) {
                final PageRequest pageRequest = PageRequest.of(pageNo, 1, Sort.by("id").ascending());
                final Page<Customer> page = customerRepository.findAll(pageRequest);

                final Customer customer;
                if (page.isEmpty()) {
                    break;
                } else {
                    pageNo++;
                    customer = page.getContent().get(0);
                }

                final boolean isDormantTarget = LocalDate.now()
                        .minusDays(365)
                        .isAfter(customer.getLoginAt().toLocalDate());

                if (isDormantTarget) {
                    customer.setStatus(Status.DORMANT);
                } else {
                    continue;
                }

                customerRepository.save(customer);

                emailProvider.send(customer.getEmail(), "Your account is not active now", "content");
            }
            jobExecution.setStatus(BatchStatus.COMPLETED);
        } catch (Exception e) {
            jobExecution.setStatus(BatchStatus.FAILED);
        }

        jobExecution.setEndTime(LocalDateTime.now());

        return jobExecution;
    }


}
