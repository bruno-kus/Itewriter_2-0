package com.example.itewriter.area.arch;

import javafx.beans.property.Property;

public interface MagicProperty extends Property<String> {
    <T> T instantiate();
}
