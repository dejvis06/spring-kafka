package com.example.consumer.impl;

import com.example.common.exceptions.RetryableException;
import com.example.consumer.Consumer;
import com.example.domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LibraryEventConsumer implements Consumer<Integer, String> {

    public static final String LIBRARY_EVENT_ID_CANNOT_BE_NULL = "Library Event ID cannot be null!";

    @KafkaListener(topics = {"library-events"})
    @Override
    public void consume(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException, RetryableException {

        LibraryEvent libraryEvent = new ObjectMapper().readValue(consumerRecord.value(), LibraryEvent.class);
        if (libraryEvent.getLibraryEventId() == null)
            throw new IllegalArgumentException(LIBRARY_EVENT_ID_CANNOT_BE_NULL);
        else if (libraryEvent.getLibraryEventId() == 0)
            throw new RetryableException();
        log.info("Consumer Record: {}", consumerRecord);
    }
}
