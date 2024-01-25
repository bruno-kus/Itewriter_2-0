package com.example.itewriter.area.tightArea;

import com.example.itewriter.area.util.MyRange;
import javafx.beans.property.*;
import javafx.util.Pair;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.reactfx.util.Either;

import java.util.*;

import static com.example.itewriter.area.tightArea.MyArea.EITHER_OPS;

public class AreaController {
    MyArea area;
    private final ManifestationModel manifestationModel;
    Property<Registry.Tag> selectedTagProperty;
    public final SimpleVariationSelector simpleVariationSelector;
    public AreaController(MyArea area, Registry registry) {
        this.area = area;
        final var tagIndexer = new TagIndexer(registry);
        manifestationModel = new ManifestationModel(registry, tagIndexer);
        simpleVariationSelector = new SimpleVariationSelector(tagIndexer, selectedTagProperty);


    }

    private void putToManifestVariations(Registry.Tag tag) {
        var manifestVariation = new SimpleObjectProperty<>(); // uzależnić od Variation selectora jakiegoś :)
        manifestVariation.addListener((ob, ov, nv) -> {
//            area.createMultiChange() // TODO
            area.createMultiChange().commit();
        });
        manifestVariations.put(tag, manifestVariation);
    }

    /**
     * jak na razie to podoba mi się umiejscowienie tej metody w kontrolerze
     * ponieważ będzie tworzyła segmenty oraz zamieniała też poprzednią narrację
     * ewentualnie mogę ją rozbić na dwa wywołania, gdzie strefa tylko uaktualnia, a
     * kontroler aktualizuje też pozycje
     * tylko, co z tego, że ja se zrobię multiChanges skoro nie mam tych multi changes odbitych...
     */
    public static void multiChange(
            MyArea area, List<Pair<MyRange, String>> oldValues, List<Pair<MyRange, String>> newValues) {
        if (oldValues.size() != newValues.size()) throw new IllegalArgumentException();
        var builder = area.createMultiChange();
        for (int i = 0; i < oldValues.size(); i++) {
            var oldRange = oldValues.get(i).getKey();
            var newText = newValues.get(i).getValue();
            builder.replaceText(oldRange.start, oldRange.end, newText);
        }
        /*
        commit wykonuje zmiany po sobie tak zupełnie zwyczajnie
        i jest tak jak myślę, że wywoływanie replace dodaje polecenie na absolutnych wartościach
        do listy, która niestety jest prywatna
        najlepszy sposób to samemu zaimplementować mój replacer
        przy okazji od razu będzie aktualizował pozycje
        aczkolwiek
         */
        builder.commit();
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
