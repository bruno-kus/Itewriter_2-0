package com.example.itewriter.area.boxview;

import com.example.itewriter.area.tightArea.Passage;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class BoxPaneView extends VBox {
    /*
    pytanie czy chciałbym abstrakcyjną klasę, która by miała prostą wewnętrzną reprezentację wbudowaną?
    chyba nie
    ale mogę zrobić interfejs :)
     */
    /* pytanie co robimy tutaj, bo faktycznie pudełko ma swój stan
    czy zostajemy przy propertiesach, no raczej tak
     */
    /**
     * podoba mi się wyświetlanie pasaży, bo dzięki temu mógłbym przestawiać je drag and drop
     * w jakieś reprezentacji co by było bardzo zaawansowanym feature'em :)
     */
    private final ObservableList<Passage> simpleInternalModel = new SimpleListProperty<>();
    public ObservableList<Passage> getSimpleInternalModelProperty() {
        return simpleInternalModel;
    }
    {
        /*
        i to poniższe zastępujemy
        odwołanie do Variation.bindContents i za każdym razem kiedy zmienia się wartość tutaj to
        ale to robię w wybieraku!
        w środku boxa zajmuję się tylko wyświetleniem pudeł
         */
        simpleInternalModel.addListener((ListChangeListener.Change<? extends Passage> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (var passage : change.getAddedSubList()) {
                        var field = new TextField();
                        field.textProperty().bindBidirectional(passage.textProperty());
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
