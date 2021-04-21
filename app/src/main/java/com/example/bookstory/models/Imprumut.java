package com.example.bookstory.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;
@Entity(tableName = "imprumuturi", foreignKeys = @ForeignKey(entity = Utilizator.class, parentColumns = "id", childColumns = "idUtilizator"))
public class Imprumut {
    @PrimaryKey(autoGenerate = true)
    private long idImprumut;
    private long idUtilizator;
    private Date dataReturnare;
    private Date dataScadenta;
    private double taxaIntarziere;

    public Imprumut(long idImprumut, long idUtilizator, Date dataReturnare, Date dataScadenta, double taxaIntarziere) {
        this.idImprumut = idImprumut;
        this.idUtilizator = idUtilizator;
        this.dataReturnare = dataReturnare;
        this.dataScadenta = dataScadenta;
        this.taxaIntarziere = taxaIntarziere;
    }

    public long getIdImprumut() {
        return idImprumut;
    }

    public void setIdImprumut(long idImprumut) {
        this.idImprumut = idImprumut;
    }

    public long getIdUtilizator() {
        return idUtilizator;
    }

    public void setIdUtilizator(long idUtilizator) {
        this.idUtilizator = idUtilizator;
    }

    public Date getDataReturnare() {
        return dataReturnare;
    }

    public void setDataReturnare(Date dataReturnare) {
        this.dataReturnare = dataReturnare;
    }

    public Date getDataScadenta() {
        return dataScadenta;
    }

    public void setDataScadenta(Date dataScadenta) {
        this.dataScadenta = dataScadenta;
    }

    public double getTaxaIntarziere() {
        return taxaIntarziere;
    }

    public void setTaxaIntarziere(double taxaIntarziere) {
        this.taxaIntarziere = taxaIntarziere;
    }
}
