package com.example.itewriter.area.tightArea;

import com.example.itewriter.area.util.MyRange;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.SetChangeListener;
import javafx.scene.Node;
import javafx.util.Pair;

import java.util.*;
import java.util.function.Supplier;

public class ManifestationModel {
    //    public final Map<Registry.Tag, Manifestation> manifestations = new HashMap<>();
    public final ObservableMap<Registry.Tag, Manifestation> observableManifestations = new SimpleMapProperty<>();

    public List<Integer> getAllPositions() {
        return observableManifestations.values().stream()
                .flatMap(m -> m.getPassagePositions().stream()).sorted().toList();
    }


    /**
     * Changes to the indexer will be reflected in the manifestation model.
     * zmiany w zamanifestowanych wariacjach
     * powinny być odbite w strefie
     *
     * @param registry
     * @param tagIndexer
     */
    ManifestationModel(TagIndexer tagIndexer, MyArea area) {
        observableManifestations.addListener((MapChangeListener<? super Registry.Tag, ? super Manifestation>)
                change -> {
                    if (change.wasAdded()) {
                        final var manifestation = change.getValueAdded();
                        manifestation.variationObservable().addListener((ob, ov, nv) -> {
                            final var tag = change.getKey();
                            final var oldLengths = ov.getLengths().iterator();
                            final var oldStarts = manifestation.getPassagePositions().iterator();
                            final var newTexts = nv.getTexts().iterator();
                            final var builder = area.createMultiChange();
                            while (oldLengths.hasNext() && oldStarts.hasNext()) {
                                final var oldStart = oldStarts.next();
                                builder.replaceText(oldStart, oldStart + oldLengths.next(), newTexts.next());
                            }
                            this.updatePositions(
                                    manifestation.getPassagePositions(),
                                    ov.getLengths(),
                                    nv.getLengths()
                            );
                        });
                    }
                }
        );
    }

    public static void multiChange(
            MyArea area, List<Pair<MyRange, String>> oldValues, List<Pair<MyRange, String>> newValues) {
        if (oldValues.size() != newValues.size()) throw new IllegalArgumentException();
        var builder = area.createMultiChange();
        for (int i = 0; i < oldValues.size(); i++) {
            var oldRange = oldValues.get(i).getKey();
            var newText = newValues.get(i).getValue();
            builder.replaceText(oldRange.start, oldRange.end, newText);
        }
        builder.commit();
    }

    public void updatePositions(
            List<Integer> changingOldPositions,
            List<Integer> changingOldLengths,
            List<Integer> changingNewLengths) {

        record TaggedPosition(Registry.Tag tag, int position) implements Comparable<TaggedPosition> {
            @Override
            public int compareTo(TaggedPosition that) {
                return Integer.compare(this.position, that.position);
            }
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
        ;
        var factory = new Supplier<Iterator<Integer>>() {
            @Override
            public Iterator<Integer> get() {
                return new DeltaIterator(changingOldLengths.iterator(), changingNewLengths.iterator());
            }
        };
        {
            Node c;
        }
        /*
        iteracja która porównuje na bieżąco if-ami
        bierze dwie listy i kiedy ma wziąć next to porównuje frontowe indeksy i zwraca ten pierwszy, przesuwając go
        no i tyle!
        iterator inspirowany mergem :)
        do tego dorzucić dwa tanie if-y czy są przedziały rozbieżne
        oraz binary search -> już przy długości 8 będę robił 5 porównań zamiast 8! mimo, że obydwa są O(n), a logn może
        być łącznie dłuższy jeżeli trzeba będzie zaczynać od początku i tak
        */
        /*
        zrobienie generycznego merge\sortere pewniew wymagało by mapy, która każdej z N sekwencji przyporządkowywałaby obecny idneks
        w javie algorytmy też powinny być deklaratywne! (pisze się je dla estetyki ograniczenia kompleksowości, nie dla realnego przyspieszenia programu)
         */

//        for (var entry : observableManifestations.entrySet()) {
//            final var it = new DeltaIterator(changingOldLengths.iterator(), changingNewLengths.iterator());
//            final var passPos = new ArrayList<>(entry.getValue().getPassagePositions());
//
//            int offset = it.next();
//            for (var pos : passPos) {
//                int delta = 0;
//                if (Collections.binarySearch(changingOldPositions, pos) >= 0) delta = it.next();
//                passPos
//                offset += delta;
//            }
//        }
        final var deltaIterator = factory.get();
//        final var tps = manifestations.entrySet().stream().flatMap(entry -> {
//            final var tag = entry.getKey();
//            final var positions = entry.getValue().getPassagePositions();
//            return positions.stream().map(position -> new TP(tag, position));
//        }).sorted(Comparator.comparing(TP::position)).toList();
//        manifestations.values().stream().map(Manifestation::getPassagePositions).forEach(List::clear);
        final ArrayList<TaggedPosition> taggedPositions;
        {
            int size = 0;
            for (var manifestation : observableManifestations.values())
                size += manifestation.getPassagePositions().size();
            taggedPositions = new ArrayList<>(size);
        }
        for (var entry : observableManifestations.entrySet()) {
            var tag = entry.getKey();
            var positions = entry.getValue().getPassagePositions();
            for (var position : positions) taggedPositions.add(new TaggedPosition(tag, position));
            positions.clear();
        }
        Collections.sort(taggedPositions);
        {
            int offset = deltaIterator.next();
            for (var tp : taggedPositions) {
                int delta = 0;
                if (Collections.binarySearch(changingOldPositions, tp.position) >= 0) delta = deltaIterator.next();
                observableManifestations.get(tp.tag).getPassagePositions().add(tp.position + offset);
                offset += delta;
            }
        }

    }
}

