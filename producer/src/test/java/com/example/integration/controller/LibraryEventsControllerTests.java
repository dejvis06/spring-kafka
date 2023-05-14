package com.example.integration.controller;

import com.example.domain.Book;
import com.example.domain.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 1)
@TestPropertySource(properties = {"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventsControllerTests {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    public void testPost() {

        Book book = Book.builder()
                .bookId(1)
                .bookName("test-book-name")
                .bookAuthor("test-book-author")
                .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(1)
                .book(book)
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("content-type", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<LibraryEvent> httpEntity = new HttpEntity<>(libraryEvent, httpHeaders);


        ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange("/v1/libraryevent", HttpMethod.POST, httpEntity, LibraryEvent.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }
}
