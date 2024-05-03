package com.example.producer.callback;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;

@Slf4j
public class LibraryEventCallBack implements ProducerListener<Integer, String> {

    @Override
    public void onSuccess(ProducerRecord<Integer, String> producerRecord, RecordMetadata recordMetadata) {
        int key = producerRecord.key();
        String value = producerRecord.value();
        int partition = recordMetadata.partition();

        log.info("Message sent successfully for key: {}, value: {} and partition: {}", key, value, partition);
    }

    @Override
    public void onError(ProducerRecord<Integer, String> producerRecord, RecordMetadata recordMetadata, Exception exception) {
        log.error("Failed message with key: {} and value: {}, error: {} ", producerRecord.key(), producerRecord.value(), exception.getMessage());
    }
}
