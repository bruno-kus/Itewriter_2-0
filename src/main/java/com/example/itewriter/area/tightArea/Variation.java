package com.example.itewriter.area.tightArea;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import java.util.*;

public class Variation {
    public final ObservableList<Passage> allPassages = new SimpleListProperty<>();
    {
        // ten blok nie należy tutaj do klasy
        // rzecz do zrobienia to zastanowić się kto będzie wiązał ze sobą wariację tak
        // żeby uzupełniały puste segmenty :)
        allPassages.addListener((ListChangeListener.Change<? extends Passage> change) -> {
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
    public Passage getPassage(int position) {
        return passageMap.get(position);
    }
    {
        allPassages.addListener((ListChangeListener.Change<? extends Passage> change) -> {
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




    /**
     * to jest powód dla którego mam listę, a nie zbiór
     * to oraz fakt, że zbiorów nie wypada modyfikować
     * podoba mi się ta logika
     * lista modyfikowalnych
     * zbiór niemodyfikowalnych
     */
//    public void offsetPositions(Passage passage, int offset) {
//        for (int i = Collections.binarySearch(allPassages, passage) + 1; i < allPassages.size(); i++) {
//            allPassages.get(i).positionProperty().setValue(passage.getPosition() + offset);
//        }
//    }
    public void offsetPositions(int position, int offset) {
        // wszystkie taki, których pozycja jest potem
        for (var p : allPassages) {
            if (p.getPosition() > position) {
                p.positionProperty().setValue(p.getPosition() + offset);
            }
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


}

