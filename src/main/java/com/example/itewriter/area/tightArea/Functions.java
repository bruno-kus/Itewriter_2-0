package com.example.itewriter.area.tightArea;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

class Functions {
//    public void insertPassage(Registry.Tag tag, String text, int position) {
//        insertPosition(tag, position, text.length());
//        manifestVariations.get(tag).getValue().insertPassage(
//                Collections.binarySearch(positions.getValue().get(tag), position),
//                text);
//        // ale ja tutaj wstawiam tylko string!
//        /*
//        żeby mieć gwarancję, że wstawię tą samą instancję, to nie jest podawana ona jako argument
//        a jako słuchacz, bo jest tylko jedno poprawne źródło argumentów i to jest ten
//        kogo słuchamy właśnie
//         */
//    }
//
//    public void insertPosition(Registry.Tag tag, int position, int offset) {
//        updatePositions(Functions.insertPosition(tag, position).andThen(Functions.offsetPositions(position, offset)));
//    }
//
//    public void offsetPositions(int pivot, int offset) {
//        updatePositions(Functions.offsetPositions(pivot, offset)); // i tu przydałby się adapter
//    }
//
//    public void offsetPositions(Iterable<Integer> pivots, Iterable<Integer> offsets) {
//        updatePositions(Functions.offsetPositions(pivots, offsets));
//    }
//
//    void updatePositions(BiFunction<Registry.Tag, List<Integer>, List<Integer>> updatePositions) {
//        positions.setValue(positions.getValue().entrySet().stream().collect(Collectors.toUnmodifiableMap(
//                Map.Entry::getKey,
//                entry -> updatePositions.apply(entry.getKey(), entry.getValue()))));
//    }
//
//    void updatePositions(UnaryOperator<List<Integer>> updatePositions) {
//        updatePositions((tag, positions) -> updatePositions.apply(positions));
//    }
    static BiFunction<Registry.Tag, List<Integer>, List<Integer>> insertPosition(
            Registry.Tag tag, int position) {
        return (positionTag, positionList) -> {
            if (positionTag == tag) {
                var result = new LinkedList<>(positionList);
                result.add(Collections.binarySearch(positionList, position), position);
                return Collections.unmodifiableList(result);
            } else {
                return positionList;
            }
        };
    }

    static UnaryOperator<List<Integer>> offsetPositions(int pivot, int offset) {
        return offsetPositions(Collections.singletonList(pivot), Collections.singletonList(offset));
    }

    static UnaryOperator<List<Integer>> offsetPositions(Iterable<Integer> pivotIterable, Iterable<Integer> offsetIterable) {
        var pivots = pivotIterable.iterator();
        var offsets = offsetIterable.iterator();
        if (pivots.hasNext() && offsets.hasNext()) {
            return positionList -> {
                int currentPivot = pivots.next();
                if (currentPivot >= positionList.get(positionList.size() - 1)) // !
                    return positionList;

                var result = new LinkedList<Integer>();
                var positions = positionList.iterator();

                int currentPosition = positions.next();
                int accumulatedOffset = 0;

                while (positions.hasNext()) {
                    while (currentPosition > currentPivot && offsets.hasNext()) {
                        if (pivots.hasNext()) currentPivot = pivots.next();
                        accumulatedOffset += offsets.next();
                    }
                    result.add(currentPosition + accumulatedOffset);
                    currentPosition = positions.next();
                }
                return Collections.unmodifiableList(result);
            };
        } else
            return UnaryOperator.identity();
    }
}
