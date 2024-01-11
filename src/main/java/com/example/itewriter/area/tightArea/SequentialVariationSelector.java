package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;

import java.util.Optional;

public class SequentialVariationSelector {

    public final ObservableList<Registry.Tag> tags = FXCollections.observableArrayList();
    private final IntegerProperty currentIndex = new SimpleIntegerProperty(-1);
    private final ObservableValue<Registry.Tag> selectedTag = Bindings.createObjectBinding(
            () -> {
                if (currentIndex.getValue() > 0 && currentIndex.getValue() < tags.size())
                    return tags.get(currentIndex.getValue());
                else
                    return null;
            },
            currentIndex, tags
    );
    /*
    a co jeśli chcę mieć TagSelector, który obsługuje random access
    w takim wypadku TagSelector powinien być interfejsem

     */

    public boolean nextTag() {
        if (currentIndex.getValue() < tags.size() - 1) {
            currentIndex.setValue(currentIndex.getValue() + 1);
            return true;
        } else {
            return false;
        }
    }

    public boolean previousTag() {
        if (currentIndex.getValue() > 0) {
            currentIndex.setValue(currentIndex.getValue() - 1);
            return true;
        } else {
            return false;
        }
    }

    public Optional<Registry.Tag> getSelectedTag() {
        return Optional.ofNullable(selectedTag.getValue());
    }

    public ObservableValue<Registry.Tag> selectedTagObservable() {
        return selectedTag;
    }

    public SequentialVariationSelector(Registry registry) {
        registry.availableTags.addListener(
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
}
