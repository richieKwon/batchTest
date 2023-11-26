package com.example.batch.batch;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.asm.SpringAsmInfo;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class JobExecution {
    private BatchStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
