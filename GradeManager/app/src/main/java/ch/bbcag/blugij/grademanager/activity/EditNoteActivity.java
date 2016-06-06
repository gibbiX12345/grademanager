package ch.bbcag.blugij.grademanager.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.adapter.FachAdapter;
import ch.bbcag.blugij.grademanager.adapter.SemesterAdapter;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Fach;
import ch.bbcag.blugij.grademanager.sqlite.model.Note;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;
import ch.bbcag.blugij.grademanager.utils.UIHelper;

public class EditNoteActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Semester semester;
    private Fach fach;
    public static final String INTENT_EXTRA_NOTE_ID = "edit_note_note_id";
    private boolean isEdit = false;
    private Note editNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        int noteId = intent.getIntExtra(INTENT_EXTRA_NOTE_ID, 0);
        if (noteId > 0){
            isEdit = true;
            editNote = databaseHelper.getUniqueNote(noteId);
            setTitle(editNote.getBezeichnung() + " " + getResources().getString(R.string.edit_entry));
        } else {
            isEdit = false;
        }


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
                    /*DatePicker dpGeschriebenAm = (DatePicker) findViewById(R.id.edit_note_datepicker_datepicker);
                    long geschriebenAm = getLongFromDatePicker(dpGeschriebenAm);*/
                    EditText etDatepicker = (EditText) findViewById(R.id.edit_note_datepicker_datepicker);
                    long geschriebenAm = new SimpleDateFormat("dd.MM.yyyy").parse(etDatepicker.getText().toString()).getTime();
                    EditText etBemerkung = (EditText) findViewById(R.id.edit_note_bemerkung_input);
                    String bemerkung = etBemerkung.getText().toString();
                    if(bezeichnung.equals("") || gewichtung <= 0 || note < 1 || geschriebenAm < 0 || semester == null || fach == null){
                        throw new Exception();
                    } else {


                        int semesterId = semester.getId();
                        int fachId = fach.getId();
                        if (isEdit) {
                            editNote.setBezeichnung(bezeichnung);
                            editNote.setGewichtung(gewichtung);
                            editNote.setNote(note);
                            editNote.setSemesterId(semester.getId());
                            editNote.setFachId(fach.getId());
                            editNote.setGeschriebenAm(geschriebenAm);
                            editNote.setBemerkung(bemerkung);
                            databaseHelper.updateNote(editNote);
                        } else {
                            Note newNote = new Note(bezeichnung, note, gewichtung, semesterId, fachId, bemerkung, geschriebenAm);
                            databaseHelper.createNote(newNote);
                        }

                        if(note > 6) {
                            UIHelper.showInfoMessage(EditNoteActivity.this, getResources().getString(R.string.alert_title_high_note),
                                    getResources().getString(R.string.alert_text_high_note), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                    UIHelper.makeToast(EditNoteActivity.this, getResources().getString(R.string.toast_text_note_saved), Toast.LENGTH_LONG);

                                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt(getString(R.string.current_fach_id), fach.getId());
                                    editor.putInt(getString(R.string.current_semester_id), semester.getId());
                                    editor.apply();

                                    Intent intent = new Intent(EditNoteActivity.this, NoteActivity.class);
                                    intent.putExtra(NoteActivity.INTENT_EXTRA_FACH_ID, fach.getId());
                                    startActivity(intent);
                                }
                            });
                        } else {
                            finish();
                            UIHelper.makeToast(EditNoteActivity.this, getResources().getString(R.string.toast_text_note_saved), Toast.LENGTH_LONG);

                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(getString(R.string.current_fach_id), fach.getId());
                            editor.putInt(getString(R.string.current_semester_id), semester.getId());
                            editor.apply();

                            Intent intent = new Intent(EditNoteActivity.this, NoteActivity.class);
                            intent.putExtra(NoteActivity.INTENT_EXTRA_FACH_ID, fach.getId());
                            startActivity(intent);
                        }
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
                                fach = fachAdapter.getItem(position);
                            }
                        }
                    }
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

        final Calendar myCalendar = Calendar.getInstance();


        final EditText etDatepicker = (EditText) findViewById(R.id.edit_note_datepicker_datepicker);
        etDatepicker.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date()));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(etDatepicker, myCalendar);
            }

        };

        etDatepicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EditNoteActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etDatepicker.setFocusable(true);
        etDatepicker.setEnabled(true);
        etDatepicker.setSingleLine(true);
        etDatepicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new DatePickerDialog(EditNoteActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });


        if (isEdit){
            EditText etBezeichnung = (EditText) findViewById(R.id.edit_note_et_bezeichnung_input);
            etBezeichnung.setText(editNote.getBezeichnung());
            EditText etGewichtung = (EditText) findViewById(R.id.edit_note_weight_input);
            etGewichtung.setText(editNote.getGewichtung() + "");
            EditText etNote = (EditText) findViewById(R.id.edit_note_note_input);
            etNote.setText(editNote.getNote() + "");
            /*DatePicker dpGeschriebenAm = (DatePicker) findViewById(R.id.edit_note_datepicker_datepicker);
            setLongToDatePicker(dpGeschriebenAm, editNote.getGeschriebenAm());*/
            Date geschriebenAm = new Date(editNote.getGeschriebenAm());
            etDatepicker.setText(new SimpleDateFormat("dd.MM.yyyy").format(geschriebenAm));
            myCalendar.setTime(geschriebenAm);
            EditText etBemerkung = (EditText) findViewById(R.id.edit_note_bemerkung_input);
            etBemerkung.setText(editNote.getBemerkung());

            for (int position = 0; position < semesterAdapter.getCount(); position++) {
                if ((semesterAdapter.getItem(position)).getId() == editNote.getSemesterId()) {
                    spinner.setSelection(position);
                }
            }
            FachAdapter fachAdapter = (FachAdapter) fachSpinner.getAdapter();
            for (int position = 0; position < fachAdapter.getCount(); position++) {
                if ((fachAdapter.getItem(position)).getId() == editNote.getFachId()) {
                    fachSpinner.setSelection(position);
                }
            }
        }
    }

    private void updateLabel(EditText etDatepicker, Calendar myCalendar) {

        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMAN);

        etDatepicker.setText(sdf.format(myCalendar.getTime()));
    }

    public static long getLongFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTimeInMillis();
    }

    public static void setLongToDatePicker(DatePicker datePicker, long date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }
}
