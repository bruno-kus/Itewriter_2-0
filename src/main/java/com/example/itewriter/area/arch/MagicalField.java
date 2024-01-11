//package com.example.itewriter.area.arch;//package com.example.indocumentvariations.area.arch;
//
//import javafx.beans.InvalidationListener;
//import javafx.beans.property.Property;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.collections.ListChangeListener;
//import javafx.collections.ObservableList;
//import javafx.scene.control.TextField;
//
//
//public class MagicalField extends TextField implements MagicProperty {
//    public static MagicalField empty() {
//        return new MagicalField();
//    }
//
//    @Override
//    public <T> T instantiate() {
//        return (T) empty();
//    }
//    <A extends MagicProperty, B extends MagicProperty> void method(ObservableList<A> properties1, ObservableList<B> properties2) {
//        properties1.addListener((ListChangeListener.Change<? extends MagicProperty> change) -> {
//            while (change.next()) {
//                if (change.wasAdded()) {
//                    for (var stringProperty1 : change.getAddedSubList()) {
//                        MagicProperty stringProperty2 = stringProperty1.instantiate();
//                        stringProperty2.bindBidirectional(stringProperty1);
//                        properties2.add(stringProperty2);
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    public void bind(ObservableValue<? extends String> observable) {
//        textProperty().bind(observable);
//    }
//
//    @Override
//    public void unbind() {
//        textProperty().unbind();
//    }
//
//    @Override
//    public boolean isBound() {
//        return textProperty().isBound();
//    }
//
//    @Override
//    public void bindBidirectional(Property<String> other) {
//        textProperty().bindBidirectional(other);
//    }
//
//    @Override
//    public void unbindBidirectional(Property<String> other) {
//        textProperty().unbindBidirectional(other);
//    }
//
//    @Override
//    public Object getBean() {
//        return textProperty().getBean();
//    }
//
//    @Override
//    public String getName() {
//        return textProperty().getName();
//    }
//
//    @Override
//    public void addListener(ChangeListener<? super String> listener) {
//        textProperty().addListener(listener);
//    }
//
//    @Override
//    public void removeListener(ChangeListener<? super String> listener) {
//        textProperty().removeListener(listener);
//    }
//
//    @Override
//    public String getValue() {
//        return textProperty().getValue();
//    }
//
//    @Override
//    public void setValue(String value) {
//        textProperty().setValue(value);
//    }
//
//    @Override
//    public void addListener(InvalidationListener listener) {
//        textProperty().addListener(listener);
//    }
//
//    @Override
//    public void removeListener(InvalidationListener listener) {
//        textProperty().removeListener(listener);
//    }
//
//
//}
