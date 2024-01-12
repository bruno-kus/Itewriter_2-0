package com.example.itewriter.area.tightArea;

import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.List;

public class MagicAreaController {
    // dla każdego tagu jest lista segmentów?
    // albo nie dla każdego tagu, tylko dla dowolnej mniejszej liczby
    List<ObservableList<StringProperty>> currentlyDisplayedSegments;
    /*
    teraz pytanie czy same w sobie tagi powinny zawierać puste elementy czy też
    padding powinien następować na poziomie segmentów
    to jest bardzo dobre pytanie!
    ogólnie to powinien występować na poziomie segmentów

    aczkolwiek tagi mają to do siebie, że wariacje nie są arbitralne tak do końca
    nie mogę dodać wariacji tak po prostu do Registry.Tag,
    bo wtedy nie będę wiedział jak mam je wyświetlić na arei
    chyba, że to nie jest prawda
    bo mogę o tym myśleć w ten sposób, że wariacja to lista stringów
    która zawsze się wyświetli na arei po kolei
    tylko teraz pytanie
    czy powinienem mieć stricte kwadratową tablicę w samych tagach???
    jak by to wyglądało na różnych widokach
    to ma chyba sens, bo jeżeli w jednym miejscu danego tagu nie mam nic

    więc teoria jest taka, że ustalamy w Registry.Tag automatyczne zarządzanie wariacjami
    każdy tag nie ma zatem dwuwymiarowej obserwowalnej listy, lecz VariationManager'a, który
    upewnia się, że tabela wariacji jest kwadratowa

    powinienem tak czy siak rozważyć use-case'y różnych widoków
    dodanie segmentu w strefie może zostać oddane w tabeli, ale dodanie w tabeli nie może zostać oddane w strefie
    modyfikowanie może działać obustronnie bez żadnego problemu

    czyli na chwilę obecną sam boxpane nie ma prawa dodawać segmentów
    jak to rozumieć, że tylko strefa może dodawać
    Rejestr bierze się ze strefy
    że najpierw jest strefa, potem rejestr
    czyli tylko strefa może tworzyć rejestr
    Area.Registry.Tag?
    ciekawe
    jaka jest alternatywa albo jakie są edgecase'y?
    rejestr do musi być podany pusty do strefy

     */
}