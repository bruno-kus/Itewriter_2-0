package com.example.itewriter.area.tightArea.preview;

import javafx.beans.binding.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestManifestation {
    public static TestManifestation compose(TestManifestation tm1, TestManifestation tm2) {
        var result = new TestManifestation(List.of(tm1, tm2));
        Stream.concat(tm1.passagePositions.stream(), tm2.passagePositions.stream())
                .sorted()
                .forEachOrdered(result.passagePositions::add);
        var composeVariation = TestVariation.compose(tm1.variation.getValue(), tm2.variation.getValue());
        return result;
    }
    public static List<TestManifestation> decompose(TestManifestation tm) {
        return tm.components;
    }
    final List<TestManifestation> components;
    private final ObservableList<IntegerProperty> passagePositions = FXCollections.observableArrayList();
    final ObservableValue<TestVariation> variation = new SimpleObjectProperty<>();

    public TestManifestation(List<TestManifestation> components) {
        this.components = Collections.unmodifiableList(components);
    }
}
