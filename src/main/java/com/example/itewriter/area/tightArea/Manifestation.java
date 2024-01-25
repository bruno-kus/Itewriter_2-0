package com.example.itewriter.area.tightArea;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Manifestation {
    // to by mogła być w ogóle nawet lista, nie mapa...
    final List<Integer> passagePositions = new ArrayList<>();

    private final ObservableValue<Variation> variation;


    public Variation getVariation() {
        return variation.getValue();
    }

    public ObservableValue<Variation> variationObservable() {
        return variation;
    }
    public Manifestation(ObservableValue<Variation> variationObservable) {
        this.variation = variationObservable;
    }

    public List<Integer> getPassagePositions() {
        return passagePositions;
    }
}
