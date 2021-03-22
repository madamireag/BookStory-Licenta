package com.example.bookstory.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
//@Entity(tableName = "utilizatori")
public class Utilizator {
    //@PrimaryKey(autoGenerate = true)
    private int idUtilizator;
    private String nume;
    private String prenume;
    private String email;
    private String password;
    private String adresa;
    private String nrTelefon;

    public Utilizator(int idUtilizator, String nume, String prenume, String email, String password, String adresa, String nrTelefon) {
        this.idUtilizator = idUtilizator;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.password = password;
        this.adresa = adresa;
        this.nrTelefon = nrTelefon;
    }
    //@Ignore
    public Utilizator(String nume, String prenume, String adresa, String nrTelefon) {
        this.nume = nume;
        this.prenume = prenume;
        this.adresa = adresa;
        this.nrTelefon = nrTelefon;
    }

    public int getIdUtilizator() {
        return idUtilizator;
    }

    public void setIdUtilizator(int idUtilizator) {
        this.idUtilizator = idUtilizator;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getNrTelefon() {
        return nrTelefon;
    }

    public void setNrTelefon(String nrTelefon) {
        this.nrTelefon = nrTelefon;
    }
}
