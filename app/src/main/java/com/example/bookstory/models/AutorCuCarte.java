package com.example.bookstory.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class AutorCuCarte {
    @Embedded
    public Autor Autor;
    @Relation(
            parentColumn = "idAutor",
            entityColumn = "idCarte",
            associateBy = @Junction(AutorCarte.class)
    )
    public List<Carte> carti;
}
