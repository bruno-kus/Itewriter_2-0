package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
    public ObservableMap<Registry.Tag, Integer> currentIndices = FXCollections.observableHashMap();

    //    private final ObservableValue<ObservableList<StringProperty>> selectedVariation;
    private final ObservableValue<SimpleVariation> selectedVariation;


    public VariationSelector(TagSelector tagSelector) {
        selectedVariation =
                Bindings.createObjectBinding(
                        () -> {
                            final var optional = tagSelector.getSelectedTag();
                            if (optional.isPresent()) {
                                final var tag = optional.get();
                                return tag.allSimpleVariations.get(currentIndices.get(tag));
                            } else
                                return null;
                        },
                        tagSelector.selectedTagObservable(), currentIndices
                );
    }

    public Optional<SimpleVariation> getSelectedVariation() {
        return Optional.ofNullable(selectedVariation.getValue());
    }

    public ObservableValue<SimpleVariation> getSelectedVariationObservable() {
        return selectedVariation;
    }

    public void setIndex(Registry.Tag tag, Integer index) {
        if (index >= 0 && index < tag.allSimpleVariations.size()) currentIndices.put(tag, index);
        else throw new IllegalArgumentException();
    }

    public Integer getIndex(Registry.Tag tag) {
        return currentIndices.get(tag);
    }
}
