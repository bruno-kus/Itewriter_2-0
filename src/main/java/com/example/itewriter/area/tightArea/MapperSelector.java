package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;

import java.util.List;

public class MapperSelector {
    private final ObservableValue <SimpleVariation> selectedVariation;

    public SimpleVariation getSelectedVariation() {
        return selectedVariation.getValue();
    }

    public ObservableValue<SimpleVariation> selectedVariationObservable() {
        return selectedVariation;
    }
    /*
        chodzi po mapie i wybiera jedną z wariacji
         */
    TagIndexer tagIndexer;
    MapperSelector(TagIndexer tagIndexer) {
        this.tagIndexer = tagIndexer;
        selectedVariation = Bindings.createObjectBinding(
                () -> {
                    var tag = list.getValue().get(currentIndex.getValue());
                    return tag.getVariationsProperty().get(tagIndexer.getIndex(tag));
                },
                tagIndexer.tagIndices, currentIndex
        );
    }
    IntegerProperty currentIndex = new SimpleIntegerProperty();
    ObjectProperty<List<Registry.Tag>> list = new SimpleObjectProperty<>();
    {
//        list.bind(Bindings.createObjectBinding(
//                () -> variationMapper.currentIndices.keySet().stream().toList(),
//                variationMapper.currentIndices
//        ));
        tagIndexer.tagIndices.addListener(
                (MapChangeListener<? super Registry.Tag, ? super Integer>) change ->
                list.setValue(tagIndexer.tagIndices.keySet().stream().toList()));
        list.addListener(e -> {
            if (list.getValue().size() > 0) currentIndex.setValue(0);
            else currentIndex.setValue(-1);
                }
        );
    }
}
