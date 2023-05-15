package com.example.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.RetryListener;

@Slf4j
public class LibraryEventListener implements RetryListener {
    @Override
    public void failedDelivery(ConsumerRecord<?, ?> record, Exception ex, int i) {
        log.error("Failed Delivery for: {}, error: {}", record, ex.getMessage());
    }

    @Override
    public void recovered(ConsumerRecord<?, ?> record, Exception ex) {
        log.info("Recovered record: {}, error: {}", record, ex.getMessage());
    }

    @Override
    public void recoveryFailed(ConsumerRecord<?, ?> record, Exception original, Exception failure) {
        log.info("Recovery failed for: {}, error: {}", record, failure.getMessage());
    }
}
