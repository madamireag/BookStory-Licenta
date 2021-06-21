package com.example.bookstory.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class CarteCuImprumut {
    @Embedded
    public Carte carte;
    @Relation(
            parentColumn = "idCarte",
            entityColumn = "idImprumut",
            associateBy = @Junction(ImprumutCarte.class)
    )
    public List<Carte> listaImprumuturiCarte;
}
