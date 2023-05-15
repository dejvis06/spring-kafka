package com.example.consumer.impl;

import com.example.consumer.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class LibraryEventConsumerManual implements Consumer<Integer, String>, AcknowledgingMessageListener<Integer, String> {

    @KafkaListener(topics = {"library-events"})
    @Override
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord, Acknowledgment acknowledgment) {
        consume(consumerRecord);
        acknowledgment.acknowledge();
    }

    @Override
    public void consume(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("Consumer Record: {}", consumerRecord);
    }
}
