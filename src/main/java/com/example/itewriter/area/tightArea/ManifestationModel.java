package com.example.itewriter.area.tightArea;

import com.example.itewriter.area.util.ItewriterCollections;
import com.example.itewriter.area.util.MyRange;
import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.util.Pair;
import org.reactfx.util.Tuple3;
import org.reactfx.util.Tuples;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

    /*
    - zaadaptować funkcyjny offset aby jednak był brudny
    - stworzyć słuchacza wariacji, który przy każdej zmianie będzie a) dodawał słuchacz do każdego pasażu i odejmował b) przesuwał offsety i generował zmianę w strefie
    - ale to na samym końcu, bo to będzie robione raz a dobrze w tym momencie, gdy w ogóle tworzę observableValue simple variation!
     */

public class ManifestationModel {
    private final Property<Map<Registry.Tag, List<Integer>>> positions = new SimpleObjectProperty<>();
    private final Map<Registry.Tag, List<Integer>> simplePositions = new HashMap<>();
    private final Binding<Void> simplePositionsObservable = Bindings.createObjectBinding(() -> null);








    private final ObservableMap<Registry.Tag, ObservableValue<SimpleVariation>> manifestVariations = new SimpleMapProperty<>();




    {
        var o = synchronizeMaps(manifestVariations, (tag, variationObservable) -> variationObservable.getValue().getPassagesObservable());


    }

    private <A, B, D> Object synchronizeMaps(ObservableMap<A, B> map1,

                                             BiFunction<A, B, ObservableList<D>> projection
    ) {
        var map2 = new SimpleMapProperty<A, ObservableList<Property<D>>>();
        map1.addListener((MapChangeListener<? super A, ? super B>) mapChange -> {
            if (mapChange.wasAdded()) {
                var key = mapChange.getKey();
                var value = mapChange.getValueAdded();
                var list1 = projection.apply(key, value);
                map2.putIfAbsent(key, new SimpleListProperty<>());
                list1.addListener((ListChangeListener<? super D>) listChange -> {
                    while (listChange.next()) {
                        if (listChange.wasAdded()) {
                            int offset = listChange.getFrom();
                            ItewriterCollections.forEachWithIndex(list1, (d, index) -> {
                                var list2 = map2.get(key);
                                list2.add(
                                        offset + index,
                                        new SimpleObjectProperty<>(d) {{
                                            this.bind(Bindings.createObjectBinding(
                                                    () -> list1.get(list2.indexOf(this)), list1)
                                            );
                                        }});
                            });
                        }
                    }
                });
            }
        });
        return map2;
    }

    {
        var posMap = synchronizeMaps(
                manifestVariations,
                (observableVariation) -> observableVariation.getValue().getPassagesObservable(),
                (tag, index) -> positions.getValue().get(tag).get(index),
                (ignore) -> positions
        );
        var texts = synchronizeMaps(
                manifestVariations,
                (observableVariation) -> observableVariation.getValue().getPassagesObservable(),
                (tag, index) -> manifestVariations.get(tag).getValue().getPassagesObservable().get(index),
                manifestVariations::get
        );

    }

    public static class GenericManifestationProperty<T> {
        // na podstawie listy w której się znajdują, czyli potrzebują do niej referencji
        final List<GenericManifestationProperty<T>> myContainer; // coś takiego powinno być przekazywane w ukryty sposób
        final ObjectProperty<T> myValue;

        GenericManifestationProperty(T initialValue, List<GenericManifestationProperty<T>> container, Function<Integer, T> getter, Observable dependency) {
            this.myContainer = container;
            myValue = new SimpleObjectProperty<>(initialValue);
            myValue.bind(Bindings.createObjectBinding(
                    () -> getter.apply(container.indexOf(this)),
                    dependency)
            );
        }
    }

    private <A, B, D> Object synchronizeMaps(ObservableMap<A, B> map1,

                                             Function<B, ObservableList<?>> listProjection,
                                             BiFunction<A, Integer, D> extraction,

                                             Function<A, Observable> dependencyFactory
    ) {
        var map2 = new SimpleMapProperty<A, ObservableList<GenericManifestationProperty<D>>>();
        map1.addListener((MapChangeListener<? super A, ? super B>) mapChange -> {
            if (mapChange.wasAdded()) {
                var key = mapChange.getKey();
                var list1 = listProjection.apply(mapChange.getValueAdded());
                map2.putIfAbsent(key, new SimpleListProperty<>());
                list1.addListener((ListChangeListener<? super Object>) listChange -> {
                    while (listChange.next()) {
                        if (listChange.wasAdded()) {
                            int offset = listChange.getFrom();
                            int listChangeSize = listChange.getAddedSize();
                            Stream.iterate(0, i -> i < listChangeSize, i -> i + 1).forEachOrdered(index -> {
                                        var list2 = map2.get(key);
                                        list2.add(offset + index, new GenericManifestationProperty<>(
                                                extraction.apply(key, index),
                                                list2,
                                                i -> extraction.apply(key, i),
                                                dependencyFactory.apply(key)
                                        ));
                                    }
                            );
                        }
                    }
                });
            }
        });
        return map2;
    }



















    {
        /*
        to powinno być chyba zinline'owane wszystko...
         */
        ObservableMap<Registry.Tag, ObservableList<AbstractManifestationMap.AbstractManifestation>> op = new AbstractManifestationMap() {

            @Override
            Integer computePosition(Registry.Tag tag, int index) {
                return positions.getValue().get(tag).get(index);
            }

            @Override
            Observable computePositionDependency(Registry.Tag tag) {
                return positions;
            }

            @Override
            StringProperty computePassage(Registry.Tag tag, int index) {
                return manifestVariations.get(tag).getValue().getPassagesObservable().get(index);
            }

            @Override
            Observable computePassageDependency(Registry.Tag tag) {
                return manifestVariations.get(tag);
            }
        };

    }
    abstract class AbstractManifestationMap extends SimpleMapProperty<Registry.Tag, ObservableList<AbstractManifestationMap.AbstractManifestation>> {
        AbstractManifestationMap() {
            manifestVariations.addListener((MapChangeListener<? super Registry.Tag, ? super ObservableValue<SimpleVariation>>) mapChange -> {
                if (mapChange.wasAdded()) {
                    var tag = mapChange.getKey();
                    var list1 = mapChange.getValueAdded().getValue().getPassagesObservable();
                    this.putIfAbsent(tag, new SimpleListProperty<>());
                    list1.addListener((ListChangeListener<? super Object>) listChange -> {
                        while (listChange.next()) {
                            if (listChange.wasAdded()) {
                                int offset = listChange.getFrom();
                                int listChangeSize = listChange.getAddedSize();
                                Stream.iterate(0, i -> i < listChangeSize, i -> i + 1).forEachOrdered(i -> {
                                            var container = this.get(tag);
                                            var absoluteIndex = offset + i;
                                            container.add(absoluteIndex, new AbstractManifestation(
                                                    computePosition(tag, absoluteIndex),
                                                    computePassage(tag, absoluteIndex),
                                                    container,
                                                    computePositionDependency(tag),
                                                    computePassageDependency(tag)
                                            ) {
                                                @Override
                                                Integer computePosition(int index) {
                                                    return AbstractManifestationMap.this.computePosition(tag, index);
                                                }

                                                @Override
                                                StringProperty computePassage(int index) {
                                                    return AbstractManifestationMap.this.computePassage(tag, index);
                                                }
                                            });
                                        }
                                );
                            }
                        }
                    });
                }
            });
        }
        abstract Integer computePosition(Registry.Tag tag, int index); // protected?
        abstract Observable computePositionDependency(Registry.Tag tag);
        abstract StringProperty computePassage(Registry.Tag tag, int index);
        abstract Observable computePassageDependency(Registry.Tag tag);
        public abstract class AbstractManifestation {
            List<AbstractManifestation> container;
            ObjectProperty<Integer> position;
            ObjectProperty<StringProperty> passage;

            AbstractManifestation(Integer initialPosition, StringProperty initialPassage, List<AbstractManifestation> container,
                                  Observable positionDependency, Observable passageDependency) {
                this.container = container;
                this.position = new AbstractProperty<>(initialPosition, positionDependency) {
                    @Override
                    Integer computeValue(int index) {
                        return computePosition(index);
                    }
                };
                this.passage = new AbstractProperty<>(initialPassage, passageDependency) {
                    @Override
                    StringProperty computeValue(int index) {
                        return computePassage(index);
                    }
                };
            }
            abstract Integer computePosition(int index);
            abstract StringProperty computePassage(int index);


            abstract class AbstractProperty<T> extends SimpleObjectProperty<T> {
                AbstractProperty(T initialValue, Observable dependency){
                    super(initialValue);
                    bind(Bindings.createObjectBinding(
                            () -> computeValue(container.indexOf(AbstractManifestation.this)),
                            dependency
                    ));
                }
                abstract T computeValue(int index);
            }
        }
    }



    public static class MagicManifestation {
        List<MagicManifestation> container;
        ObjectProperty<Integer> position;
        ObjectProperty<StringProperty> passage;

        MagicManifestation(Integer initialPosition, StringProperty initialPassage, List<MagicManifestation> container,
                           Function<Integer, Integer> positionGetter, Function<Integer, StringProperty> passageGetter,
                           Observable positionDependency, Observable passageDependency) {
            this.container = container;
            this.position = createMagicProperty(initialPosition, positionGetter, positionDependency);
            this.passage = createMagicProperty(initialPassage, passageGetter, passageDependency);
        }

        <T> ObjectProperty<T> createMagicProperty(T initialValue, Function<Integer, T> getter, Observable dependency) {
            return new SimpleObjectProperty<>(initialValue) {{
                bind(Bindings.createObjectBinding(
                        () -> getter.apply(container.indexOf(MagicManifestation.this)),
                        dependency
                ));
            }};
        }
    }

    {
        Object opa = inlineMagicManifestationMap(
                (tag, index) -> positions.getValue().get(tag).get(index),
                (ignored) -> positions,
                (tag, index) -> manifestVariations.get(tag).getValue().getPassagesObservable().get(index),
                manifestVariations::get
        );
    }

    private Object inlineMagicManifestationMap(
            BiFunction<Registry.Tag, Integer, Integer> getPosition,

            Function<Registry.Tag, Observable> whenToUpdatePositions,
            BiFunction<Registry.Tag, Integer, StringProperty> getPassage,
            Function<Registry.Tag, Observable> whenToUpdatePassages
    ) {
        var map2 = new SimpleMapProperty<Registry.Tag, ObservableList<MagicManifestation>>();
        manifestVariations.addListener((MapChangeListener<? super Registry.Tag, ? super ObservableValue<SimpleVariation>>) mapChange -> {
            if (mapChange.wasAdded()) {
                var key = mapChange.getKey();
                var list1 = mapChange.getValueAdded().getValue().getPassagesObservable();
                map2.putIfAbsent(key, new SimpleListProperty<>());
                list1.addListener((ListChangeListener<? super Object>) listChange -> {
                    while (listChange.next()) {
                        if (listChange.wasAdded()) {
                            int offset = listChange.getFrom();
                            int listChangeSize = listChange.getAddedSize();
                            Stream.iterate(0, i -> i < listChangeSize, i -> i + 1).forEachOrdered(index -> {
                                        var list2 = map2.get(key);
                                        list2.add(offset + index, new MagicManifestation(
                                                getPosition.apply(key, index),
                                                getPassage.apply(key, index),
                                                list2,
                                                i -> getPosition.apply(key, i),
                                                i -> getPassage.apply(key, i),
                                                whenToUpdatePositions.apply(key),
                                                whenToUpdatePassages.apply(key)
                                        ));
                                    }
                            );
                        }
                    }
                });
            }
        });
        return map2;
    }

    {
        var magics = explicitMakeMagicManifestationMap(
                manifestVariations,
                observableVariation -> observableVariation.getValue().getPassagesObservable(),
                (tag, index) -> positions.getValue().get(tag).get(index),
                (tag, index) -> manifestVariations.get(tag).getValue().getPassagesObservable().get(index),
                (ignored) -> positions,
                manifestVariations::get
        );
    }


    private <A, B> Object explicitMakeMagicManifestationMap(ObservableMap<A, B> map1,

                                                            Function<B, ObservableList<?>> whenToAdd,
                                                            BiFunction<A, Integer, Integer> getPosition,
                                                            BiFunction<A, Integer, StringProperty> getPassage,

                                                            Function<A, Observable> whenToUpdatePositions,
                                                            Function<A, Observable> whenToUpdatePassages
    ) {
        var map2 = new SimpleMapProperty<A, ObservableList<MagicManifestation>>();
        map1.addListener((MapChangeListener<? super A, ? super B>) mapChange -> {
            if (mapChange.wasAdded()) {
                var key = mapChange.getKey();
                var list1 = whenToAdd.apply(mapChange.getValueAdded());
                map2.putIfAbsent(key, new SimpleListProperty<>());
                list1.addListener((ListChangeListener<? super Object>) listChange -> {
                    while (listChange.next()) {
                        if (listChange.wasAdded()) {
                            int offset = listChange.getFrom();
                            int listChangeSize = listChange.getAddedSize();
                            Stream.iterate(0, i -> i < listChangeSize, i -> i + 1).forEachOrdered(index -> {
                                        var list2 = map2.get(key);
                                        list2.add(offset + index,
                                                new MagicManifestation(
                                                        getPosition.apply(key, index),
                                                        getPassage.apply(key, index),
                                                        list2,
                                                        i -> getPosition.apply(key, i),
                                                        i -> getPassage.apply(key, i),
                                                        whenToUpdatePositions.apply(key),
                                                        whenToUpdatePassages.apply(key)
                                                )
                                        );
                                    }
                            );
                        }
                    }
                });
            }
        });
        return map2;
    }

















@Deprecated
    public final ObservableMap<Registry.Tag, com.example.itewriter.area.tightArea.Manifestation> observableManifestations = new SimpleMapProperty<>();

    public final ObservableValue<List<Tuple3<Integer, String, Registry.Tag>>> sortedPassages =
            Bindings.createObjectBinding(this::getSortedPassages, observableManifestations);

    private List<Tuple3<Integer, String, Registry.Tag>> getSortedPassages() {
        return observableManifestations.entrySet().stream()
                .flatMap(entry -> {
                    var manifestation = entry.getValue();
                    var positions = manifestation.getPassagePositions().iterator();
                    var texts = manifestation.getVariation().getTexts().iterator();
                    var tag = entry.getKey();
                    var tuples = new ArrayList<Tuple3<Integer, String, Registry.Tag>>();
                    while (positions.hasNext() || texts.hasNext()) {
                        tuples.add(Tuples.t(positions.next(), texts.next(), tag));
                    }
                    return tuples.stream();
                }).sorted(Comparator.comparing(tuple -> tuple._1)).toList();
    }

    public List<Registry.Tag> getPatternForManyTags(Collection<Registry.Tag> tags) {
        var states = tags.stream().map(tag -> new State(tag, getPositions(tag).iterator())).toList();
        var size = tags.stream().mapToInt(Registry.Tag::getNumberOfPassages).sum();
        var result = new ArrayList<Registry.Tag>(size);
        while (states.stream().map(State::getIterator).anyMatch(Iterator::hasNext)) {
            var firstState = Collections.min(states, Comparator.comparingInt(State::getPosition));
            firstState.position = firstState.iterator.next();
            result.add(firstState.tag);
        }
        return result;
    }











    public List<Integer> getAllPositions() {
        return observableManifestations.values().stream()
                .flatMap(m -> m.getPassagePositions().stream()).sorted().toList();
    }

    public List<Integer> getPositions(Registry.Tag tag) {
        return observableManifestations.get(tag).getPassagePositions();
    }


    ManifestationModel(TagIndexer tagIndexer, MyArea area) {
        observableManifestations.addListener(new Functions(area)); // ?
    }

    public void insertPassage(Registry.Tag tag, int position, String text) {
        observableManifestations.get(tag).insertPassage(position, text);

        // na chwilę obecną mam chyba automatyczne unieważnianie... zbyt automatyczne?
        // update positions?
        // invalidate
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
        Iterable<Integer> changes = () -> new DeltaIterator(changingOldLengths.iterator(), changingNewLengths.iterator());

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
        var deltaIterator = changes.iterator();
//        final var tps = manifestations.entrySet().stream().flatMap(entry -> {
//            final var tag = entry.getKey();
//            final var positions = entry.getValue().getPassagePositions();
//            return positions.stream().map(position -> new TP(tag, position));
//        }).sorted(Comparator.comparing(TP::position)).toList();
//        manifestations.values().stream().map(Manifestation::getPassagePositions).forEach(List::clear);
        ArrayList<TaggedPosition> taggedPositions;

        int size = 0;
        for (var manifestation : observableManifestations.values())
            size += manifestation.getPassagePositions().size();
        taggedPositions = new ArrayList<>(size);

        // to jest po prostu uzyskanie wzorku! z tą dodatkową różnicą, że oprócz tagów mamy pozycje
        for (var entry : observableManifestations.entrySet()) {
            var tag = entry.getKey();
            var positions = entry.getValue().getPassagePositions();
            for (var position : positions) taggedPositions.add(new TaggedPosition(tag, position));
            positions.clear();
        }
        Collections.sort(taggedPositions);

        // tu jest tylko faktyczne offsetowanie
        // i to bym chciał zastąpić wizytowanie manifestacji
        // spróbujmy zrefaktoryzować!

        int offset = deltaIterator.next();
        for (var tp : taggedPositions) {
            int delta = 0;
            // ten warunek by się zmienił, by zamiast równości miałbym równość lub większość
            if (Collections.binarySearch(changingOldPositions, tp.position) >= 0) delta = deltaIterator.next();
            observableManifestations.get(tp.tag).getPassagePositions().add(tp.position + offset);
            offset += delta;
        }
    }

    static class State {
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

    class PositionUpdater implements MapChangeListener<Registry.Tag, com.example.itewriter.area.tightArea.Manifestation> {
        final MyArea area;

        public PositionUpdater(MyArea area) {
            this.area = area;
        }

        /*
        są 4 uaktualnienia horyzontalne
        na zmianie wybranej wariacji -> automat, ponieważ są zależne od indeksera, a ten jest kontrolowany z zewnątrz
        na wstawieniu kolumny -> metoda w kontrolerze, ręczny zwyczajne offset
        na zmianie tekstu pasażu -> to na bank automat, bo mogę zmieniać tekst, nie mając dostępu do interfejsu kontrolera
        na zmianie tekstu poza pasażem -> metoda w kontrolerze
         */
        /*
        jest również wertykalne uaktualnienie
        na wstawieniu nowego pasażu do dowolnej wariacji tagu, wszystkie pozostałe wariacje tagu
        pasaże może wstawiać tylko kontroler i robić to jednocześnie z pozycjami
        oznacza to, że dostęp do wariacji bezpośrednio będzie miał tylko variation model
        a wszystkie widoki mają dostęp co najwyżej do niemodyfikowalnej listy stringów

         */
        @Override
        public void onChanged(Change<? extends Registry.Tag, ? extends com.example.itewriter.area.tightArea.Manifestation> change) {
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
                    updatePositions(
                            manifestation.getPassagePositions(),
                            ov.getLengths(),
                            nv.getLengths()
                    );
                });
            }
        }
    }
}

