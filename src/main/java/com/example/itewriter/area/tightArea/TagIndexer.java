package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.SetChangeListener;

public class TagIndexer {
    public ObservableValue<Variation> createBinding(Registry.Tag tag) {
        return Bindings.createObjectBinding(() -> tag.getAllVariations().get(tagIndices.get(tag)), tagIndices);
    }
    TagIndexer(Registry registry) {
        registry.allTags.addListener((SetChangeListener.Change<? extends Registry.Tag> change) -> {
            if (change.wasAdded()) {
                var tag = change.getElementAdded();
                if (tag.allVariations.isEmpty()) tagIndices.put(change.getElementAdded(), -1);
                else tagIndices.put(change.getElementAdded(), 0);
            } else if (change.wasRemoved()) tagIndices.remove(change.getElementRemoved());
        } );
    }
    public ObservableMap<Registry.Tag, Integer> tagIndices = FXCollections.observableHashMap();

    public ObservableMap<Registry.Tag, Integer> getTagIndices() {
        return tagIndices;
    }


    public void setIndex(Registry.Tag tag, Integer index) {
        if (index >= 0 && index < tag.allVariations.size()) tagIndices.put(tag, index);
        else throw new IllegalArgumentException();
    }

    public Integer getIndex(Registry.Tag tag) {
        return tagIndices.get(tag);
    }
}
