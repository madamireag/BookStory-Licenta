package com.example.bookstory.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "autori")
public class Autor {
    @PrimaryKey(autoGenerate = true)
    private long idAutor;
    private String nume;

    public Autor(long idAutor, String nume) {
        this.idAutor = idAutor;
        this.nume = nume;
    }

    @Ignore
    public Autor(String nume) {
        this.nume = nume;
    }


    public long getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(long idAutor) {
        this.idAutor = idAutor;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }
}
