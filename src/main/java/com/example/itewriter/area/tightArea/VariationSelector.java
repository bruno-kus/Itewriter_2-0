package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.Optional;

public class VariationSelector {
    /*
    albo mogę zrobić, że jest tyle variation selecorów ile tagów
    i każdy odnosi się do swojego tag selectora
    albo operować na tej mapie

    operowanie na mapie jest proste
    po prostu podnoszę indeks dla tego który selector aktualnie ma wybrany :)
     */
    public ObservableMap<AreaRegistry.Tag, Integer> currentIndices = FXCollections.observableHashMap();

    private final ObservableValue<ObservableList<StringProperty>> selectedVariation;
    public VariationSelector(SequentialTagSelector tagSelector) {
        selectedVariation =
                Bindings.createObjectBinding(
                        () -> {
                            final var optional = tagSelector.getSelectedTag();
                            if (optional.isPresent()) {
                                final var tag = optional.get();
                                return tag.variations.get(currentIndices.get(tag));
                            } else
                                return null;
                        },
                        tagSelector.selectedTagObservable(), currentIndices
                );
    }
    public Optional<ObservableList<StringProperty>> getSelectedVariation() {
        return Optional.ofNullable(selectedVariation.getValue());
    }
    public ObservableValue<ObservableList<StringProperty>> getSelectedVariationObservable() {
        return selectedVariation;
    }
    public void setIndex(AreaRegistry.Tag tag, Integer index) {
        if (index >= 0 && index < tag.variations.size()) currentIndices.put(tag, index);
        else throw new IllegalArgumentException();
    }
    public Integer getIndex(AreaRegistry.Tag tag) {
        return currentIndices.get(tag);
    }
}
