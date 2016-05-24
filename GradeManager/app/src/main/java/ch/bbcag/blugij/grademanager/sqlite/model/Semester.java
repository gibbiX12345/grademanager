package ch.bbcag.blugij.grademanager.sqlite.model;

/**
 * Created by blugij on 24.05.2016.
 */
public class Semester {
    int id;
    String sBezeichnung;
    double sDurchschnitt;

    public Semester(){}

    public Semester(String bezeichnung, double durchschnitt){
        this.sBezeichnung = bezeichnung;
        this.sDurchschnitt = durchschnitt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return sBezeichnung;
    }

    public void setBezeichnung(String sBezeichnung) {
        this.sBezeichnung = sBezeichnung;
    }

    public double getDurchschnitt() {
        return sDurchschnitt;
    }

    public void setDurchschnitt(double sDurchschnitt) {
        this.sDurchschnitt = sDurchschnitt;
    }
}
