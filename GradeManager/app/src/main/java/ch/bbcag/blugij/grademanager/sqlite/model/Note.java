package ch.bbcag.blugij.grademanager.sqlite.model;

import java.sql.Timestamp;

/**
 * Created by blugij on 24.05.2016.
 */
public class Note {
    int id;
    String nBezeichnung;
    double nNote;
    double nGewichtung;
    int nSemesterId;
    int nFachId;
    String nBemerkung;
    long nGeschriebenAm;

    public Note(){}

    public Note(String bezeichnung, double note, double gewichtung, int semesterId, int fachId, String bemerkung, long geschriebenAm){
        this.nBezeichnung = bezeichnung;
        this.nNote = note;
        this.nGewichtung = gewichtung;
        this.nSemesterId = semesterId;
        this.nFachId = fachId;
        this.nBemerkung = bemerkung;
        this.nGeschriebenAm = geschriebenAm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return nBezeichnung;
    }

    public void setBezeichnung(String nBezeichnung) {
        this.nBezeichnung = nBezeichnung;
    }

    public double getNote() {
        return nNote;
    }

    public void setNote(double nNote) {
        this.nNote = nNote;
    }

    public double getGewichtung() {
        return nGewichtung;
    }

    public void setGewichtung(double nGewichtung) {
        this.nGewichtung = nGewichtung;
    }

    public int getSemesterId() {
        return nSemesterId;
    }

    public void setSemesterId(int nSemesterId) {
        this.nSemesterId = nSemesterId;
    }

    public int getFachId() {
        return nFachId;
    }

    public void setFachId(int nFachId) {
        this.nFachId = nFachId;
    }

    public String getBemerkung() {
        return nBemerkung;
    }

    public void setBemerkung(String nBemerkung) {
        this.nBemerkung = nBemerkung;
    }

    public long getGeschriebenAm() {
        return nGeschriebenAm;
    }

    public void setGeschriebenAm(long nGeschriebenAm) {
        this.nGeschriebenAm = nGeschriebenAm;
    }
}
