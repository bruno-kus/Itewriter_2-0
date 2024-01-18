package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class MySegment {
    /*
    okej
    i problem jest w tym, że obydwa segmenty dzielą listę
    nie myślałem o tym w ten sposób
    tak jakbym znowu potrzebował śledzenia zmian...

    tak!
    że 2 krotnie odnoszę się do tego który dzielę
    ale żeby stworzyć podsegment modyfikuję listę którą dzielą obydwa!
     */
//    Tag tag; // powinien być final ale weź ogarnij konstruktor, musiałbym zrobić, żeby empty był nieco inteligentniejszy
    Registry.Tag masterTag;
    IntegerProperty inVariationIndex = new SimpleIntegerProperty(-1);
    StringBinding currentText;

    static MySegment empty() {
        return new MySegment();
    }
    private MySegment() {

    }
    /*
    każdy tag może należeć do jakiegoś modelu!
    wtedy mogę mieć dwa takie same tagi, ale należące do różnych modeli
    i to bardzo dobrze!
    jaka jest niby tego zaleta?
    że jeden kolor może być w różnych miejscach
    ale teraz też to samo osiągnąłem, po prostu porównując tagi na podstawie adresu :)
     */
    public MySegment(Registry.Tag masterTag, SequentialTagSelector sequentialTagSelector) {
        this.masterTag = masterTag;
        currentText = Bindings.createStringBinding(
                // getValue jest nieco bardziej ekspresyjne w przeciwieństwie do samego get
                () -> masterTag.getActiveVariation().get(inVariationIndex.getValue()).getValue(),
                masterTag.activeVariationProperty(), inVariationIndex
        );
    }
    public String getCurrentText() {
        return currentText.get();
    }
    public int getInVariationIndex() {
        return inVariationIndex.get();
    }
    public IntegerProperty inVariationIndexProperty() {
        return inVariationIndex;
    }
    public void setCurrentIndex(int index) {
        inVariationIndexProperty().setValue(index);
    }
    @Override
    public String toString() {
        return "MySegment{" +
                ", currentIndex=" + inVariationIndex +
//                ", currentText=" + currentText +
                ", getCurrentText()=" + getCurrentText() +
                '}';
    }

    public Registry.Tag getTag() {
        return masterTag;
    }
}
