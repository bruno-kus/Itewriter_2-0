package com.example.itewriter.area.tightArea;

import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;

import org.reactfx.util.*;

import java.util.*;

public class MyArea extends GenericStyledArea<Void, Either<String, MySegment>, String> {
    private static final TextOps<String, String> STYLED_TEXT_OPS = SegmentOps.styledTextOps();
    private static final MySegmentOps<String> MY_OPS = new MySegmentOps<>();
    static final TextOps<Either<String, MySegment>, String> EITHER_OPS = STYLED_TEXT_OPS._or(MY_OPS, (e1, e2) -> Optional.empty());

    /**
     * w konstruktorze powinien być rejestr tagów?
     */
    public MyArea() {
        super(
                null,
                (t, p) -> {
                },
                null,
                EITHER_OPS,
                e -> e.getSegment().unify(
                        TextExt::new,
                        mySegment -> {
                            return new TextExt(mySegment.getCurrentText());
                        })

        );
    }

    public List<MySegment> getMySegments() {
        return getAllSegments().stream().filter(Either::isRight).map(Either::getRight).toList();
    }

    public List<Either<String, MySegment>> getAllSegments() {
        return getDocument().subSequence(0, getLength()).getParagraphs().stream().
                flatMap(e -> e.getSegments().stream()).
                toList();
    }

    public List<MySegment> getTagSegments(Registry.Tag tag) {
        return getMySegments().stream().filter(seg -> seg.getTag().equals(tag)).toList();
    }
}
