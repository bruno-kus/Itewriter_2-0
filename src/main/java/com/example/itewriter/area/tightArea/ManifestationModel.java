package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.collections.SetChangeListener;

import java.util.*;

public class ManifestationModel {
    public final Map<Registry.Tag, Manifestation> manifestations = new HashMap<>();

    /**
     * Changes to the indexer will be reflected in the manifestation model.
     * zmiany w zamanifestowanych wariacjach
     * powinny być odbite w strefie
     *
     * @param registry
     * @param tagIndexer
     */
    ManifestationModel(Registry registry, TagIndexer tagIndexer, AreaController areaController) {
        registry.allTags.addListener((SetChangeListener.Change<? extends Registry.Tag> change) -> {
            if (change.wasAdded()) {
                final var tag = change.getElementAdded();
                final var manifestation = new Manifestation(
                        Bindings.createObjectBinding(
                                () -> tag.getAllVariations().get(tagIndexer.getTagIndices().get(tag)),
                                tagIndexer.getTagIndices())
                );
                manifestation.variationObservable().addListener((ob, ov, nv) -> {
                    this.updatePositions(
                            manifestation.getPassagePositions(),
                            ov.getLengths(),
                            nv.getLengths()
                    );
                    areaController.multiChange()
                });
                manifestations.put(tag, manifestation);
            } else if (change.wasRemoved()) {
                manifestations.remove(change.getElementRemoved());
            }
        });
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
        final var deltaIterator = new Iterator<Integer>() {
            final Iterator<Integer> oldLengthIterator = changingOldLengths.iterator();
            final Iterator<Integer> newLengthIterator = changingNewLengths.iterator();

            @Override
            public boolean hasNext() {
                return newLengthIterator.hasNext() && oldLengthIterator.hasNext();
            }

            @Override
            public Integer next() {
                return newLengthIterator.next() - oldLengthIterator.next();
            }
        };
//        final var tps = manifestations.entrySet().stream().flatMap(entry -> {
//            final var tag = entry.getKey();
//            final var positions = entry.getValue().getPassagePositions();
//            return positions.stream().map(position -> new TP(tag, position));
//        }).sorted(Comparator.comparing(TP::position)).toList();
//        manifestations.values().stream().map(Manifestation::getPassagePositions).forEach(List::clear);
        final ArrayList<TaggedPosition> taggedPositions;
        {
            int size = 0;
            for (var manifestation : manifestations.values()) size += manifestation.getPassagePositions().size();
            taggedPositions = new ArrayList<>(size);
        }
        for (var entry : manifestations.entrySet()) {
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
                manifestations.get(tp.tag).getPassagePositions().add(tp.position + offset);
                offset += delta;
            }
        }
    }

}
