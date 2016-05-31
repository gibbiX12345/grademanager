package ch.bbcag.blugij.grademanager.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.adapter.FachAdapter;
import ch.bbcag.blugij.grademanager.adapter.SemesterAdapter;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Fach;
import ch.bbcag.blugij.grademanager.sqlite.model.Note;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;

public class DisplayNoteActivity extends AppCompatActivity {
    public static final String INTENT_EXTRA_NOTE_ID = "display_note_note_id";
    private Note displayNote;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        databaseHelper = new DatabaseHelper(this);
        
        Intent intent = getIntent();
        int noteId = intent.getIntExtra(INTENT_EXTRA_NOTE_ID, 0);
        if (noteId > 0){
            displayNote = databaseHelper.getUniqueNote(noteId);
            setTitle(displayNote.getBezeichnung());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Spinner spinner = (Spinner) findViewById(R.id.edit_note_semester_spinner);
        spinner.setEnabled(false);
        spinner.setClickable(false);
        final Spinner fachSpinner = (Spinner) findViewById(R.id.edit_note_fach_spinner);
        fachSpinner.setEnabled(false);
        fachSpinner.setClickable(false);
        SemesterAdapter semesterAdapter = new SemesterAdapter(this, R.layout.custom_list_view_item_one_column, databaseHelper.getAllSemesters(), true);
        spinner.setAdapter(semesterAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        int semesterId = sharedPreferences.getInt(getString(R.string.current_semester_id), 0);

        if (semesterId != 0) {
            for (int position = 0; position < semesterAdapter.getCount(); position++) {
                if ((semesterAdapter.getItem(position)).getId() == semesterId) {
                    spinner.setSelection(position);
                    FachAdapter fachAdapter = new FachAdapter(getApplicationContext(), R.layout.custom_list_view_item_one_column, databaseHelper.getAllFachsBySemester(semesterId), true);
                    fachSpinner.setAdapter(fachAdapter);

                    int fachId = sharedPreferences.getInt(getString(R.string.current_fach_id), 0);

                    if (fachId != 0) {
                        for (position = 0; position < fachAdapter.getCount(); position++) {
                            if ((fachAdapter.getItem(position)).getId() == fachId) {
                                fachSpinner.setSelection(position);
                            }
                        }
                    }
                }
            }
        }

        EditText etBezeichnung = (EditText) findViewById(R.id.edit_note_et_bezeichnung_input);
        etBezeichnung.setText(displayNote.getBezeichnung());
        EditText etGewichtung = (EditText) findViewById(R.id.edit_note_weight_input);
        etGewichtung.setText(displayNote.getGewichtung() + "");
        EditText etNote = (EditText) findViewById(R.id.edit_note_note_input);
        etNote.setText(displayNote.getNote() + "");
        EditText etGeschriebenAm = (EditText) findViewById(R.id.edit_note_datepicker_input);
        etGeschriebenAm.setText(new SimpleDateFormat("dd. MMM yyyy").format(new Date(displayNote.getGeschriebenAm())));
        EditText etBemerkung = (EditText) findViewById(R.id.edit_note_bemerkung_input);
        etBemerkung.setText(displayNote.getBemerkung());

        for (int position = 0; position < semesterAdapter.getCount(); position++) {
            if ((semesterAdapter.getItem(position)).getId() == displayNote.getSemesterId()) {
                spinner.setSelection(position);
            }
        }
        FachAdapter fachAdapter = (FachAdapter) fachSpinner.getAdapter();
        for (int position = 0; position < fachAdapter.getCount(); position++) {
            if ((fachAdapter.getItem(position)).getId() == displayNote.getFachId()) {
                fachSpinner.setSelection(position);
            }
        }
    }
}
