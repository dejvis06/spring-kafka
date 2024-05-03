package com.example.unit.controller;

import com.example.controller.LibraryEventsController;
import com.example.domain.Book;
import com.example.domain.LibraryEvent;
import com.example.producer.Producer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.CompletableFuture;

import org.mockito.Mockito;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.MessageHeaders;


@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
public class LibraryEventControllerTests {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    Producer<Integer, String, LibraryEvent> producer;

    @Test
    void testPost() throws Exception {
        Book book = Book.builder()
                .bookId(1)
                .bookName("test-book-name")
                .bookAuthor("test-book-author")
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(1)
                .book(book)
                .build();

        CompletableFuture<SendResult<Integer, String>> future = mockFuture();

        when(producer.produce(any())).thenReturn(future);

        String json = new ObjectMapper().writeValueAsString(libraryEvent);
        mockMvc.perform(post("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    private CompletableFuture<SendResult<Integer, String>> mockFuture() {
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>("topicName", 1, "Your message here");
        RecordMetadata recordMetadata = new RecordMetadata(
                new org.apache.kafka.common.TopicPartition("topicName", 1), 0, 0, System.currentTimeMillis(), Long.valueOf(-1), 1, 1);
        SendResult<Integer, String> mockSendResult = new SendResult<>(producerRecord, recordMetadata);
        CompletableFuture<SendResult<Integer, String>> future = CompletableFuture.completedFuture(mockSendResult);
        return future;
    }
}
