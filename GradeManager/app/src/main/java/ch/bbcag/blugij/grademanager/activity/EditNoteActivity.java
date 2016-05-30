package ch.bbcag.blugij.grademanager.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.List;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.adapter.FachAdapter;
import ch.bbcag.blugij.grademanager.adapter.NoteAdapter;
import ch.bbcag.blugij.grademanager.adapter.SemesterAdapter;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Fach;
import ch.bbcag.blugij.grademanager.sqlite.model.Note;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;

public class EditNoteActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Semester semester;
    private Fach fach;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_save_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText etBezeichnung = (EditText) findViewById(R.id.edit_note_et_bezeichnung_input);
                    String bezeichnung = etBezeichnung.getText().toString();
                    EditText etGewichtung = (EditText) findViewById(R.id.edit_note_weight_input);
                    double gewichtung = Double.parseDouble(etGewichtung.getText().toString());
                    EditText etNote = (EditText) findViewById(R.id.edit_note_note_input);
                    double note = Double.parseDouble(etNote.getText().toString());
                    DatePicker dpGeschriebenAm = (DatePicker) findViewById(R.id.edit_note_datepicker_datepicker);
                    long geschriebenAm = getLongFromDatePicker(dpGeschriebenAm);
                    EditText etBemerkung = (EditText) findViewById(R.id.edit_note_bemerkung_input);
                    String bemerkung = etBemerkung.getText().toString();
                    if(bezeichnung.equals("") || gewichtung <= 0 || note < 1 || geschriebenAm < 0 || semester == null || fach == null){
                        throw new Exception();
                    } else {
                        int semesterId = semester.getId();
                        int fachId = fach.getId();
                        Note newNote = new Note(bezeichnung, note, gewichtung, semesterId, fachId, bemerkung, geschriebenAm);
                        databaseHelper.createNote(newNote);
                        finish();
                    }
                } catch (Exception e){
                    Snackbar.make(view, getResources().getString(R.string.message_fill_all_fields), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Spinner spinner = (Spinner) findViewById(R.id.edit_note_semester_spinner);
        final Spinner fachSpinner = (Spinner) findViewById(R.id.edit_note_fach_spinner);
        SemesterAdapter semesterAdapter = new SemesterAdapter(this, R.layout.custom_list_view_item_one_column, databaseHelper.getAllSemesters(), true);
        spinner.setAdapter(semesterAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                semester = (Semester) parent.getItemAtPosition(position);
                fachSpinner.setAdapter(new FachAdapter(getApplicationContext(), R.layout.custom_list_view_item_one_column, databaseHelper.getAllFachsBySemester(semester.getId()), true));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                semester = null;
            }
        });

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
                                return;
                            }
                        }
                    }
                    return;
                }
            }
        }
        fachSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fach = (Fach) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fach = null;
            }
        });
    }

    public static long getLongFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTimeInMillis();
    }
}
