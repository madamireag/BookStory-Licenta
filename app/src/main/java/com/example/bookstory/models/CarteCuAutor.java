package com.example.bookstory.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class CarteCuAutor {
    @Embedded
    public Carte carte;
    @Relation(
            parentColumn = "idCarte",
            entityColumn = "idAutor",
            associateBy = @Junction(AutorCarte.class)
    )
    public List<Autor> autori;

}
