package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;

import java.util.Optional;

/**
 * to też powinno pozwolić ustawiać index tak jak mi się podoba i jak coś to kontroler waliduje indexowanie
 */
public class SequentialTagSelector {
    /**
     * tags jest listą w przeciwieństwie do zbioru w Registry
     * każdy Selector może mieć inną strukturę danych
     */
    public final ObservableList<Registry.Tag> tags = FXCollections.observableArrayList();
    public final IntegerProperty currentIndex = new SimpleIntegerProperty(-1);
    private final ObservableValue<Registry.Tag> selectedTag = Bindings.createObjectBinding(
            () -> {
                if (currentIndex.getValue() > 0 && currentIndex.getValue() < tags.size())
                    return tags.get(currentIndex.getValue());
                else
                    return null;
            },
            currentIndex, tags
    );

    //    private final ObservableListValue<StringProperty> currentVariation = Bindings.createObjectBinding(
//            () -> {
//                selectedTag.getValue().currentVariationIndex
//            },
//
//    );
    public SequentialTagSelector(Registry registry) {
        registry.allTags.addListener(
                (SetChangeListener.Change<? extends Registry.Tag> change) -> {
                    if (change.wasAdded()) tags.add(change.getElementAdded());
                    else if (change.wasRemoved()) tags.add(change.getElementRemoved());
                }
        );
//        if (!registry.availableTags.isEmpty()) {
//            tags.addAll(registry.availableTags);
        currentIndex.setValue(0);
//        }
    }

    public Optional<Registry.Tag> getSelectedTag() {
        return Optional.ofNullable(selectedTag.getValue());
    }

    public ObservableValue<Registry.Tag> selectedTagObservable() {
        return selectedTag;
    }

    public void setIndex(Integer index) {
        if (index >= 0 && index < tags.size()) currentIndex.setValue(index);
        else throw new IllegalArgumentException();
    }
}
