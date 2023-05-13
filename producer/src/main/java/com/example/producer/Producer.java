package com.example.producer;

public interface Producer<T> {

    void produce(T t) throws Exception;
}
