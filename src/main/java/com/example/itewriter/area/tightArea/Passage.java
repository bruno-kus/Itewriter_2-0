package com.example.itewriter.area.tightArea;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Passage implements Comparable<Passage>{
    public Passage(int position, String text) {
        this.position.setValue(position);
        this.text.setValue(text);
    }

    static void bindBidirectional(StringProperty stringProperty) {
        stringProperty.addListener(e -> {
        });
        /*
        on user action...
         */
    }

    StringProperty text = new SimpleStringProperty();

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    {

        // tylko czy to mi nie uruchomi jakieś kaskady???
        // ależ uruchomi!
        // czyli ta operacja musi być przeprowadzana na poziomie całej wariacji
        // jeden pasaż nie jest atomiczny!
    }

    @Override
    public int compareTo(Passage o) {
        return 0;
    }

    IntegerProperty position = new SimpleIntegerProperty();

    public int getPosition() {
        return position.get();
    }

    public IntegerProperty positionProperty() {
        return position;
    }


}
