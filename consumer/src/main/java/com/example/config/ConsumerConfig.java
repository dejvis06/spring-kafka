package com.example.config;

import com.example.common.exceptions.RetryableException;
import com.example.consumer.listener.LibraryEventListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

@Configuration
@EnableKafka
@Slf4j
public class ConsumerConfig {

    public static final String LIBRARY_EVENTS_RETRY = "library-events.RETRY";
    public static final String LIBRARY_EVENTS_DLT = "library-events.DLT";
    @Autowired
    KafkaTemplate<String, Integer> kafkaTemplate;

    @Bean
    ConcurrentKafkaListenerContainerFactory<Integer, String> kafkaListenerContainerFactory(ConsumerFactory<Integer, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    private DefaultErrorHandler errorHandler() {

        ExponentialBackOffWithMaxRetries exponentialBackOffWithMaxRetries = new ExponentialBackOffWithMaxRetries(2);
        exponentialBackOffWithMaxRetries.setInitialInterval(1_000L);
        exponentialBackOffWithMaxRetries.setMultiplier(2.0);
        exponentialBackOffWithMaxRetries.setMaxInterval(2_000L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer(), exponentialBackOffWithMaxRetries);
        errorHandler.setRetryListeners(new LibraryEventListener());
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
    }

    DeadLetterPublishingRecoverer deadLetterPublishingRecoverer() {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (r, e) -> {
                    if (e instanceof RetryableException) {
                        return new TopicPartition(r.topic() + LIBRARY_EVENTS_RETRY, r.partition());
                    } else {
                        return new TopicPartition(r.topic() + LIBRARY_EVENTS_DLT, r.partition());
                    }
                });
        return recoverer;
    }
}
