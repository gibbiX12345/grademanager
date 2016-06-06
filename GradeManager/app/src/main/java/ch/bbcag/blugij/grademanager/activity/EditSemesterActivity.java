package ch.bbcag.blugij.grademanager.activity;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.adapter.FachAdapter;
import ch.bbcag.blugij.grademanager.adapter.SemesterAdapter;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;
import ch.bbcag.blugij.grademanager.utils.UIHelper;

public class EditSemesterActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    public static final String INTENT_EXTRA_SEMESTER_ID = "edit_semester_semester_id";
    private boolean isEdit = false;
    private Semester editSemester;
    private boolean duplicate = false;
    private Semester oldSemester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_semester);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        Intent intent = getIntent();
        int semesterId = intent.getIntExtra(INTENT_EXTRA_SEMESTER_ID, 0);

        if (semesterId > 0){
            isEdit = true;
            editSemester = databaseHelper.getUniqueSemester(semesterId);
            setTitle(editSemester.getBezeichnung() + " " + getResources().getString(R.string.edit_entry));
        } else {
            isEdit = false;
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText editText = (EditText) findViewById(R.id.edit_semester_et_bezeichnung_input);
                    String bezeichnung = editText.getText().toString();

                    if (bezeichnung.equals("")) {
                        throw new Exception();
                    } else {
                        boolean hasSame = false;
                        for(Semester semester : databaseHelper.getAllSemesters()){
                            if (semester.getBezeichnung().toLowerCase().equals(bezeichnung.toLowerCase())){
                                if (isEdit){
                                    if (semester.getId() != editSemester.getId()){
                                        hasSame = true;
                                    }
                                } else {
                                    hasSame = true;
                                }
                            }
                        }

                        if (isEdit){
                            editSemester.setBezeichnung(bezeichnung);
                            databaseHelper.updateSemester(editSemester);
                        } else {
                            if (duplicate){
                                Spinner spinnerSemester = (Spinner) findViewById(R.id.edit_semester_spinner);
                                databaseHelper.duplicateSemester(oldSemester, bezeichnung);
                            } else {
                                Semester semester = new Semester(bezeichnung, 0.0);
                                databaseHelper.createSemester(semester);
                            }
                        }

                        if (hasSame) {
                            UIHelper.showInfoMessage(EditSemesterActivity.this, getResources().getString(R.string.alert_title_same_bez),
                                    getResources().getString(R.string.alert_text_same_bez), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                            UIHelper.makeToast(EditSemesterActivity.this, getResources().getString(R.string.toast_text_semester_saved), Toast.LENGTH_LONG);
                                        }
                                    });
                        } else {
                            finish();
                            UIHelper.makeToast(EditSemesterActivity.this, getResources().getString(R.string.toast_text_semester_saved), Toast.LENGTH_LONG);
                        }
                    }
                }catch (Exception e){
                    Snackbar.make(view, getResources().getString(R.string.message_fill_all_fields), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isEdit){
            EditText editText = (EditText) findViewById(R.id.edit_semester_et_bezeichnung_input);

            editText.setText(editSemester.getBezeichnung());
        }

        CheckBox cbxFaecherUebernehmen = (CheckBox) findViewById(R.id.cbx_faecher_uebernehmen);
        final Spinner spinnerSemester = (Spinner) findViewById(R.id.edit_semester_spinner);


        SemesterAdapter semesterAdapter = new SemesterAdapter(this, R.layout.custom_list_view_item_one_column, databaseHelper.getAllSemesters(), true);
        spinnerSemester.setAdapter(semesterAdapter);
        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                oldSemester = (Semester) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                oldSemester = null;
            }
        });

        if (databaseHelper.getAllSemesters().size() < 1){
            cbxFaecherUebernehmen.setEnabled(false);
        } else {
            cbxFaecherUebernehmen.setEnabled(true);
        }

        cbxFaecherUebernehmen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    spinnerSemester.setVisibility(View.VISIBLE);
                    duplicate = true;
                } else {
                    spinnerSemester.setVisibility(View.INVISIBLE);
                    duplicate = false;
                }
            }
        });

        spinnerSemester.setVisibility(View.INVISIBLE);
        cbxFaecherUebernehmen.setChecked(false);

        if (isEdit){
            cbxFaecherUebernehmen.setVisibility(View.INVISIBLE);
            spinnerSemester.setVisibility(View.INVISIBLE);
        }
    }

}
