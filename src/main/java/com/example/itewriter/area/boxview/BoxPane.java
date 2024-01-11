package com.example.itewriter.area.boxview;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class BoxPane extends VBox {
    /*
    pomysł taki, żeby dosłownie zrobić najpierw BoxPane współpracujący z modelem, pomijając areę :)
    no powiedzmy, że to mniej więcej działa, aczkolwiek problem jest taki
    że powinienem jeszcze przywiązać do czegoś TextProperty pól tekstowych
    czyli zarówno zmieniać wartości pól jak i dodawać nowe
     */
    public ObservableList<StringProperty> texts = FXCollections.observableArrayList();
    private final ObservableList<TextField> fields = FXCollections.observableArrayList();
    {
        texts.addListener((ListChangeListener.Change<? extends StringProperty> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (var text : change.getAddedSubList()) {
                        var field = new TextField();
                        field.textProperty().bindBidirectional(text);
                        fields.add(field);
                    }
                }
                if (change.wasRemoved()) {
                    fields.remove(change.getFrom());
                }
            }
        });
        fields.addListener((ListChangeListener.Change<? extends TextField> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (var field : change.getAddedSubList()) {
                        var text = new SimpleStringProperty();
                        text.bindBidirectional(field.textProperty());
                        texts.add(text);
                    }
                    texts.addAll(change.getFrom(), change.getAddedSubList().stream()
                            .map(TextField::getText)
                            .map(SimpleStringProperty::new)
                            .toList());
                }
                if (change.wasRemoved()) {
                    texts.remove(change.getFrom());
                }
            }
        });
    }

    {
        Bindings.bindContent(getChildren(), fields); // działa jednostronnie!
    }
}
