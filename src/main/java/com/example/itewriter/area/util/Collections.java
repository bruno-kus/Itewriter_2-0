package com.example.itewriter.area.util;

import java.util.Iterator;
import java.util.List;

public class Collections {

//    static <T> Iterable<T> moduleIterable(List<T> list, int from) {
//        class ModuloIterator implements Iterator<T> {
//            int index;
//            final int size;
//            int totalIterated = 0;
//            List<T> list;
//            public ModuloIterator(List<T> list, int from) {
//                size = list.size();
//                index = from;
//            }
//            @Override
//            public boolean hasNext() {
//                return totalIterated < size;
//            }
//            @Override
//            public T next() {
//                totalIterated++;
//                return list.get(index++ % size);
//            }
//        }
//        return () -> new ModuloIterator(list, from);
//    }


    public static <T> Iterable<T> moduloIterable(List<T> list, int from) {
        return () -> new Iterator<T>() {
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


    record ModuloIterable<T>(List<T> list, int from) implements Iterable<T> {
        class ModuloIterator implements Iterator<T> {
            int index;
            final int size;
            int totalIterated = 0;
            List<T> list;

            public ModuloIterator(List<T> list, int from) {
                size = list.size();
                index = from;
            }

            @Override
            public boolean hasNext() {
                return totalIterated < size;
            }

            @Override
            public T next() {
                totalIterated++;
                return list.get(index++ % size);
            }
        }

        @Override
        public Iterator<T> iterator() {
            return new ModuloIterator(list, from);
        }
    }

}
