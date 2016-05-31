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
import android.widget.EditText;
import android.widget.Toast;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;
import ch.bbcag.blugij.grademanager.utils.UIHelper;

public class EditSemesterActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    public static final String INTENT_EXTRA_SEMESTER_ID = "edit_semester_semester_id";
    private boolean isEdit = false;
    private Semester editSemester;

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
                                hasSame = true;
                            }
                        }

                        if (isEdit){
                            editSemester.setBezeichnung(bezeichnung);
                            databaseHelper.updateSemester(editSemester);
                        } else {
                            Semester semester = new Semester(bezeichnung, 0.0);
                            databaseHelper.createSemester(semester);
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
    }
}
