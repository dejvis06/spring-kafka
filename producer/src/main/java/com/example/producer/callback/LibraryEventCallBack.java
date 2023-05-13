package com.example.producer.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
public class LibraryEventCallBack implements ListenableFutureCallback<SendResult<Integer, String>> {
    @Override
    public void onFailure(Throwable ex) {
        log.error("Failed, error: {} ", ex.getMessage());
    }

    @Override
    public void onSuccess(SendResult<Integer, String> result) {

        int key = result.getProducerRecord().key();
        String value = result.getProducerRecord().value();
        int partition = result.getRecordMetadata().partition();

        log.info("Message sent successfully for key: {}, value: {} and partition: {}", key, value, partition);
    }
}
