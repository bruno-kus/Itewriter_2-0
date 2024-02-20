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

    public static Iterable<Integer> differenceIterable(Iterable<Integer> minuends, Iterable<Integer> subtrahends) {
        return new Iterable<>() {
            final Iterable<Integer> minuendsIterable;
            final Iterable<Integer> subtrahendsIterable;
            {
                this.minuendsIterable = minuends;
                this.subtrahendsIterable = subtrahends;
            }
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<>() {
                    final Iterator<Integer> minuends = minuendsIterable.iterator();
                    final Iterator<Integer> subtrahends = subtrahendsIterable.iterator();
                    @Override
                    public boolean hasNext() {
                        return minuends.hasNext() && subtrahends.hasNext();
                    }

                    @Override
                    public Integer next() {
                        return minuends.next() - subtrahends.next();
                    }
                };
            }
        };
    }
    record DeltaIterator(Iterator<Integer> oldLengthIterator,
                         Iterator<Integer> newLengthIterator) implements Iterator<Integer> {
        @Override
        public boolean hasNext() {
            return newLengthIterator.hasNext() && oldLengthIterator.hasNext();
        }

        @Override
        public Integer next() {
            return newLengthIterator.next() - oldLengthIterator.next();
        }
    }
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