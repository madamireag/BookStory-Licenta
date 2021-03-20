package com.example.bookstory.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "carti")
public class Carte {
    @PrimaryKey(autoGenerate = true)
    private int idCarte;
    private String titlu;
    private String ISBN;
    private Gen genCarte;
    private int nrCopiiDisponibile;
    private String copertaURI;

    public Carte(int idCarte, String titlu, String ISBN, Gen genCarte, int nrCopiiDisponibile, String copertaURI) {
        this.idCarte = idCarte;
        this.titlu = titlu;
        this.ISBN = ISBN;
        this.genCarte = genCarte;
        this.nrCopiiDisponibile = nrCopiiDisponibile;
        this.copertaURI = copertaURI;
    }
    @Ignore
    public Carte(String titlu, String ISBN, Gen genCarte, int nrCopiiDisponibile, String copertaURI) {
        this.titlu = titlu;
        this.ISBN = ISBN;
        this.genCarte = genCarte;
        this.nrCopiiDisponibile = nrCopiiDisponibile;
        this.copertaURI = copertaURI;
    }

    public int getIdCarte() {
        return idCarte;
    }

    public void setId(int id) {
        this.idCarte = id;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public Gen getGenCarte() {
        return genCarte;
    }

    public void setGenCarte(Gen genCarte) {
        this.genCarte = genCarte;
    }

    public int getNrCopiiDisponibile() {
        return nrCopiiDisponibile;
    }

    public void setNrCopiiDisponibile(int nrCopiiDisponibile) {
        this.nrCopiiDisponibile = nrCopiiDisponibile;
    }

    public String getCopertaURI() {
        return copertaURI;
    }

    public void setCopertaURI(String copertaURI) {
        this.copertaURI = copertaURI;
    }
}
