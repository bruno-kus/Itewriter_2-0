//package com.example.itewriter.area.root;

import com.example.itewriter.area.tightArea.AreaController;
import com.example.itewriter.area.tightArea.MyArea;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.stream.Stream;

//public class ControlBarFactory {
//    @Retention(RetentionPolicy.RUNTIME)
//    @Target(ElementType.METHOD)
//    @interface ControlBar{
//
//    }
//    final AreaController areaController;
//    final MyArea area;
//
//    public ControlBarFactory(AreaController areaController, MyArea area) {
//        this.areaController = areaController;
//        this.area = area;
//    }
//
//    // inna opcja to coś w stylu
//    // Bars.color
//    // Bars.form
//    // ale to musi być fabryka, bo sama jest konstruowana, okej!
//    // jeżeli bym chciał optymalizować to mogę zrobić leniwe gettery <3
//    @ControlBar
//    public Node color() {
//        var colorBar = new HBox();
//        areaController.tagSelector.registry.availableTags.stream()
//                .map(tag -> new Button(tag.getColor().toString()))
//                .forEachOrdered(colorBar.getChildren()::add);
//        return colorBar;
//    }
//    @ControlBar
//    public Node form() {
//        Spinner<Integer> mySegmentIndexSpinner = new Spinner<>();
//        SpinnerValueFactory<Integer> mySegmentIndexValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0);
//        mySegmentIndexSpinner.setValueFactory(mySegmentIndexValueFactory);
//
//
//        Button cloneButton = new Button("clone");
//
//        TextField field = new TextField();
//
//        Button editButton = new Button("edit");
//
//
//        Button upVariationButton = new Button("▲");
//        Button downVariationButton = new Button("▼");
//
//
//        VBox variationButtons = new VBox(upVariationButton, downVariationButton);
//
//        return new HBox(mySegmentIndexSpinner, cloneButton, field, editButton, variationButtons);
//    }
//
//    @ControlBar
//    public Node controls() {
//        // na pewno chcę, żeby metoda stwarzała
//        // czy statyczna?
//        // chyba tak, pytanie czy tu, w managerze
//        /*
//        powinny być te statyczne metody w klasie, która ma dostęp do area
//        z zewnątrz chciałbym wywoływać nazwy kontrolek, które chcę w danym managerze
//        i pytanie czy dodaję je tylko raz, no raczej, że tak!
//        czyli Constructor
//        nie mogą być statyczne bo wymagana jest area!
//        i bardzo dobrze!
//        var cm = new ControlManager(area);
//        cm.add(cm.controls(), cm.form(), cm.print())
//        inna opcja to Control builder...
//
//        nie podoba mi się w cm.add(cm.controls()) to, że jeżeli dodaję do cm, tow wiem, że dodaję cm.controls
//
//        chyba, że statycznie controls(cm)
//
//        czy chcę dodawać bezpośrednio Node'y?
//        właściwie to tak
//        w ten sposób mogę łatwo dodawać inline'owo rzeczy
//
//        a co jeśli jest ControlFactory cb i na nim używam
//        xx.addAll(cf.controls(), cf.form(), cf.print())
//        czym jest xx?
//        na chwilę obecną po prostu panelem
//         */
//        Button printButton = new Button("print all segments");
//        printButton.setOnAction(e -> {
//            System.out.printf("size -> %d", area.getAllSegments().size());
//            System.out.printf("area.getAllSegments():\n%s\n", area.getAllSegments());
//        });
//        Button indicatorButton = new Button("indicator");
//        Button mySegmentButton = new Button("MySegment");
////                mySegmentButton.setOnAction(e -> area.replaceSelectionWithMySegment());
//        return new HBox(mySegmentButton, printButton, indicatorButton);
//    }
//
//    public Collection<Node> all() {
//        return Stream.of(getClass().getMethods())
//                .filter(method -> method.isAnnotationPresent(ControlBar.class))
//                .map(m -> {
//                    try {
//                        return (Node) m.invoke(this);
//                    } catch (IllegalAccessException | InvocationTargetException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//                .toList();
//    }
//}
