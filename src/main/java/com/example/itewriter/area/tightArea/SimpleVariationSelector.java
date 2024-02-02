package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;

public class SimpleVariationSelector {
    final TagIndexer tagIndexer;
    final ObservableValue<Registry.Tag> selectedTag;
    final ObservableValue<SimpleVariation> selectedVariation;

    SimpleVariationSelector(TagIndexer tagIndexer, ObservableValue<Registry.Tag> selectedTag) {
        this.tagIndexer = tagIndexer;
        this.selectedTag = selectedTag;
        selectedVariation = Bindings.createObjectBinding(
                () -> selectedTag.getValue().getAllSimpleVariationsProperty().get(tagIndexer.getIndex(selectedTag.getValue())),
                selectedTag, tagIndexer.getTagIndices()
        );
    }

    public ObservableValue<SimpleVariation> selectedVariationObservable() {
        return selectedVariation;
    }

    public ObservableValue<SimpleVariation> selectedVariationProperty() {
        return selectedVariation;
    }

    public boolean nextVariation() {
        final var tag = selectedTag.getValue();
        final var tagIndex = tagIndexer.getIndex(tag);
        if (tagIndex < tag.getAllSimpleVariationsProperty().size() - 1) {
            tagIndexer.setIndex(tag, tagIndex + 1);
            return true;
        } else
            return false;
    }

    public boolean previousVariation() {
        final var tag = selectedTag.getValue();
        final var tagIndex = tagIndexer.getIndex(tag);
        if (tagIndex > 1) {
            tagIndexer.setIndex(tag, tagIndex - 1);
            return true;
        } else
            return false;
    }
}
