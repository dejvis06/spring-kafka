package com.example.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface Consumer<K, V> {

    void consume(ConsumerRecord<K, V> consumerRecord);
}
