package com.example.batch.customer;

import lombok.extern.slf4j.Slf4j;

public interface EmailProvider {
    void send(
            String emailAddress,
            String title,
            String body
    );

    @Slf4j
    class Fake implements EmailProvider{

        @Override
        public void send(String emailAddress, String title, String body) {
          log.info("{} email has been sent {}, {}", emailAddress, title, body );
        }
    }
}
