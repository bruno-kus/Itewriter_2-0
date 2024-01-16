package com.example.itewriter.area.tightArea;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ListPropertyBase;
import javafx.beans.property.ReadOnlyListWrapper;
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


    REJESTR MUSI BYĆ PODANY DO STREFY PUSTY
    A WIĘC MOŻE BYĆ PRZEZ NIĄ SAMĄ ZAINICJALIZOWANY
    czy rejestr powinien należeć do MyArea, MagicAreaController czy w ogóle do kogo?????
    zanim stworzę tag to muszę stworzyć go w stref
    kto tworzy segmenty w strefie, kiedy i jak?
    użytkownik, kiedy kliknie "write segment"
    co robi write segment?
    insertuje do GENERIC-STYLED-AREA nowy segment, jeżeli kursor nie jest na MYSEG albo zmienia wartość obecnego
    stojące za tym API to:
    insert segment, (odpowiedzialne również za normalizację w sensie padowanie)
    modify segment

    najpierw zmiana musi nastąpić w dokumencie a dopiero potem w boxView
    Registry przechowuje zbiór tagów obecnie używanych wewnątrz konkretnej strefy
    metoda InsertSegment MUSI GWARANTOWAĆ SYNCHRONIZACJĘ pomiędzy segmentami oraz Rejestrem
    co jest gorsze? tag bez segmentu czy segment bez tagu?
    segment bez tagu nie ma najmniejszego prawa się wyświetlić!

    czyli Area <- Registry <- Tag <- Segment

    czy chciałbym coś w stylu Tag.Segment -> czyli że każdy tag ma zbiór segów, może... ale chyba nie w ten sposób

    co innego tagi a co innego wariacje
    taga może dodać każdy, po prostu będzie on pusty
    pytanie właśnie brzmi czy czy Registry powinien trzymać pulę nieużywanych tagów z których użytkownik może
    sobie wybrać którego użyć czy też pownien służyć wyłącznie do pilnowania, które tagi są użyte w tabeli
    why not both :)

    jaki był dokładnie mój pomysł na użycie wewnętrznej klasy?
    że tylko klasa area jest w stanie używać registry
    to znaczy, że registry ma metodę addTag, której może używać tylko MyArea
    albo po prostu mogę sprawić, żeby registry w arenie było w jakiś sposób ukrywane albo w ogóle go nie pobierać
    I FOUND A REASON :)

    */
//    {
//        var r = new AreaRegistry();
//        r.new Tag();
//    }
}