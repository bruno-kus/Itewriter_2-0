package com.example.itewriter.area.boxview;

import com.example.itewriter.area.tightArea.SequentialVariationSelector;
import javafx.beans.binding.Bindings;

public class BoxPaneController {
    public final BoxPane boxPane;
    public final SequentialVariationSelector sequentialVariationSelector;
    public BoxPaneController(BoxPane boxPane, SequentialVariationSelector sequentialVariationSelector) {
        this.boxPane = boxPane;
        this.sequentialVariationSelector = sequentialVariationSelector;
        this.sequentialVariationSelector.selectedTagObservable().addListener((ob, ov, newSelectedTag) -> {
            Bindings.unbindContent(boxPane.getActiveVariation(), newSelectedTag.getActiveVariation());
            boxPane.getActiveVariation().clear();
            boxPane.getActiveVariation().addAll(newSelectedTag.getActiveVariation());
            Bindings.bindContent(boxPane.getActiveVariation(), newSelectedTag.getActiveVariation());
        });
    }
    public boolean nextTag() {
        return sequentialVariationSelector.nextTag();
    }
    public boolean previousTag() {
        return sequentialVariationSelector.previousTag();
    }
}
