package com.example.bookstory.models;

import androidx.room.Entity;

import java.util.Date;
//@Entity(tableName = "imprumuturi")
public class Imprumut {
    private long idImprumut;
    private long idUtilizator;
    private Date dataReturnare;
    private Date dataScadenta;
    private double taxaIntarziere;


}
