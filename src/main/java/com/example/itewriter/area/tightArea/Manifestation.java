package com.example.itewriter.area.tightArea;

import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;

public class Manifestation {
    // to by mogła być w ogóle nawet lista, nie mapa...
    final List<Integer> passagePositions = new ArrayList<>(); // w Scali bym przypisywał nową listę za każdym razem?

    private final ObservableValue<SimpleVariation> variation;


    public SimpleVariation getVariation() {
        return variation.getValue();
    }

    public ObservableValue<SimpleVariation> variationObservable() {
        return variation;
    }
    public Manifestation(ObservableValue<SimpleVariation> variationObservable) {
        this.variation = variationObservable;
    }

    public List<Integer> getPassagePositions() {
        return passagePositions;
    }
}
