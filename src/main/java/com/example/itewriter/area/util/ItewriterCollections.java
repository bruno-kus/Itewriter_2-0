package com.example.itewriter.area.util;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ItewriterCollections {
    // cyclic, nie rotary, bo okrąg nie to samo, co obrót
    // cyclic, czyli zapętlające się; goniące swój własny ogon
    // rotary już by się tak nie tłumaczył

//    public static <T> void forEachWithIndex(BiConsumer<T, Integer> biConsumer) {
//        // ?
//    }
    public static <T> void forEachWithIndex(List<T> list, BiConsumer<? super T, Integer> action) {
        Objects.requireNonNull(list);
        Objects.requireNonNull(action);

        for (int i =  0; i < list.size(); i++) {
            action.accept(list.get(i), i);
        }
    }
    public static <T> Iterable<T> cyclicIterable(List<T> list, int from) {
        return () -> new Iterator<>() {
            int index = from;
            final List<T> listReference = list;
            final int size = listReference.size();
            int totalIterated = 0;

            @Override
            public boolean hasNext() {
                return totalIterated < size;
            }

            @Override
            public T next() {
                totalIterated++;
                return listReference.get(index++ % size);
            }
        };
    }
}