package com.example.bookstory.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "imprumuturi", foreignKeys = @ForeignKey(entity = Utilizator.class, parentColumns = "id", childColumns = "idUtilizator"))
public class Imprumut {
    @PrimaryKey(autoGenerate = true)
    private long idImprumut;
    private long idUtilizator;
    private Date dataImprumut;
    private Date dataScadenta;
    private double taxaIntarziere;

    public Imprumut(long idImprumut, long idUtilizator, Date dataImprumut, Date dataScadenta, double taxaIntarziere) {
        this.idImprumut = idImprumut;
        this.idUtilizator = idUtilizator;
        this.dataImprumut = dataImprumut;
        this.dataScadenta = dataScadenta;
        this.taxaIntarziere = taxaIntarziere;
    }

    @Ignore
    public Imprumut(long idUtilizator, Date dataImprumut, Date dataScadenta, double taxaIntarziere) {
        this.idUtilizator = idUtilizator;
        this.dataImprumut = dataImprumut;
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

    public Date getDataImprumut() {
        return dataImprumut;
    }

    public void setDataImprumut(Date dataImprumut) {
        this.dataImprumut = dataImprumut;
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
        if (taxaIntarziere > 0) {
            this.taxaIntarziere = taxaIntarziere;
        }
    }
}
