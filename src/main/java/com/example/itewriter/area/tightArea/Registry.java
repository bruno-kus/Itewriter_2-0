package com.example.itewriter.area.tightArea;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.paint.Color;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Registry {
    private static final Map<String, Field> colors =
            Stream.of(Color.class.getFields()).collect(Collectors.toMap(Field::getName, Function.identity()));
    private static final String filename = "/Users/Bruno/Desktop/Itewriter/Itewriter_2-0/initialTags";

    // pytanie brzmi czy chcę manipulować tagami tylko poprzez rejestr, czy też na nich samych
    // najważniejsze są jednak zachowania tagu, czyli pola funkcjonalne

    public final ObservableSet<Tag> allTags = FXCollections.observableSet();

    private void addTag(Color color) {
    }

    // jeśli tylko Registry by tworzyło tagi, to mógłbym podawać rejestr w otwarty sposób
    public Registry() {
//        makeShitFromFile();
    }

    /**
     * każde zmienienie czegokolwiek powinno
     * wywołać ten o to offset!
     */
    public void bindBidirectional(Variation variation, ObservableList<Passage> passages) {

    }
    public ObservableList<Passage> viewOf(Variation variation) {
        // sprawdzić czy ta instancja wariacji należy do tego modelu
        // to może i lepiej zrobić, variation::viewOf -> może to i by jednak miało sens, żeby każda wariacja
        // miała dostęp do modelu
        // chociaż trzeba się zastanowić czy to w ogóle będzie potrzebne
        // i czy to w ogóle powinny być pasaże czy tylko Stringi
        // najważniejsze teraz jest tak czy siak aktualizowanie pozycji
    }
    public void offsetAllTags(int position, int offset) {
        for (var tag : allTags) {
            tag.offsetAllVariations(position, offset);
        }
    }
    public class Tag {
        public void offsetAllVariations(int position, int offset) {
            for (var variation : allVariations) {
                variation.offsetPositions(position, offset);
            }
        }
        private final StringProperty name = new SimpleStringProperty();
        private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
        public ObservableList<Variation> allVariations = FXCollections.observableArrayList();
//        private final IntegerProperty currentIndex = new SimpleIntegerProperty(-1);
//        private final ObjectBinding<ObservableList<StringProperty>> activeVariation = Bindings.createObjectBinding(
//                () -> variations.get(currentIndex.getValue()), variations, currentIndex
//            /*
//            czy wystarczyłby sam indeks?
//             */
//        );
//
//        public ObservableList<StringProperty> getActiveVariation() {
//            return activeVariation.get();
//        }
//
//        public ObjectBinding<ObservableList<StringProperty>> activeVariationProperty() {
//            return activeVariation;
//        }

        public ObjectProperty<Color> colorProperty() {
            return color;
        }

        public Color getColor() {
            return color.get();
        }

        public Tag(String name, Color color) {
            this.name.setValue(name);
            this.color.setValue(color);
        }

        public boolean setColor(Color color) {
            if (allTags.stream().map(Tag::getColor).noneMatch(color::equals)) {
                this.color.setValue(color);
                return true;
            } else {
                return false; // czy powinienem wrzucić tu wyjątek? Na pewno nie!
            }
        }

    }
//    void makeShitFromFile() {
//        try {
//            for (var line : Files.readAllLines(Paths.get(filename))) {
//                var splitLine = List.of(line.split("--"));
//                var name = splitLine.get(0);
//                if (colors.containsKey(name)) {
//                    var tag = new Tag(name, (Color) colors.get(name).get(null));
//                    for (int i = 1, size = splitLine.size(); i < size; i++) {
//                        var words = splitLine.get(i);
//                        // tworzenie nowej wariacji nie powinno tu być
//                        ObservableList<StringProperty> variation = FXCollections.observableArrayList();
//                        Stream.of(words.split(" "))
//                                .map(SimpleStringProperty::new)
//                                .forEachOrdered(variation::add);
//
//                        tag.variations.add(variation);
////                        System.out.printf("TagVariation:%n%s%n", variation);
//                    }
//                }
//            }
//        } catch (IOException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
