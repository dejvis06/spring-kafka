# Table of Contents

- [Spring Kafka Project](#spring-kafka-project)
   - [Introduction](#introduction)
- [Consumer Application](#consumer-application)
   - [Consumer Configuration Overview](#consumer-configuration-overview)
      - [Listening to Kafka Topics](#listening-to-kafka-topics)
      - [Consumer Factory Configurations](#consumer-factory-configurations)
      - [ConcurrentKafkaListenerContainerFactory](#concurrentkafkalistenercontainerfactory)
- [Producer Application](#producer-application)
   - [Overview](#overview)
   - [Key Components](#key-components)
      - [LibraryEventProducer](#libraryeventproducer)
      - [Producer Interface](#producer-interface)
- [Domain](#domain)
   - [LibraryEvent](#libraryevent)
   - [Book](#book)
- [Setup and Configuration](#setup-and-configuration)
   - [Docker Compose](#docker-compose)


# Spring Kafka Project

This project is a demo for learning about Spring for Apache Kafka.

## Consumer
Located under `consumer/`, this service is a Spring Boot application that listens to Kafka topics and processes incoming messages.

### Consumer Configuration Overview

The `ConsumerConfig` class is a `@Configuration` bean, annotated also with `@EnableKafka`. The `@EnableKafka` enables spring's application context to register
the consumers annotated with the `@KafkaListener` annotation:
```java
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
```

#### Consumer Factory Configurations
The following configurations are specified in the `application.properties` file.

- **Bootstrap Servers**: `kafka:29092` - The Kafka broker addresses.
- **Key Deserializer**: `org.apache.kafka.common.serialization.IntegerDeserializer` - Deserializer for the key that is used when consuming messages.
- **Value Deserializer**: `org.apache.kafka.common.serialization.StringDeserializer` - Deserializer for the value that is used when consuming messages.
- **Group ID**: `library-events-listener-group`

This method defines a `ConcurrentKafkaListenerContainerFactory` bean that configures the Kafka listener containers and allows the creation of multiple concurrent consumer threads for each @KafkaListener

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<Integer, String> kafkaListenerContainerFactory(ConsumerFactory<Integer, String> consumerFactory) {
    ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setCommonErrorHandler(errorHandler());
    return factory;
}
```

### Producer Application

Found in `producer/`, the Producer Application serves as a Kafka message producer. It publishes library event messages to Kafka topics, handling serialization and response handling through callbacks. Key components include:
- **LibraryEventProducer**: Sends library events to Kafka, implementing error handling and successful message callbacks.
- **Producer**: Defines the generic producer interface, encapsulating the production of messages.

## Domain

Both applications share a common domain context with two primary entities:
- **LibraryEvent**: Represents an event in the library, containing identifiers and associated book details.
- **Book**: Contains book details like ID, name, and author.

## Setup and Configuration

Check the docker-compose file in the root project and start from there to build & run
