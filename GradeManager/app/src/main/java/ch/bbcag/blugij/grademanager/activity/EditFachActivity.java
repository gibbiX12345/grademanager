package ch.bbcag.blugij.grademanager.activity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.adapter.SemesterAdapter;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Fach;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;

public class EditFachActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SemesterAdapter adapter;
    private Semester semester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fach);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper(this);

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
                        Fach fach = new Fach(editText.getText().toString(), 0.0, 0.0, semester.getId());
                        databaseHelper = new DatabaseHelper(getApplicationContext());
                        databaseHelper.createFach(fach);
                        finish();
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


    }
}
