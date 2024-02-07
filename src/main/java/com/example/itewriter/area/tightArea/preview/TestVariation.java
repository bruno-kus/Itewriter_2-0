package com.example.itewriter.area.tightArea.preview;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Stream;

public class TestVariation {
    public static TestVariation compose(TestVariation tv1, TestVariation tv2) {
        var result = new TestVariation(List.of(tv1, tv2));
        Stream.concat(tv1.passages.stream(), tv2.passages.stream())
                .sorted()
                .forEachOrdered(result.passages::add);
        return result;
    }
    List<TestVariation> components;
    ObservableList<StringProperty> passages;

    public TestVariation(List<TestVariation> components) {
        this.components = components;
    }
}
