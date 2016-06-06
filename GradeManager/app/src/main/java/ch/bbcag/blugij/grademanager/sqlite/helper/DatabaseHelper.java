package ch.bbcag.blugij.grademanager.sqlite.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ch.bbcag.blugij.grademanager.sqlite.model.Fach;
import ch.bbcag.blugij.grademanager.sqlite.model.Note;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;

/**
 * Created by blugij on 24.05.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String LOG = DatabaseHelper.class.getName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "gradeManager";

    //Table Names
    private static final String TABLE_SEMESTER = "semester";
    private static final String TABLE_FACH = "fach";
    private static final String TABLE_NOTE = "note";

    //region Column-Definitions
    // id definition
    private static final String KEY_ID = "id";

    //SEMESTER TABLE column names
    private static final String KEY_S_BEZEICHNUNG = "s_bezeichnung";
    private static final String KEY_S_DURCHSCHNITT = "s_durchschnitt";

    //FACH TABLE column names
    private static final String KEY_F_BEZEICHNUNG = "f_bezeichnung";
    private static final String KEY_F_DURCHSCHNITT = "f_durchschnitt";
    private static final String KEY_F_GEWICHTUNG = "f_gewichtung";
    private static final String KEY_F_SEMESTER_ID = "f_semester_id";

    //NOTE TABLE column names
    private static final String KEY_N_BEZEICHNUNG = "n_bezeichnung";
    private static final String KEY_N_NOTE = "n_note";
    private static final String KEY_N_GEWICHTUNG = "n_gewichtung";
    private static final String KEY_N_SEMESTER_ID = "n_semester_id";
    private static final String KEY_N_FACH_ID = "n_fach_id";
    private static final String KEY_N_BEMERKUNG = "n_bemerkung";
    private static final String KEY_N_GESCHRIEBEN_AM= "n_geschrieben_am";
    //endregion

    //region Table Create-Statements
    private static final String CREATE_TABLE_SEMESTER = "CREATE TABLE " + TABLE_SEMESTER
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_S_BEZEICHNUNG + " TEXT,"
            + KEY_S_DURCHSCHNITT + " REAL" + ")";

    private static final String CREATE_TABLE_FACH = "CREATE TABLE " + TABLE_FACH
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_F_BEZEICHNUNG + " TEXT,"
            + KEY_F_DURCHSCHNITT + " REAL," + KEY_F_GEWICHTUNG + " REAL,"
            + KEY_F_SEMESTER_ID + " INTEGER" + ")";

    private static final String CREATE_TABLE_NOTE = "CREATE TABLE " + TABLE_NOTE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_N_BEZEICHNUNG + " TEXT,"
            + KEY_N_NOTE + " REAL," + KEY_N_GEWICHTUNG + " REAL,"
            + KEY_N_SEMESTER_ID + " INTEGER," + KEY_N_FACH_ID + " INTEGER,"
            + KEY_N_BEMERKUNG + " TEXT," + KEY_N_GESCHRIEBEN_AM + " INTEGER" + ")";

    //endregion

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SEMESTER);
        db.execSQL(CREATE_TABLE_FACH);
        db.execSQL(CREATE_TABLE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FACH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEMESTER);

        onCreate(db);
    }

    //region Semester Table methods

    public int createSemester(Semester semester){

        ContentValues values = new ContentValues();
        values.put(KEY_S_BEZEICHNUNG, semester.getBezeichnung());
        values.put(KEY_S_DURCHSCHNITT, semester.getDurchschnitt(this));

        SQLiteDatabase db = this.getWritableDatabase();

        long semesterId = db.insert(TABLE_SEMESTER, null, values);

        db.close();

        return (int) semesterId;
    }

    public int updateSemester(Semester semester){

        ContentValues values = new ContentValues();
        values.put(KEY_S_BEZEICHNUNG, semester.getBezeichnung());
        values.put(KEY_S_DURCHSCHNITT, semester.getDurchschnitt(this));

        SQLiteDatabase db = this.getWritableDatabase();

        int affectedRows = db.update(TABLE_SEMESTER, values, KEY_ID + " = ?",
                new String[]{String.valueOf(semester.getId())});

        db.close();
        return affectedRows;
    }

    public void deleteSemester(int semesterId){
        for(Fach fach : getAllFachsBySemester(semesterId)){
            deleteFach(fach.getId());
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SEMESTER, KEY_ID + " = ?",
                new String[]{String.valueOf(semesterId)});
        db.close();
    }

    public List<Semester> getAllSemesters(){
        List<Semester> semesters = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SEMESTER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()){
            do {
                Semester semester = new Semester();
                semester.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                semester.setBezeichnung(c.getString(c.getColumnIndex(KEY_S_BEZEICHNUNG)));
                semester.setDurchschnitt(c.getDouble(c.getColumnIndex(KEY_S_DURCHSCHNITT)));
                semesters.add(semester);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return semesters;
    }

    public Semester getUniqueSemester(int semesterId){
        String selectQuery = "SELECT * FROM " + TABLE_SEMESTER + " WHERE "
                + KEY_ID + " = " + semesterId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null){
            c.moveToFirst();
            Semester semester = new Semester();
            semester.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            semester.setBezeichnung(c.getString(c.getColumnIndex(KEY_S_BEZEICHNUNG)));
            semester.setDurchschnitt(c.getDouble(c.getColumnIndex(KEY_S_DURCHSCHNITT)));

            c.close();
            db.close();
            return semester;
        } else {
            return null;
        }
    }

    public double calculateSemesterAverage(Semester semester){
        List<Fach> fachList = getAllFachsBySemester(semester.getId());
        double gewichtungCount = 0;
        double sumFach = 0;
        for(Fach fach : fachList){
            fach.setDurchschnitt(calculateFachAverage(fach));
            if (fach.getDurchschnitt(this) != 0) {
                sumFach += fach.getGewichtung() * fach.getDurchschnitt(this);
                gewichtungCount += fach.getGewichtung();
            }
        }
        if (sumFach == 0){
            return sumFach;
        }
        return Math.round((sumFach / gewichtungCount) * 10) / 10.0;
    }

    //endregion

    //region Fach Table methods

    public int createFach(Fach fach){

        ContentValues values = new ContentValues();
        values.put(KEY_F_BEZEICHNUNG, fach.getBezeichnung());
        values.put(KEY_F_DURCHSCHNITT, fach.getDurchschnitt(this));
        values.put(KEY_F_GEWICHTUNG, fach.getGewichtung());
        values.put(KEY_F_SEMESTER_ID, fach.getSemesterId());

        SQLiteDatabase db = this.getWritableDatabase();

        long fachId = db.insert(TABLE_FACH, null, values);

        db.close();

        return (int) fachId;
    }

    public int updateFach(Fach fach){

        ContentValues values = new ContentValues();
        values.put(KEY_F_BEZEICHNUNG, fach.getBezeichnung());
        values.put(KEY_F_DURCHSCHNITT, fach.getDurchschnitt(this));
        values.put(KEY_F_GEWICHTUNG, fach.getGewichtung());
        values.put(KEY_F_SEMESTER_ID, fach.getSemesterId());

        SQLiteDatabase db = this.getWritableDatabase();

        int affectedRows = db.update(TABLE_FACH, values, KEY_ID + " = ?",
                new String[]{String.valueOf(fach.getId())});

        db.close();
        return affectedRows;
    }

    public void deleteFach(int fachId){
        for(Note note : getAllNotesByFach(fachId)){
            deleteNote(note.getId());
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FACH, KEY_ID + " = ?",
                new String[]{String.valueOf(fachId)});
        db.close();
    }

    public List<Fach> getAllFachs(){
        List<Fach> fachList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FACH;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()){
            do {
                Fach fach = new Fach();
                fach.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                fach.setBezeichnung(c.getString(c.getColumnIndex(KEY_F_BEZEICHNUNG)));
                fach.setDurchschnitt(c.getDouble(c.getColumnIndex(KEY_F_DURCHSCHNITT)));
                fach.setGewichtung(c.getDouble(c.getColumnIndex(KEY_F_GEWICHTUNG)));
                fach.setSemesterId(c.getInt(c.getColumnIndex(KEY_F_SEMESTER_ID)));
                fachList.add(fach);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return fachList;
    }

    public List<Fach> getAllFachsBySemester(int semesterId){
        List<Fach> fachList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FACH + " WHERE " + KEY_F_SEMESTER_ID + " = " + semesterId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()){
            do {
                Fach fach = new Fach();
                fach.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                fach.setBezeichnung(c.getString(c.getColumnIndex(KEY_F_BEZEICHNUNG)));
                fach.setDurchschnitt(c.getDouble(c.getColumnIndex(KEY_F_DURCHSCHNITT)));
                fach.setGewichtung(c.getDouble(c.getColumnIndex(KEY_F_GEWICHTUNG)));
                fach.setSemesterId(c.getInt(c.getColumnIndex(KEY_F_SEMESTER_ID)));
                fachList.add(fach);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return fachList;
    }

    public Fach getUniqueFach(int fachId){
        String selectQuery = "SELECT * FROM " + TABLE_FACH + " WHERE "
                + KEY_ID + " = " + fachId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null){
            c.moveToFirst();
            Fach fach = new Fach();
            fach.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            fach.setBezeichnung(c.getString(c.getColumnIndex(KEY_F_BEZEICHNUNG)));
            fach.setDurchschnitt(c.getDouble(c.getColumnIndex(KEY_F_DURCHSCHNITT)));
            fach.setGewichtung(c.getDouble(c.getColumnIndex(KEY_F_GEWICHTUNG)));
            fach.setSemesterId(c.getInt(c.getColumnIndex(KEY_F_SEMESTER_ID)));

            c.close();
            db.close();
            return fach;
        } else {
            return null;
        }
    }

    public double calculateFachAverage(Fach fach){
        List<Note> noteList = getAllNotesByFach(fach.getId());
        double gewichtungCount = 0;
        double sumNote = 0;
        for(Note note : noteList){
            sumNote += note.getGewichtung() * note.getNote();
            gewichtungCount += note.getGewichtung();
        }
        if (sumNote == 0){
            return sumNote;
        }
        return Math.round((sumNote / gewichtungCount) * 10) / 10.0;
    }

    //endregion

    //region Note Table methods

    public int createNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_N_BEZEICHNUNG, note.getBezeichnung());
        values.put(KEY_N_NOTE, note.getNote());
        values.put(KEY_N_GEWICHTUNG, note.getGewichtung());
        values.put(KEY_N_SEMESTER_ID, note.getSemesterId());
        values.put(KEY_N_FACH_ID, note.getFachId());
        values.put(KEY_N_BEMERKUNG, note.getBemerkung());
        values.put(KEY_N_GESCHRIEBEN_AM, note.getGeschriebenAm());

        long noteId = db.insert(TABLE_NOTE, null, values);

        db.close();

        return (int) noteId;
    }

    public int updateNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_N_BEZEICHNUNG, note.getBezeichnung());
        values.put(KEY_N_NOTE, note.getNote());
        values.put(KEY_N_GEWICHTUNG, note.getGewichtung());
        values.put(KEY_N_SEMESTER_ID, note.getSemesterId());
        values.put(KEY_N_FACH_ID, note.getFachId());
        values.put(KEY_N_BEMERKUNG, note.getBemerkung());
        values.put(KEY_N_GESCHRIEBEN_AM, note.getGeschriebenAm());

        int affectedRows = db.update(TABLE_NOTE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(note.getId())});

        db.close();
        return affectedRows;
    }

    public void deleteNote(int noteId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE, KEY_ID + " = ?",
                new String[]{String.valueOf(noteId)});
        db.close();
    }

    public List<Note> getAllNotes(){
        List<Note> fachList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NOTE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()){
            do {
                Note note = new Note();
                note.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                note.setBezeichnung(c.getString(c.getColumnIndex(KEY_N_BEZEICHNUNG)));
                note.setNote(c.getDouble(c.getColumnIndex(KEY_N_NOTE)));
                note.setGewichtung(c.getDouble(c.getColumnIndex(KEY_N_GEWICHTUNG)));
                note.setSemesterId(c.getInt(c.getColumnIndex(KEY_N_SEMESTER_ID)));
                note.setFachId(c.getInt(c.getColumnIndex(KEY_N_FACH_ID)));
                note.setBemerkung(c.getString(c.getColumnIndex(KEY_N_BEMERKUNG)));
                note.setGeschriebenAm(c.getLong(c.getColumnIndex(KEY_N_GESCHRIEBEN_AM)));
                fachList.add(note);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return fachList;
    }

    public List<Note> getAllNotesByFach(int fachId){
        List<Note> fachList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NOTE + " WHERE " + KEY_N_FACH_ID + " = " + fachId + " order by " + KEY_N_GESCHRIEBEN_AM + " desc";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()){
            do {
                Note note = new Note();
                note.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                note.setBezeichnung(c.getString(c.getColumnIndex(KEY_N_BEZEICHNUNG)));
                note.setNote(c.getDouble(c.getColumnIndex(KEY_N_NOTE)));
                note.setGewichtung(c.getDouble(c.getColumnIndex(KEY_N_GEWICHTUNG)));
                note.setSemesterId(c.getInt(c.getColumnIndex(KEY_N_SEMESTER_ID)));
                note.setFachId(c.getInt(c.getColumnIndex(KEY_N_FACH_ID)));
                note.setBemerkung(c.getString(c.getColumnIndex(KEY_N_BEMERKUNG)));
                note.setGeschriebenAm(c.getLong(c.getColumnIndex(KEY_N_GESCHRIEBEN_AM)));
                fachList.add(note);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return fachList;
    }

    public Note getUniqueNote(int noteId){
        String selectQuery = "SELECT * FROM " + TABLE_NOTE + " WHERE "
                + KEY_ID + " = " + noteId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null){
            c.moveToFirst();
            Note note = new Note();
            note.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            note.setBezeichnung(c.getString(c.getColumnIndex(KEY_N_BEZEICHNUNG)));
            note.setNote(c.getDouble(c.getColumnIndex(KEY_N_NOTE)));
            note.setGewichtung(c.getDouble(c.getColumnIndex(KEY_N_GEWICHTUNG)));
            note.setSemesterId(c.getInt(c.getColumnIndex(KEY_N_SEMESTER_ID)));
            note.setFachId(c.getInt(c.getColumnIndex(KEY_N_FACH_ID)));
            note.setBemerkung(c.getString(c.getColumnIndex(KEY_N_BEMERKUNG)));
            note.setGeschriebenAm(c.getLong(c.getColumnIndex(KEY_N_GESCHRIEBEN_AM)));

            c.close();
            db.close();
            return note;
        } else {
            return null;
        }
    }

    //endregion
}
