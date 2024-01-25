package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;

public class SimpleVariationSelector {
    final TagIndexer tagIndexer;
    final ObservableValue<Registry.Tag> selectedTag;
    final ObservableValue<Variation> selectedVariation;

    SimpleVariationSelector(TagIndexer tagIndexer, ObservableValue<Registry.Tag> selectedTag) {
        this.tagIndexer = tagIndexer;
        this.selectedTag = selectedTag;
        selectedVariation = Bindings.createObjectBinding(
                () -> selectedTag.getValue().getAllVariations().get(tagIndexer.getIndex(selectedTag.getValue())),
                selectedTag, tagIndexer.getTagIndices()
        );
    }

    public ObservableValue<Variation> selectedVariationObservable() {
        return selectedVariation;
    }

    public ObservableValue<Variation> selectedVariationProperty() {
        return selectedVariation;
    }

    public boolean nextVariation() {
        final var tag = selectedTag.getValue();
        final var tagIndex = tagIndexer.getIndex(tag);
        if (tagIndex < tag.getAllVariations().size() - 1) {
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