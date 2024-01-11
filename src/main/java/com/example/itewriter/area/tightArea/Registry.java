package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;

public class Registry {
    // pytanie brzmi czy chcę manipulować tagami tylko poprzez rejestr, czy też na nich samych
    // najważniejsze są jednak zachowania tagu, czyli pola funkcjonalne
    public final Set<Tag> availableTags = new HashSet<>();
    public void addTag(Color color) {
        availableTags.add(new Tag(color));
    }
    // jeśli tylko Registry by tworzyło tagi, to mógłbym podawać rejestr w otwarty sposób
    public class Tag {
        // current variation
        private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
        private final IntegerProperty currentIndex = new SimpleIntegerProperty(-1);
        ObservableList<ObservableList<StringProperty>> variations = FXCollections.observableArrayList();
        // każdy tag ma swoje osobiste wariacje
        // to jest to samo co z mapami, tam tag model to było to co tutaj się nazywa tag
        // tylko tam identyfikatorem były enumy, a tutaj są adresy obiektów
        // seems good to me
        private final ObjectBinding<ObservableList<StringProperty>> activeVariation = Bindings.createObjectBinding(
                () -> variations.get(currentIndex.getValue()), variations , currentIndex
            /*
            czy wystarczyłby sam indeks?
             */
        );

        public ObservableList<StringProperty> getActiveVariation() {
            return activeVariation.get();
        }

        public ObjectBinding<ObservableList<StringProperty>> activeVariationProperty() {
            return activeVariation;
        }

        public ObjectProperty<Color> colorProperty() {
            return color;
        }
        public Color getColor() {
            return color.get();
        }

        private Tag(Color color) {
            this.color.setValue(color);
        }

        public boolean setColor(Color color) {
            if (availableTags.stream().map(Tag::getColor).noneMatch(color::equals)) {
                this.color.setValue(color);
                return true;
            } else {
                return false; // czy powinienem wrzucić tu wyjątek? Na pewno nie!
            }
        }

    }
}
