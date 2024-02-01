package com.example.itewriter.area.tightArea;

import com.example.itewriter.area.util.MyRange;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.SetChangeListener;
import javafx.util.Pair;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.StyleSpans;
import org.reactfx.util.Either;

import java.util.*;

import static com.example.itewriter.area.tightArea.MyArea.EITHER_OPS;

public class AreaController {
    MyArea area;
    {
//        area.getStyleSpans()
        StyleSpans
    }

    private final ManifestationModel manifestationModel;
    Property<Registry.Tag> selectedTagProperty;
    public final SimpleVariationSelector simpleVariationSelector;

    public AreaController(MyArea area, Registry registry) {
        this.area = area;
        final var tagIndexer = new TagIndexer(registry);
        manifestationModel = new ManifestationModel(tagIndexer, this);
        simpleVariationSelector = new SimpleVariationSelector(tagIndexer, selectedTagProperty);

        registry.allTags.addListener((SetChangeListener.Change<? extends Registry.Tag> change) -> {
            if (change.wasAdded()) {
                final var tag = change.getElementAdded();
                final var manifestation = new Manifestation(
                        Bindings.createObjectBinding(
                                () -> tag.getAllVariations().get(tagIndexer.getTagIndices().get(tag)),
                                tagIndexer.getTagIndices())
                );
                manifestationModel.observableManifestations.put(tag, manifestation);
            } else if (change.wasRemoved()) {
                manifestationModel.observableManifestations.remove(change.getElementRemoved());
            }
        });
        area.setOnKeyPressed(e -> {
            final var carPos = area.getCaretPosition();
            final var allPos = manifestationModel.getAllPositions();
            final var i = -(Collections.binarySearch(allPos, carPos) + 1);
            // znowu potrzebuję czegoś takiego jak taggedposition!
            // w ogóle mógłbym tutaj mieć listę manifestacji
            // albo może nawet snapshotów manifestacji!
//            if (carPos > allPos.get(i) && carPos < allPos.get(i)) +

            l1:
            for (final var entry : manifestationModel.observableManifestations.entrySet()) {
                final var passPos = entry.getValue().getPassagePositions();
                for (final var pos : passPos) {
                    if (pos == i) {
                        final var innerIndex = Collections.binarySearch(passPos, i);
                        final var start = allPos.get(i);
                        final var end = start + entry.getValue().getVariation().getTexts().get(innerIndex).length();
                        final var property = entry.getValue().getVariation().allPassages.get(innerIndex);
                        if () {
                            property.setValue(new StringBuilder(property.getValue())
                                    .insert(carPos - start, e.getText()).toString());
                        }
                        break l1;
                    }
                }
            }
            /*
            zmapować entry set manifestacji do czegoś jak
            pozycja -> tag, Variation
            czy manifestacja powinna mieć wewnątrz siebie taga
            mogę zamiast tego operować na entrisach!

            pozycję znalezioną w zgromadzeniu łatwo też znaleźć w konkretnej manifestacji (taki sam binSearch)
             */
        });
    }


    /**
     * jak na razie to podoba mi się umiejscowienie tej metody w kontrolerze
     * ponieważ będzie tworzyła segmenty oraz zamieniała też poprzednią narrację
     * ewentualnie mogę ją rozbić na dwa wywołania, gdzie strefa tylko uaktualnia, a
     * kontroler aktualizuje też pozycje
     * tylko, co z tego, że ja se zrobię multiChanges skoro nie mam tych multi changes odbitych...
     */


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
