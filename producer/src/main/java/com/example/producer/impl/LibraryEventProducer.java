package com.example.producer.impl;

import com.example.domain.LibraryEvent;
import com.example.producer.Producer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class LibraryEventProducer implements Producer<Integer, String, LibraryEvent> {

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public LibraryEventProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public CompletableFuture<SendResult<Integer, String>> produce(LibraryEvent libraryEvent) throws JsonProcessingException {
        String value = objectMapper.writeValueAsString(libraryEvent);

        log.info("Producing library event: {}", value);
        return kafkaTemplate.sendDefault(value);
    }
}
