package com.example.itewriter.area.tightArea;

import javafx.beans.property.SimpleStringProperty;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.reactfx.util.Either;

import static com.example.itewriter.area.tightArea.MyArea.EITHER_OPS;


public class AreaController {
    MyArea area;
    public final SequentialVariationSelector sequentialVariationSelector;

    public AreaController(MyArea area, SequentialVariationSelector sequentialVariationSelector) {
        this.area = area;
        this.sequentialVariationSelector = sequentialVariationSelector;

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
        Either<String, MySegment> right = Either.right(new MySegment(tag, sequentialVariationSelector));
        area.replaceSelection(ReadOnlyStyledDocument.fromSegment(
                right,
                null,
                null,
                EITHER_OPS));

        var inserted = area.getTagSegments(tag).stream().filter(seg -> seg.getInVariationIndex() == -1).findFirst();
        if (inserted.isPresent()) {
            int i = area.getTagSegments(tag).indexOf(inserted.get());
            sequentialVariationSelector.getSelectedTag().ifPresent(t ->
                    t.getActiveVariation().add(i, new SimpleStringProperty(area.getText(area.getSelection()))));
        }
    }

    public void replaceWithMySegment(int start, int end, Registry.Tag tag) {
        // tutaj uwierzytelniam
        if (sequentialVariationSelector.tags.contains(tag))
            area.replace(start, end, ReadOnlyStyledDocument.fromSegment(
                    Either.right(new MySegment(tag, sequentialVariationSelector)),
                    null,
                    "",
                    EITHER_OPS
            ));
        else throw new RuntimeException();
    }
}
