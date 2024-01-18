package com.example.itewriter.area.tightArea;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.reactfx.util.Either;

import static com.example.itewriter.area.tightArea.MyArea.EITHER_OPS;


public class AreaController {
    MyArea area;
    /**
     * może być zarówno podane przez konstruktor, jeśli chcę synchronizacji jak i utworzone nowe
     */
    public final SequentialTagSelector sequentialTagSelector;

    public AreaController(MyArea area, Registry registry, SequentialTagSelector sequentialTagSelector) {
        this.area = area;
        this.sequentialTagSelector = sequentialTagSelector;

        this.area.setOnKeyPressed(key -> {
            registry.offsetAllTags(area.getCaretPosition(), key.getText().length());
            /*
            mega ważne! zmiany w pasażach powinny dotyczyć tylko tej samej wariacji
            zmiany w tekście nienależącym do nikogo powinny być we wszystkich wprowadzone
            ale jest coś takiego, że z perspektywy wariacji zmiana w innej wariacji
            niczym się nie różni od zmiany w czystym tekście!
             */
        });

    }


    public void writeMySegment(Registry.Tag tag) {
        area.setOnKeyPressed(k -> insertMySegment(area.getCaretPosition(), tag));
    }

    public void insertMySegment(int position, Registry.Tag tag) {
        replaceWithMySegment(position, position, tag);
    }

    private void replaceSelectionWithMySegment(Registry.Tag tag) {
        System.out.println("MyArea::replaceSelectionWithMySegment");

        /*
        najpierw bym dodawał segment pusty
        a następnie patrzył, gdzie też ten pusty został dodany!
         */
        // dodaj do modelu najpierw?
        Either<String, MySegment> right = Either.right(new MySegment(tag, sequentialTagSelector));
        area.replaceSelection(ReadOnlyStyledDocument.fromSegment(
                right,
                null,
                null,
                EITHER_OPS));

        var inserted = area.getTagSegments(tag).stream().filter(seg -> seg.getInVariationIndex() == -1).findFirst();
        if (inserted.isPresent()) {
            int i = area.getTagSegments(tag).indexOf(inserted.get());
            sequentialTagSelector.getSelectedTag().ifPresent(t ->
                    t.getActiveVariation().add(i, new SimpleStringProperty(area.getText(area.getSelection()))));
        }
    }

    public void replaceWithMySegment(int start, int end, Registry.Tag tag) {
        // tutaj uwierzytelniam
        if (sequentialTagSelector.tags.contains(tag))
            area.replace(start, end, ReadOnlyStyledDocument.fromSegment(
                    Either.right(new MySegment(tag, sequentialTagSelector)),
                    null,
                    "",
                    EITHER_OPS
            ));
        else throw new RuntimeException();
    }
}
