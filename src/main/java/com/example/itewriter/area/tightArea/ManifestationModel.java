package com.example.itewriter.area.tightArea;

import com.example.itewriter.area.util.MyRange;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.util.Pair;
import org.reactfx.util.Tuple3;
import org.reactfx.util.Tuples;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ManifestationModel {
    /*
    przecież ja to chcę aktualizować od góry do dołu
    znaczy się to zmienia się cała mapa jednocześnie i dopiero na jej podstawie poszczególne wariacje
    a nie na odwrót
    dlaczego?
    dlatego, że wariacje nie są od siebie niezależne!
    manifestacja NIGDY nie zmienia się, nie zmieniając przy tym innych wariacji, NIGDY

    i opór był taki, żeby zamiast kopiować to unieważniać ręcznie całą strukturę przy każdej zmianie
    da się to zrobić z reaktywnymi potokami
     */


    private final HashMap<Registry.Tag, List<Integer>> passagePositions = new HashMap<>();
    private final Binding<Map<Registry.Tag, List<Integer>>> unmodifiablePassagePositions =
            Bindings.createObjectBinding(
                    () -> passagePositions.entrySet().stream().collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> Collections.unmodifiableList(entry.getValue()))));
    public ObservableValue<Map<Registry.Tag, List<Integer>>> passagePositionsObservable() {
        return unmodifiablePassagePositions;
    }


    public final ObservableMap<Registry.Tag, Manifestation> observableManifestations = new SimpleMapProperty<>();

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





    /*
    poważne pytanie tutaj
    jakie mogą być sposoby reprezentowania modelu!
    chciałem niemodyfikowalne widoki
    ale jak wtedy rozpoznam czy dodany element był przed czy po obecnie wybranym przez selektor?

    1 lista
    2 mapa z listą dubletów
    3 same triplety?
     */

    public List<Integer> getAllPositions() {
        return observableManifestations.values().stream()
                .flatMap(m -> m.getPassagePositions().stream()).sorted().toList();
    }

    public List<Integer> getPositions(Registry.Tag tag) {
        return observableManifestations.get(tag).getPassagePositions();
    }


    ManifestationModel(TagIndexer tagIndexer, MyArea area) {
        observableManifestations.addListener(new PositionUpdater(area));
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
        final var deltaIterator = changes.iterator();
//        final var tps = manifestations.entrySet().stream().flatMap(entry -> {
//            final var tag = entry.getKey();
//            final var positions = entry.getValue().getPassagePositions();
//            return positions.stream().map(position -> new TP(tag, position));
//        }).sorted(Comparator.comparing(TP::position)).toList();
//        manifestations.values().stream().map(Manifestation::getPassagePositions).forEach(List::clear);
        final ArrayList<TaggedPosition> taggedPositions;

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

    class PositionUpdater implements MapChangeListener<Registry.Tag, Manifestation> {
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
        public void onChanged(Change<? extends Registry.Tag, ? extends Manifestation> change) {
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

