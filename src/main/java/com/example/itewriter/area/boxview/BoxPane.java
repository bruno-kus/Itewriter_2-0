package com.example.itewriter.area.boxview;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class BoxPane extends VBox {

    public ObservableList<StringProperty> activeVariation = FXCollections.observableArrayList();
    public ObservableList<StringProperty> getActiveVariation() {
        return activeVariation;
    }
    {
        activeVariation.addListener((ListChangeListener.Change<? extends StringProperty> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (var text : change.getAddedSubList()) {
                        var field = new TextField();
                        field.textProperty().bindBidirectional(text);
                        getChildren().add(field);
                    }
                }
                if (change.wasRemoved()) {
                    getChildren().remove(change.getFrom());
                }
            }
        });
    }
}
