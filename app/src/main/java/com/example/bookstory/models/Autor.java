package com.example.bookstory.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "autori")
public class Autor implements Serializable {
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

    @Override
    public String toString() {
        return "Autor{" +
                "idAutor=" + idAutor +
                ", nume='" + nume + '\'' +
                '}';
    }
}
