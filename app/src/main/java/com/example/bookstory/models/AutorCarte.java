package com.example.bookstory.models;

import androidx.room.Entity;

@Entity(primaryKeys = {"idCarte", "idAutor"})
public class AutorCarte {
    private long idAutor;
    private long idCarte;

    public AutorCarte(long idAutor, long idCarte) {
        this.idAutor = idAutor;
        this.idCarte = idCarte;
    }

    public long getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(long idAutor) {
        this.idAutor = idAutor;
    }

    public long getIdCarte() {
        return idCarte;
    }

    public void setIdCarte(long idCarte) {
        this.idCarte = idCarte;
    }
}
