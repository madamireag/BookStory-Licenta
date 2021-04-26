package com.example.bookstory.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "utilizatori")
public class Utilizator  {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String numeComplet;
    private String adresa;
    private String nrTelefon;
    private String email;
    private String password;
    String uid;

    public Utilizator(long id, String numeComplet, String adresa, String nrTelefon, String email, String password, String uid) {
        this.id = id;
        this.numeComplet = numeComplet;
        this.adresa = adresa;
        this.nrTelefon = nrTelefon;
        this.email = email;
        this.password = password;
        this.uid = uid;
    }

    @Ignore
    public Utilizator(long id, String numeComplet, String email, String password, String adresa, String nrTelefon) {
        this.id = id;
        this.numeComplet = numeComplet;
        this.email = email;
        this.password = password;
        this.adresa = adresa;
        this.nrTelefon = nrTelefon;
    }

    @Ignore
    public Utilizator(String numeComplet, String adresa, String nrTelefon) {
        this.numeComplet = numeComplet;
        this.adresa = adresa;
        this.nrTelefon = nrTelefon;
    }

    @Ignore
    public Utilizator(String numeComplet, String adresa, String nrTelefon, String email, String password, String uid) {
        this.numeComplet = numeComplet;
        this.adresa = adresa;
        this.nrTelefon = nrTelefon;
        this.email = email;
        this.password = password;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumeComplet() {
        return numeComplet;
    }

    public void setNumeComplet(String numeComplet) {
        this.numeComplet = numeComplet;
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
