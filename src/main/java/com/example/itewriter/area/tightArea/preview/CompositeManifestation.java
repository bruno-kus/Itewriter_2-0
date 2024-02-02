package com.example.itewriter.area.tightArea.preview;

import com.example.itewriter.area.tightArea.Manifestation;
import com.example.itewriter.area.tightArea.SimpleVariation;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class CompositeManifestation implements IManifestation {
    static CompositeManifestation composeManifestations(Manifestation manif1, Manifestation manif2) {
        // merge them!
        record ManifestationPosition(Manifestation manif, int pos) {
        }
        var c = manif1.getPassagePositions().stream()
                .map(pos -> new ManifestationPosition(manif1, pos))
                .toList();
        var v = manif2.getPassagePositions().stream()
                .map(pos -> new ManifestationPosition(manif2, pos))
                .toList();
        var sortedManifPos = Stream.concat(c.stream(), v.stream())
                .sorted(Comparator.comparing(ManifestationPosition::pos))
                .toList();
        // dzięki temu wiem jak iterować po elementach wariacji :)
        var result = new ArrayList<StringProperty>();
        var manif1It = manif1.getVariation().allPassages.iterator();
        var manif2It = manif2.getVariation().allPassages.iterator();
        for (var it : sortedManifPos)
            if (it.manif.equals(manif1)) result.add(manif1It.next());
            else result.add(manif2It.next());
//        int i = 0;

        /*
        pytanie czy przechowuję coś takiego?
        czy zlecam za każdym razem?
        musiałbym nadpisać obserwowalną listę która by delegowała wszystko do dwóch
        co jest na pozycji x?
        zmerge'ujmy obydwie listy by to sprawdzić!
        byśmy merge'owali aż nadeszła by ta pozycja
         */
        var h = new CompositeManifestation(result, List.of(manif1, manif2));
        sortedManifPos.forEach(mp -> h.passagePositions.add(mp.pos));
        return h;
    }
    static Collection<Manifestation> decomposeCompositeManifestation(
            CompositeManifestation composite) {
        return composite.components;
    }

    public CompositeManifestation(List<StringProperty> passages, Collection<Manifestation> components) {
        this.passages = passages;
        this.components = components;
    }

    // w zasadzie to spoko bo propertisy po prostu prześwitują
    // pytanie co zrobić z pozycjami
    // potrzbne jest decompose!
    final List<StringProperty> passages;
    final Collection<Manifestation> components;
    List<Integer> passagePositions;

/*
komponować musiałbym dla całej kolumny
gdzie znajduje się kolumna?

w rejestrze
w modelu manifestacji
w indekserze?
 */


    @Override
    public ObservableValue<IVariation> variationObservable() {


        return new SimpleObjectProperty<>(new IVariation() {
            int i = 9;
            @Override
            public ObservableList<StringProperty> allPassagesProperty() {
                return null;
            }


        });
    }

}
