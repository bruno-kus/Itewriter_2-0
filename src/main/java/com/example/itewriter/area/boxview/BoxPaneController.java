package com.example.itewriter.area.boxview;

import com.example.itewriter.area.tightArea.TagSelector;
import javafx.beans.binding.Bindings;

public class BoxPaneController {
    public final BoxPane boxPane;
    public final TagSelector tagSelector;

    public BoxPaneController(BoxPane boxPane, TagSelector tagSelector) {
        this.boxPane = boxPane;
        this.tagSelector = tagSelector;
        this.tagSelector.selectedTagProperty().addListener((ob, ov, nv) -> {
            Bindings.unbindContent(boxPane.texts, nv.getActiveVariation());
            boxPane.texts.clear();
            boxPane.texts.addAll(nv.getActiveVariation());
            Bindings.bindContent(boxPane.texts, nv.getActiveVariation());
        });

    }
}
