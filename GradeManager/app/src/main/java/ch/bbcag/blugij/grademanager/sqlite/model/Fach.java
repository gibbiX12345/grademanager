package ch.bbcag.blugij.grademanager.sqlite.model;

/**
 * Created by blugij on 24.05.2016.
 */
public class Fach {
    int id;
    String fBezeichnung;
    double fDurchschnitt;
    double fGewichtung;
    int fSemesterId;

    public Fach(){}

    public Fach(String bezeichnung, double durchschnitt, double gewichtung, int semesterId){
        this.fBezeichnung = bezeichnung;
        this.fDurchschnitt = durchschnitt;
        this.fGewichtung = gewichtung;
        this.fSemesterId = semesterId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return fBezeichnung;
    }

    public void setBezeichnung(String fBezeichnung) {
        this.fBezeichnung = fBezeichnung;
    }

    public double getDurchschnitt() {
        return fDurchschnitt;
    }

    public void setDurchschnitt(double fDurchschnitt) {
        this.fDurchschnitt = fDurchschnitt;
    }

    public double getGewichtung() {
        return fGewichtung;
    }

    public void setGewichtung(double fGewichtung) {
        this.fGewichtung = fGewichtung;
    }

    public int getSemesterId() {
        return fSemesterId;
    }

    public void setSemesterId(int fSemesterId) {
        this.fSemesterId = fSemesterId;
    }
}
