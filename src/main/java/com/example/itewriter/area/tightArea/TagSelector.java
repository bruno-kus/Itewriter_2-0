package com.example.itewriter.area.tightArea;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class TagSelector {
// z tego można by zrobić interface może...
    // zaletą AreaModel w oddzielnej klasie jest enkapsulacja
    // AreaModel w ogóle nie potrzebuje pola area :)

    ObjectProperty<Registry.Tag> selectedTag = new SimpleObjectProperty<>();

    public Registry.Tag getSelectedTag() {
        return selectedTag.get();
    }

    public ObjectProperty<Registry.Tag> selectedTagProperty() {
        return selectedTag;
    }

    public void setSelectedTag(Registry.Tag selectedTag) {
        this.selectedTag.set(selectedTag);
    }

    public TagSelector() {
    }
}
