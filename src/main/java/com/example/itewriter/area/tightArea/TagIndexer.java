package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.SetChangeListener;
import javafx.scene.control.Button;

public class TagIndexer {
    public ObservableValue<SimpleVariation> createBinding(Registry.Tag tag) {
        return Bindings.createObjectBinding(() -> tag.getVariationsProperty().get(tagIndices.get(tag)), tagIndices);
    }

    TagIndexer(Registry registry) {
        registry.allSimpleTags.addListener(new RegistryHandler());
    }
    {
        Button l;
    }

    class RegistryHandler implements SetChangeListener<Registry.Tag> {
        @Override
        public void onChanged(Change<? extends Registry.Tag> change) {
            if (change.wasAdded()) {
                var addedTag = change.getElementAdded();
                tagIndices.put(addedTag, addedTag.getVariationsProperty().isEmpty() ? -1 : 0);
            } else if (change.wasRemoved()) tagIndices.remove(change.getElementRemoved());
        }
    }

    public ObservableMap<Registry.Tag, Integer> tagIndices = FXCollections.observableHashMap();

    public ObservableMap<Registry.Tag, Integer> getTagIndices() {
        return tagIndices;
    }


    public void setIndex(Registry.Tag tag, Integer index) {
        if (index >= 0 && index < tag.getVariationsProperty().size()) tagIndices.put(tag, index);
        else throw new IllegalArgumentException();
    }

    public Integer getIndex(Registry.Tag tag) {
        return tagIndices.get(tag);
    }
}
