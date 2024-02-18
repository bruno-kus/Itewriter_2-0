package com.example.itewriter.area.tightArea;

import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;

public class SimpleVariation {
    /*
    konieczna jest kompozycja nad dziedziczeniem bo nigdy nie chcę, żeby ktoś na wariacji użył setAll!
     */
    public final ObservableList<StringProperty> passages = FXCollections.observableList(new LinkedList<>());
    private final ObservableList<StringProperty> unmodifiablePassages = FXCollections.unmodifiableObservableList(passages);

    public ObservableList<StringProperty> getPassagesObservable() {
        return unmodifiablePassages;
    }
    /*
    zajebista sprawa!
     */
    public void insertPassage(int index, String text) {
        passages.add(index, new SimpleStringProperty(text));
    }
    public void removePassage(int index) {
        passages.remove(index);
    }

    public List<Integer> getLengths() {
        return passages.stream().map(StringExpression::getValue).map(String::length).toList();
    }
    public List<String> getTexts() {
        return passages.stream().map(StringExpression::getValue).toList();
    }

}