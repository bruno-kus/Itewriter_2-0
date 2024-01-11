package com.example.itewriter.area.arch;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.List;

public class TagModel {
    ObservableList<ObservableList<String>> variations = FXCollections.observableArrayList();
    private final IntegerProperty currentIndex = new SimpleIntegerProperty(-1);
    ObjectBinding<ObservableList<String>> activeVariation = Bindings.createObjectBinding(
            () -> variations.get(currentIndex.getValue()), variations , currentIndex
            /*
            czy wystarczyłby sam indeks?
             */
    );



    public void setCurrentIndex(int currentIndex) {
        this.currentIndex.set(currentIndex);
    }

//    public void enumerateSegments(List<MySegment> segments) {
//        // jeśli długości się nie zgadzają rzuć błąd
//        int i = 0;
//        for (var seg : segments) {
//            seg.setCurrentIndex(i++);
//        }
//
//    }
    public void initialize(List<String> strings) {
        variations.add(FXCollections.observableArrayList(strings));
        currentIndex.setValue(0);
    }

    public boolean increment() {
        if (currentIndex.getValue() < variations.size() - 1) {
            currentIndex.setValue(currentIndex.getValue() + 1);
            return true;
        } else {
            return false;
        }
    }
    public boolean decrement() {
        if (currentIndex.getValue() > 0) {
            currentIndex.setValue(currentIndex.getValue() - 1);
            return true;
        } else {
            return false;
        }
    }



    public IntegerProperty currentIndexProperty() {
        return currentIndex;
    }

    public ObservableList<ObservableList<String>> getVariations() {
        return variations;
    }

    public int getCurrentIndex() {
        return currentIndex.get();
    }

    public ObservableList<String> getActiveVariation() {
        return activeVariation.get();
    }

    public ObjectBinding<ObservableList<String>> activeVariationBinding() {
        return activeVariation;
    }
}