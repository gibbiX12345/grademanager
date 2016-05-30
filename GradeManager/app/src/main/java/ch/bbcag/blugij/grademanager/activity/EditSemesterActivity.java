package ch.bbcag.blugij.grademanager.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Fach;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;

public class EditSemesterActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_semester);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText editText = (EditText) findViewById(R.id.edit_semester_et_bezeichnung_input);
                    String bezeichnung = editText.getText().toString();

                    EditText etInputSemester = (EditText) findViewById(R.id.edit_semester_et_bezeichnung_input);


                    if (bezeichnung.equals("")) {
                        throw new Exception();
                    } else {
                        Semester semester = new Semester(etInputSemester.getText().toString(), 0.0);
                        databaseHelper = new DatabaseHelper(getApplicationContext());
                        databaseHelper.createSemester(semester);
                        finish();
                    }
                }catch (Exception e){
                    Snackbar.make(view, getResources().getString(R.string.message_fill_all_fields), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });

    }

}
