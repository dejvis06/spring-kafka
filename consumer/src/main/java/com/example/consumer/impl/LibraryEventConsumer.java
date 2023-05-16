package com.example.consumer.impl;

import com.example.common.exceptions.MyRetriableException;
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
    public static final String LIBRARY_EVENT_ID_0 = "Library Event ID = 0";
    public static final String LIBRARY_EVENTS_RETRY = "library-events.RETRY";
    public static final String LIBRARY_EVENTS = "library-events";

    @KafkaListener(topics = {LIBRARY_EVENTS})
    @Override
    public void consume(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException, MyRetriableException {

        LibraryEvent libraryEvent = new ObjectMapper().readValue(consumerRecord.value(), LibraryEvent.class);
        if (libraryEvent.getLibraryEventId() == null)
            throw new IllegalArgumentException(LIBRARY_EVENT_ID_CANNOT_BE_NULL);
        else if (libraryEvent.getLibraryEventId() == 0)
            throw new MyRetriableException(LIBRARY_EVENT_ID_0);
        log.info("Consumer Record: {}", consumerRecord);
    }

    @KafkaListener(topics = {LIBRARY_EVENTS_RETRY})
    @Override
    public void retry(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("Retrying Consumer Record: {}", consumerRecord);
    }
}
