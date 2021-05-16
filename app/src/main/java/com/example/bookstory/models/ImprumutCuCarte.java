package com.example.bookstory.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class ImprumutCuCarte {
    @Embedded
    public Imprumut imprumut;
    @Relation(
            parentColumn = "idImprumut",
            entityColumn = "idCarte",
            associateBy = @Junction(ImprumutCarte.class)
    )
    public List<Carte> listaCartiImprumut;
}
