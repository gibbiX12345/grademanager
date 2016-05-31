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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.adapter.SemesterAdapter;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Fach;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;
import ch.bbcag.blugij.grademanager.utils.UIHelper;

public class EditFachActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SemesterAdapter adapter;
    private Semester semester;
    public static final String INTENT_EXTRA_FACH_ID = "edit_fach_fach_id";
    private boolean isEdit = false;
    private Fach editFach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fach);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        int fachId = intent.getIntExtra(INTENT_EXTRA_FACH_ID, 0);
        if (fachId > 0){
            isEdit = true;
            editFach = databaseHelper.getUniqueFach(fachId);
            setTitle(editFach.getBezeichnung() + " " + getResources().getString(R.string.edit_entry));
        } else {
            isEdit = false;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_save_fach);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText editText = (EditText) findViewById(R.id.edit_fach_et_bezeichnung_input);
                    String bezeichnung = editText.getText().toString();

                    EditText etInputWeight = (EditText) findViewById(R.id.edit_lesson_weight_input);
                    double weight = Double.parseDouble(etInputWeight.getText().toString());


                    if (bezeichnung.equals("") || weight == 0) {
                        throw new Exception();
                    } else {
                        boolean hasSame = false;
                        for(Fach fach : databaseHelper.getAllFachsBySemester(semester.getId())){
                            if (fach.getBezeichnung().toLowerCase().equals(bezeichnung.toLowerCase())){
                                hasSame = true;
                            }
                        }

                        if (isEdit){
                            editFach.setBezeichnung(bezeichnung);
                            editFach.setGewichtung(weight);
                            editFach.setSemesterId(semester.getId());
                            databaseHelper.updateFach(editFach);
                        } else {
                            Fach fach = new Fach(editText.getText().toString(), 0.0, weight, semester.getId());
                            databaseHelper.createFach(fach);
                        }

                        if (hasSame) {
                            UIHelper.showInfoMessage(EditFachActivity.this, getResources().getString(R.string.alert_title_same_bez_fach),
                                    getResources().getString(R.string.alert_text_same_bez_fach), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                            UIHelper.makeToast(EditFachActivity.this, getResources().getString(R.string.toast_text_fach_saved), Toast.LENGTH_LONG);

                                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt(getString(R.string.current_semester_id), semester.getId());
                                            editor.apply();

                                            Intent fachIntent = new Intent(getApplicationContext(), FachActivity.class);
                                            fachIntent.putExtra(FachActivity.INTENT_EXTRA_SEMESTER_ID, semester.getId());
                                            startActivity(fachIntent);
                                        }
                                    });
                        } else {
                            finish();
                            UIHelper.makeToast(EditFachActivity.this, getResources().getString(R.string.toast_text_fach_saved), Toast.LENGTH_LONG);

                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(getString(R.string.current_semester_id), semester.getId());
                            editor.apply();

                            Intent fachIntent = new Intent(getApplicationContext(), FachActivity.class);
                            fachIntent.putExtra(FachActivity.INTENT_EXTRA_SEMESTER_ID, semester.getId());
                            startActivity(fachIntent);
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
        Spinner spinner = (Spinner) findViewById(R.id.edit_lesson_spinner);
        adapter = new SemesterAdapter(this, R.layout.custom_list_view_item_one_column, databaseHelper.getAllSemesters(), true);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                semester = (Semester) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                semester = null;
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        int semesterId = sharedPreferences.getInt(getString(R.string.current_semester_id), 0);

        if (semesterId != 0) {
            for (int position = 0; position < adapter.getCount(); position++) {
                if ((adapter.getItem(position)).getId() == semesterId) {
                    spinner.setSelection(position);
                }
            }
        }

        if (isEdit){

            EditText editText = (EditText) findViewById(R.id.edit_fach_et_bezeichnung_input);
            editText.setText(editFach.getBezeichnung());

            EditText etInputWeight = (EditText) findViewById(R.id.edit_lesson_weight_input);
            etInputWeight.setText(editFach.getGewichtung() + "");

            for (int position = 0; position < adapter.getCount(); position++) {
                if ((adapter.getItem(position)).getId() == editFach.getSemesterId()) {
                    spinner.setSelection(position);
                }
            }
        }

    }
}
