package com.example.itewriter.area.tightArea;

import com.example.itewriter.area.tightArea.preview.CompositeManifestation;
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

    //    /**
//     * każde zmienienie czegokolwiek powinno
//     * wywołać ten o to offset!
//     */
//    public void bindBidirectional(Variation variation, ObservableList<Passage> passages) {
//
//    }
    public ObservableList<Passage> viewOf(SimpleVariation simpleVariation) {
        // sprawdzić czy ta instancja wariacji należy do tego modelu
        // to może i lepiej zrobić, variation::viewOf -> może to i by jednak miało sens, żeby każda wariacja
        // miała dostęp do modelu
        // chociaż trzeba się zastanowić czy to w ogóle będzie potrzebne
        // i czy to w ogóle powinny być pasaże czy tylko Stringi
        // najważniejsze teraz jest tak czy siak aktualizowanie pozycji
    }


    public class Tag {
        ObservableSet<Tag> components = null;

        // czy to powinno być leniwie alokowane? chyba tak, ponieważ tag bazowy nie może stać się kompozytem
        private Optional<Collection<Tag>> getComponents() {
            return Optional.ofNullable(components);
        }

        public boolean isComposite() {
            return components != null;
        }


        private final StringProperty name = new SimpleStringProperty();
        private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
        private final ObservableList<SimpleVariation> allSimpleVariations = FXCollections.observableArrayList();

        public ObservableList<SimpleVariation> getVariationsProperty() {
            return allSimpleVariations;
        }

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

        public Tag(String name, Color color, Collection<Tag> components) {
            this(name, color);
            this.components = FXCollections.observableSet(Set.copyOf(components));
        }

        public int getNumberOfPassages() {
            return allSimpleVariations.get(0).passages.size();
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

    class Composer {
        /*
        czy to oznacza, że same w sobie tagi
        powinny być abstrakcyjne?
         */
        void compose(Tag t1, Tag t2, ManifestationModel manifestationModel) {
            if (t1 == t2 || !allTags.contains(t1) || !allTags.contains(t2)) {
                throw new IllegalArgumentException();
            }
            var composite = new Tag("composed", Color.WHITE, List.of(t1, t2));

            var m1 = manifestationModel.observableManifestations.get(t1);
            var m2 = manifestationModel.observableManifestations.get(t2);
            var compoManif = CompositeManifestation.composeManifestations(m1, m2);
            manifestationModel.observableManifestations.remove(t1);
            manifestationModel.observableManifestations.remove(t2);
            manifestationModel.observableManifestations.put(composite, compoManif); // :)


            allTags.remove(t1);
            allTags.remove(t2);
            allTags.add(composite);
        }

        void decompose(Tag tag) {
            allTags.remove(tag);
            allTags.addAll(tag.components);
        }
    }
}
