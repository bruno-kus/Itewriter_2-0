package com.example.itewriter.area.tightArea;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import java.util.*;

public class Variation {
    final ObservableList<Passage> passages = new SimpleListProperty<>();
    {
        // ten blok nie należy tutaj do klasy
        // rzecz do zrobienia to zastanowić się kto będzie wiązał ze sobą wariację tak
        // żeby uzupełniały puste segmenty :)
        passages.addListener((ListChangeListener.Change<? extends Passage> change) -> {
            var allVariationsOfThisTag = new ArrayList<Variation>();
            while (change.next()) {
                if (change.wasAdded()) {
                    for (var v : allVariationsOfThisTag) {
                        change.getAddedSubList().forEach(v::addPassage);
                    }
                }
            }
        });
    }
    /*
    hash mapa jest tak diabelnie szybka, że nawet jeżeli chcemy modyfikować dane to bardziej opłaca się
    dodawać nowe do hashmapy a usuwać stare
     */
    private final ObservableMap<Integer, Passage> passageMap = FXCollections.observableHashMap();
    private Passage getPassage(int position) {
        return passageMap.get(position);
    }
    {
        passages.addListener((ListChangeListener.Change<? extends Passage> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (var p : change.getAddedSubList()) {
                        p.positionProperty().addListener((ob, ov, nv) -> {
                            passageMap.remove((Integer) ov);
                            passageMap.put((Integer) nv, p);
                        });
                    }
                }
                if (change.wasRemoved()) {
                    for (var p : change.getRemoved()) {
                        passageMap.remove(p.getPosition());
                    }
                }
            }
        });
    }



    /*
    moje opcje to albo lista która będzie sieczką ale przyozdobiona posortowaniem albo zwykła lista i własne api
     */




    void modifySomeValue(Passage passage, String text) {
        offsetPositions(passage, text.length() - passage.text.getValue().length());
        passage.text.setValue(text);
    }

    /**
     * to jest powód dla którego mam listę, a nie zbiór
     * to oraz fakt, że zbiorów nie wypada modyfikować
     * podoba mi się ta logika
     * lista modyfikowalnych
     * zbiór niemodyfikowalnych
     */
    public void offsetPositions(Passage passage, int offset) {
        for (int i = Collections.binarySearch(passages, passage) + 1; i < passages.size(); i++) {
            passages.get(i).positionProperty().setValue(passage.getPosition() + offset);
        }
    }

    /**
     * zupełnie nie potrzebuję drzewa, żeby wstawiać elementy na właściwe pozycje!
     */
    private void addPassage(Passage passage) {
        var list = new SimpleListProperty<Passage>();
        list.add(Collections.binarySearch(list, passage), passage);
    }

    public void addPassage(int position, String text) {
        addPassage(new Passage(position, text));

    }

    public void addPassage(int position) {
        addPassage(position, "");
    }

    void bindObservableList(ObservableList<Passage> passages) {
        for (Passage p : passages) {
            p.textProperty().addListener((ob, ov, nv) -> this.modifySomeValue(this.getPassage(p.getPosition()), nv)
//                this.modifySomeValue(
//                        this.passages.get(Collections.binarySearch(passagePositions, p.getPosition())),
//                        nv
//                );
            );
        }


    }
}

