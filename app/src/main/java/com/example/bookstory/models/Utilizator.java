package com.example.bookstory.models;

//@Entity(tableName = "utilizatori")
public class Utilizator {
    //@PrimaryKey(autoGenerate = true)
    private int idUtilizator;
    private String numeComplet;
    private String email;
    private String password;
    private String adresa;
    private String nrTelefon;

    public Utilizator(int idUtilizator, String numeComplet, String email, String password, String adresa, String nrTelefon) {
        this.idUtilizator = idUtilizator;
        this.numeComplet = numeComplet;
        this.email = email;
        this.password = password;
        this.adresa = adresa;
        this.nrTelefon = nrTelefon;
    }
    //@Ignore
    public Utilizator(String numeComplet, String adresa, String nrTelefon) {
        this.numeComplet = numeComplet;
        this.adresa = adresa;
        this.nrTelefon = nrTelefon;
    }

    public int getIdUtilizator() {
        return idUtilizator;
    }

    public void setIdUtilizator(int idUtilizator) {
        this.idUtilizator = idUtilizator;
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
