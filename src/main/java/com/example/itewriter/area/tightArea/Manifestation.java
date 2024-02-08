package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Manifestation {
    // żeby to gówno refaktoryzować muszę najpierw prześledzić działanie updateFunction w modelu manifestacji
    // zamiast czyszczenia i odtwarzania, powinienem robić kopię, modyfikować ją i wstawiać nową niemodyfikowalną
    // aczkolwiek wtedy musiałbym robić kopię całej mapy


    final List<Integer> passagePositions = new ArrayList<>();


    final ObjectBinding<List<Integer>> passagePositionsObservable = Bindings.createObjectBinding(
            () -> Collections.unmodifiableList(passagePositions)
    );

    public void offsetPositions(int from, int offset) {
        // (...)
        passagePositionsObservable.invalidate();
    }
    public void updatePositions(Iterable<Integer> changes) {
        // iterable = fabryka iteratorów!
    }
    public void insertPassage(int position, String text) {

    }
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
