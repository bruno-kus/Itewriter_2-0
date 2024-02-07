package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.reactfx.util.Either;

import java.util.*;

import static com.example.itewriter.area.tightArea.MyArea.EITHER_OPS;
import static com.example.itewriter.area.util.Collections.moduloIterable;

public class AreaController {
    MyArea area;


    private final ManifestationModel manifestationModel;
    Property<Registry.Tag> selectedTagProperty;
    public final SimpleVariationSelector simpleVariationSelector;
    private final Registry registry;
    final TagIndexer tagIndexer;

    public AreaController(MyArea area, Registry registry) {
        this.area = area;
        this.registry = registry;
        this.tagIndexer = new TagIndexer(this.registry);
        manifestationModel = new ManifestationModel(tagIndexer, this);
        simpleVariationSelector = new SimpleVariationSelector(tagIndexer, selectedTagProperty);

        area.setOnKeyPressed(e -> {
            final var carPos = area.getCaretPosition();
            final var allPos = manifestationModel.getAllPositions();
            final var i = -(Collections.binarySearch(allPos, carPos) + 1);
            // znowu potrzebuję czegoś takiego jak taggedposition!
            // w ogóle mógłbym tutaj mieć listę manifestacji
            // albo może nawet snapshotów manifestacji!
//            if (carPos > allPos.get(i) && carPos < allPos.get(i)) +

            l1:
            for (final var entry : manifestationModel.observableManifestations.entrySet()) {
                final var passPos = entry.getValue().getPassagePositions();
                for (final var pos : passPos) {
                    if (pos == i) {
                        final var innerIndex = Collections.binarySearch(passPos, i);
                        final var start = allPos.get(i);
                        final var end = start + entry.getValue().getVariation().getTexts().get(innerIndex).length();
                        final var property = entry.getValue().getVariation().passages.get(innerIndex);
                        if () {
                            property.setValue(new StringBuilder(property.getValue())
                                    .insert(carPos - start, e.getText()).toString());
                        }
                        break l1;
                    }
                }
            }
            /*
            zmapować entry set manifestacji do czegoś jak
            pozycja -> tag, Variation
            czy manifestacja powinna mieć wewnątrz siebie taga
            mogę zamiast tego operować na entrisach!

            pozycję znalezioną w zgromadzeniu łatwo też znaleźć w konkretnej manifestacji (taki sam binSearch)
             */
        });
    }

    public void addTag(/**/) {
        // najpierw muszę całego stworzyć!
    }

    private void addTagToManifestations(Registry.Tag tag) {
        registry.allTags.add(tag);
        final var manifestation = new Manifestation(
                Bindings.createObjectBinding(
                        () -> tag.getVariationsProperty().get(tagIndexer.getTagIndices().get(tag)),
                        this.tagIndexer.getTagIndices())
        );
        manifestationModel.observableManifestations.put(tag, manifestation);
    }

    public void removeTagFromManifestations(Registry.Tag tag) {
        manifestationModel.observableManifestations.remove(tag);
    }

    {
        Gop c = :
    }
    /*
    czy mogę n ilość tagów łączyć?
    ależ oczywiście!
     */

    private void recursiveCombinations(Registry.Tag[] tags, int index) {
        // bez sensu, bo nie bierze pod uwagę kolejności w jakiej powinny być własności! najpierw powinna byc matryca
        // tych wszystkich tagów!
        List<List<StringProperty>> currentVariations = new ArrayList<>();
        currentVariations.add(new ArrayList<>());
        for (var tag : tags) {
            for (var vari : tag.getVariationsProperty()) {
                for (var prop : vari.passages) {
                    List<List<StringProperty>> futureVariations = new ArrayList<>(currentVariations.size());
                    for (var currentVariation : currentVariations) {
                        var futureVariation = new ArrayList<>(currentVariation);
                        futureVariation.add(prop);
                        futureVariations.add(futureVariation);
                    }
                    currentVariations = futureVariations;
                }
            }
        }
    }

    private static class State {
        public State(Registry.Tag tag, Iterator<Integer> iterator) {
            this.iterator = iterator;
            this.tag = tag;
            position = iterator.next();
        }

        public final Iterator<Integer> iterator;
        public final Registry.Tag tag;
        public int position;

        public Iterator<Integer> getIterator() {
            return iterator;
        }

        public Registry.Tag getTag() {
            return tag;
        }

        public int getPosition() {
            return position;
        }
    }

    private List<Registry.Tag> getPatternForManyTags(Registry.Tag[] tags) {
        var states = Arrays.stream(tags).map(tag -> new State(tag, manifestationModel.getPositions(tag).iterator())).toList();
        var size = Arrays.stream(tags).mapToInt(Registry.Tag::getNumberOfPassages).sum();
        var result = new ArrayList<Registry.Tag>(size);
        while (states.stream().map(State::getIterator).anyMatch(Iterator::hasNext)) {
            var firstState = Collections.min(states, Comparator.comparingInt(State::getPosition));
            firstState.position = firstState.iterator.next();
            result.add(firstState.tag);
        }
        return result;
    }


    public Registry.Tag composeManyTags(Registry.Tag... tags) {
        var tagSet = new HashSet<Registry.Tag>();
        for (var tag : tags) if (!tagSet.add(tag)) throw new IllegalArgumentException();
        if (!registry.allTags.containsAll(tagSet)) throw new IllegalArgumentException();
        var composite = this.registry.new Tag("composed", Color.WHITE, tagSet);
        var pattern = getPatternForManyTags(tags);
        // zamiast budować liniowo mogę też od razu wstawiać elementy na właściwe pozycje, brzmi jak plan
        // powinienem mieć iterator po pozycjach na które ma wstawiać iterator po własnościach
        // własność -> pozycja
        var patternMap = new HashMap<Registry.Tag, List<Integer>>();
        for (int i = 0; i < pattern.size(); i++) {
            var tag = pattern.get(i);
            patternMap.putIfAbsent(tag, new ArrayList<>());
            patternMap.get(tag).add(i);
        }


        var totalVariations = Arrays.stream(tags).mapToInt(Registry.Tag::getNumberOfPassages)
                .reduce(1, (a, b) -> a * b);
        var totalVariationPassages = Arrays.stream(tags).mapToInt(Registry.Tag::getNumberOfPassages)
                .sum();
        var c = new ArrayList<>();


        List<List<StringProperty>> previousCompositeVariations = new ArrayList<>(1);
        previousCompositeVariations.add(new ArrayList<>(0));
        for (var tag : tags) {
            for (var variation : moduloIterable(tag.getVariationsProperty(), tagIndexer.getIndex(tag))) {
                List<List<StringProperty>> currentCompositeVariations = new ArrayList<>(totalVariationPassages);
                for (var composedVariation : previousCompositeVariations) {
                    var futureVariation = new ArrayList<StringProperty>(totalVariationPassages);
                    Collections.copy(futureVariation, composedVariation);
                    for (var index : patternMap.get(tag)) {
                        futureVariation.add(index, variation.getPassagesProperty().get(index));
                    }
                    currentCompositeVariations.add(futureVariation);
                }
                previousCompositeVariations = currentCompositeVariations;
            }
        }
        var compositeVariations = composite.getVariationsProperty();
        previousCompositeVariations.stream().map(composedVariation -> {
            var variation = new SimpleVariation();
            variation.getPassagesProperty().addAll(composedVariation);
            return variation;
        }).forEach(compositeVariations::add);
        return composite;
    }

    public void composeTags(Registry.Tag t1, Registry.Tag t2) {
        if (t1 == t2 || !registry.allTags.contains(t1) || !registry.allTags.contains(t2)) {
            throw new IllegalArgumentException();
        }
        var composite = this.registry.new Tag("composed", Color.WHITE, List.of(t1, t2));
        var vars1 = t1.getVariationsProperty();
        var vars2 = t2.getVariationsProperty();
        var it1 = manifestationModel.getPositions(t1).iterator();
        var it2 = manifestationModel.getPositions(t2).iterator();
        var pos1 = it1.next();
        var pos2 = it2.next();
        var tagOrder = new ArrayList<Registry.Tag>();
        while (it1.hasNext() && it2.hasNext()) {
            if (pos1 < pos2) {
                pos1 = it1.next();
                tagOrder.add(t1);
            } else if (pos1 > pos2) {
                pos2 = it2.next();
                tagOrder.add(t2);
            }
        }
        for (var vari1 : vars1) {
            for (var vari2 : vars2) {
                var tempVari = new SimpleVariation();
                var strPropIt1 = vari1.passages.iterator();
                var strPropIt2 = vari2.passages.iterator();
                vari2.passages.iterator();
                for (var tag : tagOrder) {
                    if (tag == t1) tempVari.passages.add(strPropIt1.next());
                    else if (tag == t2) tempVari.passages.add(strPropIt2.next());
                }
                composite.getVariationsProperty().add(tempVari);
            }
        }
        addTagToManifestations(composite);
        removeTagFromManifestations(t1);
        removeTagFromManifestations(t2);
    }

    public void decomposeTag(Registry.Tag tag) {

    }


    /**
     * jak na razie to podoba mi się umiejscowienie tej metody w kontrolerze
     * ponieważ będzie tworzyła segmenty oraz zamieniała też poprzednią narrację
     * ewentualnie mogę ją rozbić na dwa wywołania, gdzie strefa tylko uaktualnia, a
     * kontroler aktualizuje też pozycje
     * tylko, co z tego, że ja se zrobię multiChanges skoro nie mam tych multi changes odbitych...
     */


    public void writeMySegment(Registry.Tag tag) {
        area.setOnKeyPressed(k -> insertMySegment(area.getCaretPosition(), tag));
    }

    public void insertMySegment(int position, Registry.Tag tag) {
        replaceWithMySegment(position, position, tag);
    }

    private void replaceSelectionWithMySegment(Registry.Tag tag) {
        System.out.println("MyArea::replaceSelectionWithMySegment");

        /*
        najpierw bym dodawał segment pusty
        a następnie patrzył, gdzie też ten pusty został dodany!
         */
        // dodaj do modelu najpierw?
        Either<String, MySegment> right = Either.right(new MySegment(tag, sequentialTagSelector));
        area.replaceSelection(ReadOnlyStyledDocument.fromSegment(
                right,
                null,
                null,
                EITHER_OPS));

        var inserted = area.getTagSegments(tag).stream().filter(seg -> seg.getInVariationIndex() == -1).findFirst();
        if (inserted.isPresent()) {
            int i = area.getTagSegments(tag).indexOf(inserted.get());
            sequentialTagSelector.getSelectedTag().ifPresent(t ->
                    t.getActiveVariation().add(i, new SimpleStringProperty(area.getText(area.getSelection()))));
        }
    }

    public void replaceWithMySegment(int start, int end, Registry.Tag tag) {
        // tutaj uwierzytelniam
        if (sequentialTagSelector.tags.contains(tag))
            area.replace(start, end, ReadOnlyStyledDocument.fromSegment(
                    Either.right(new MySegment(tag, sequentialTagSelector)),
                    null,
                    "",
                    EITHER_OPS
            ));
        else throw new RuntimeException();
    }
}
