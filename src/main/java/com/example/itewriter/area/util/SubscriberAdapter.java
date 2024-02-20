package com.example.itewriter.area.util;

import java.util.concurrent.Flow;

public interface SubscriberAdapter<T> extends Flow.Subscriber<T> {
    @Override
    default void onSubscribe(Flow.Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }
    @Override
    default void onError(Throwable throwable) {
    }

    @Override
    default void onComplete() {
    }
}
