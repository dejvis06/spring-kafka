package com.example.integration.controller;

import com.example.common.exceptions.MyRetriableException;
import com.example.consumer.impl.LibraryEventConsumer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events", "library-events.RETRY", "library-events.DLT"}, partitions = 1)
@TestPropertySource(properties = {"spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventsConsumerIntegrationTest {

    public static final String LIBRARY_EVENTS_RETRY = "library-events.RETRY";
    public static final String LIBRARY_EVENTS_DLT = "library-events.DLT";
    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @SpyBean
    LibraryEventConsumer libraryEventConsumerSpy;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp() {
        kafkaListenerEndpointRegistry.getListenerContainers()
                .forEach(messageListenerContainer -> ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic()));
    }

    @Test
    void testConsume() throws ExecutionException, InterruptedException, JsonProcessingException, MyRetriableException {
        String json = "{ \"libraryEventId\": 1, \"book\": { \"bookId\": 0, \"bookName\": \"string\", \"bookAuthor\": \"string\" } }";
        kafkaTemplate.send("library-events", json).get();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(3, TimeUnit.SECONDS);

        verify(libraryEventConsumerSpy, times(1)).consume(isA(ConsumerRecord.class));
    }

    @Test
    void testRetryableTopic() throws ExecutionException, InterruptedException, JsonProcessingException, MyRetriableException {
        String json = "{ \"libraryEventId\": 0, \"book\": { \"bookId\": 0, \"bookName\": \"string\", \"bookAuthor\": \"string\" } }";
        kafkaTemplate.send("library-events", json).get();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await(3, TimeUnit.SECONDS);

        verify(libraryEventConsumerSpy, times(2)).consume(isA(ConsumerRecord.class));

        buildRetryableTopicConsumer();

        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, LIBRARY_EVENTS_RETRY);
        assertEquals(json, consumerRecord.value());
    }

    private void buildRetryableTopicConsumer() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, LIBRARY_EVENTS_RETRY);
    }
}
