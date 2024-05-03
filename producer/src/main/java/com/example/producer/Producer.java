package com.example.producer;

import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

public interface Producer<K, V, T> {
    CompletableFuture<SendResult<K, V>> produce(T t) throws Exception;
}
