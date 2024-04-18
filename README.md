# Spring Kafka Project

This project demonstrates an implementation of Apache Kafka with Spring Boot, structured into two main applications: Consumer and Producer.

## Applications

### Consumer Application

Located under `consumer/`, the Consumer Application is a Spring Boot application that listens to Kafka topics and processes incoming messages. Key components include:
- **LibraryEventConsumer**: Processes library events from Kafka. It handles retryable conditions and logs appropriate messages based on the event status.
- **LibraryEventListener**: Manages retries for failed Kafka message deliveries, providing detailed logs on failures and recoveries.

### Producer Application

Found in `producer/`, the Producer Application serves as a Kafka message producer. It publishes library event messages to Kafka topics, handling serialization and response handling through callbacks. Key components include:
- **LibraryEventProducer**: Sends library events to Kafka, implementing error handling and successful message callbacks.
- **Producer**: Defines the generic producer interface, encapsulating the production of messages.

## Domain

Both applications share a common domain context with two primary entities:
- **LibraryEvent**: Represents an event in the library, containing identifiers and associated book details.
- **Book**: Contains book details like ID, name, and author.

## Setup and Configuration

Each application contains Dockerfiles and Kubernetes configurations for easy deployment. Maven is used for dependency management, with wrapper scripts included for both Unix and Windows environments.

For detailed setup and running instructions, refer to individual READMEs within the `consumer/` and `producer/` directories.
