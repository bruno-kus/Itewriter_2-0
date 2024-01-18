package com.example.itewriter.area.boxview;

import com.example.itewriter.area.tightArea.Passage;
import com.example.itewriter.area.tightArea.Registry;
import com.example.itewriter.area.tightArea.SequentialTagSelector;
import com.example.itewriter.area.tightArea.VariationSelector;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;

import java.util.Collections;

/**
 * moje pytanie brzmi:
 * czy jest sens instancjonować BoxPane view bez kontrolera?
 * czy może powinienem mieć jeszcze jeden poziom abstrakcji
 * czym może być wiele kontrolerów lub wiele boxPane'ów??
 */
public class BoxPaneSequentialController {
    /*
    registry -> tagSelector -> variationSelector
    tagSelector, variationSelector -> boxPaneController

    pytanie brzmi czy box pane ma prawo działać, jeżeli selectory nie są połączone
    wydaje mi się że powinien być całkowicie z enkapsulowany!
    BOX CONTROLLER tyczy się tylko boxa
    może nie mieć sensu w ogóle kontroler pojedynczej wariacji dla innych widoków jak choćby sama strefa!
     */

    public final BoxPaneView boxPaneView;
    private final VariationSelector variationSelector;
    private final SequentialTagSelector sequentialTagSelector;

    /*
    tutaj powinien być zarówno API do next tag, jak i do next variation
     */
    /*
    teraz mogę w jakiś zmyślny sposób opakować zarówno niskie api sterowania wariacją,
    jak i samym tagiem
    pytanie co jest potrzebne do jakiej zmiany, na których mi zależy oraz która klasa powinna tym się zajmować
     */
    public BoxPaneSequentialController(BoxPaneView boxPaneView, Registry registry) {
        // czy ta klasa powinna mieć properties'a, który byłby zbindowany
        this.boxPaneView = boxPaneView;
        this.variationSelector = new VariationSelector(sequentialTagSelector = new SequentialTagSelector(registry));
        this.variationSelector.getSelectedVariationObservable().addListener((variationProperty, oldVariation, newVariation) -> {
//            Bindings.unbindContent(boxPaneView.getActiveVariationProperty(), ov);
//            boxPaneView.getActiveVariationProperty().setAll(nv);
//            Bindings.bindContent(boxPaneView.getActiveVariationProperty(), nv);

//            nv.unbindObservableList(boxPaneView.getSimpleInternalModelProperty());
//            boxPaneView.getSimpleInternalModelProperty().setAll(nv.getPassages());
//            nv.bindObservableList(boxPaneView.getSimpleInternalModelProperty());
            for (var textFieldPassage : boxPaneView.getSimpleInternalModelProperty()) {
                var variationPassage = newVariation.getPassage(textFieldPassage.getPosition());
                textFieldPassage.textProperty().unbind();
                textFieldPassage.textProperty().setValue(variationPassage.textProperty().getValue());
                textFieldPassage.textProperty().bind(variationPassage.textProperty());
                textFieldPassage.textProperty().addListener((textProperty, oldText, newText) -> { // taki sam handler powinien być, kiedy dodawane są nowe elementy do wariacji
                    registry.offsetAllTags(textFieldPassage.getPosition(), newText.length() - textFieldPassage.textProperty().getValue().length());
                    newVariation.getPassage(textFieldPassage.getPosition()).textProperty().setValue(newText); // modyfikuje korespondującego indeksem taga
                });
            }
            // oprócz tego trzeba samą w sobie zawartość boxPane związać -> i można przy dodawaniu elementów wywoływac powyższe!


        });
    }

    @Buttonize
    public boolean nextTag() {
        var currentIndex = sequentialTagSelector.currentIndex.getValue();
        if (currentIndex < sequentialTagSelector.tags.size() - 1) {
            sequentialTagSelector.setIndex(currentIndex + 1);
            return true;
        } else
            return false;
    }

    @Buttonize
    public boolean previousTag() {
        var currentIndex = sequentialTagSelector.currentIndex.getValue();
        if (currentIndex > 0) {
            sequentialTagSelector.setIndex(currentIndex - 1);
            return true;
        } else
            return false;
    }

    @Buttonize
    public boolean nextVariation() {
        final var optionalTag = sequentialTagSelector.getSelectedTag();
        if (optionalTag.isPresent()) {
            final var tag = optionalTag.get();
            final var currentIndex = variationSelector.getIndex(tag);
            if (currentIndex < tag.allVariations.size() - 1) {
                variationSelector.setIndex(tag, currentIndex + 1);
                return true;
            }
        }
        return false;
    }

    @Buttonize
    public boolean previousVariation() {
        final var optionalTag = sequentialTagSelector.getSelectedTag();
        if (optionalTag.isPresent()) {
            final var tag = optionalTag.get();
            final var currentIndex = variationSelector.getIndex(tag);
            if (currentIndex > 0) {
                variationSelector.setIndex(tag, currentIndex + 1);
                return true;
            }
        }
        return false;
    }
}
