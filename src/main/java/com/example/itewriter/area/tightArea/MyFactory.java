package com.example.itewriter.area.tightArea;

import javafx.scene.Node;
import org.fxmisc.richtext.TextExt;
import org.fxmisc.richtext.model.StyledSegment;
import org.reactfx.util.Either;
import java.util.function.Function;

public class MyFactory implements Function<StyledSegment<Either<String, MySegment>, String>, Node> {
    @Override
    public Node apply(StyledSegment<Either<String, MySegment>, String> styledSegment) {
        var either = styledSegment.getSegment();
        var style = styledSegment.getStyle();
        TextExt text = new TextExt();
        if (either.isLeft()) {
            String string = either.getLeft();
            text.setText(string);
        }
        if (either.isRight()) {
            MySegment seg = either.getRight();
            text.setText(seg.getCurrentText());


//            text.backgroundColorProperty().bind(seg.getTag().colorProperty());
            // text.setBackgroundColor(seg.getTag().getColor())

            // jeszcze kwetsia tworzenia nowych segmentów
            // tworzenie segmentu nie zmienia nic dla tagu, zwłaszcza jeśli ten jest statycznie przypisany
            // TAGI NIE MOGĄ BYĆ STATYCZNE ponieważ zawierają odniesienie do modelu

            // Tag model to jest model wewnątrz tagowy -> to ma sens!
            // i do każdego tagu jest aktualnie przypisany któryś
            // czy model to dobre określenie

            // więc albo posługuję się mapą (kinda data oriented)
            // właściwie chyba tylko mapa wchodzi w grę
            /*
            jest jakiś twardy zbiór TAGów
            w każdym momencie jest wybrany aktualny tag (to kwestia kosmetyczna)
            są segmenty, które należą do tego tagu
            do każdego tagu może być przypisany różny stan
            na podstawie tego, który jest aktualnie przypisany określa się wartość każdego segmentu
            segment -> tag -> stan tagu
            current text ( tag state ( tag ) )

            zaczynam dostrzegać problem
            chcę móc w dowolnym momencie pisać tagiem
            żeby dodawanie nowych segmentów było bułką z masłem
            ale same tagi muszę stworzyć po uruchomieniu

            więc albo tworzę barebones strukturę podczas kompilacji, której stan następnie uzupełniam za pomocą map
            albo jednak jestem w stanie stworyć tagi po utworzeniu programu i swobodnie z nich korzystać...
            to bardzo dobre pytanie :)
             */
        }
        return text;
    }
}
/*
            text.setOnMouseEntered(e -> {
                text.setBackgroundColor(Color.GRAY);
            });
            text.setOnMouseExited(e -> {
                text.setBackgroundColor(Color);
            });
            mógłbym zrobić jakiś obiekt, który by zarządzał tym zachowaniem
 */