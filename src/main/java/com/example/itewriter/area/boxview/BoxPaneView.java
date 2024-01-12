package com.example.itewriter.area.boxview;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class BoxPaneView extends VBox {
    /* pytanie co robimy tutaj, bo faktycznie pudełko ma swój stan
    czy zostajemy przy propertiesach, no raczej tak
     */
    public ListProperty<StringProperty> activeVariation = new SimpleListProperty<>();
    public ObservableList<StringProperty> getActiveVariationProperty() {
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
