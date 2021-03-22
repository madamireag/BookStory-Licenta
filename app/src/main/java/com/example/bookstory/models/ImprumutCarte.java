package com.example.bookstory.models;

public class ImprumutCarte {
    private long idImprumut;
    private long idCarte;

    public ImprumutCarte(long idImprumut, long idCarte) {
        this.idImprumut = idImprumut;
        this.idCarte = idCarte;
    }

    public long getIdImprumut() {
        return idImprumut;
    }

    public void setIdImprumut(long idImprumut) {
        this.idImprumut = idImprumut;
    }

    public long getIdCarte() {
        return idCarte;
    }

    public void setIdCarte(long idCarte) {
        this.idCarte = idCarte;
    }
}
