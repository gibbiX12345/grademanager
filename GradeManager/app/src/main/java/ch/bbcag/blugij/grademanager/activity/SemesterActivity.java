package ch.bbcag.blugij.grademanager.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.widget.Toast;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Fach;
import ch.bbcag.blugij.grademanager.sqlite.model.Note;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;

public class SemesterActivity extends AppCompatActivity implements View.OnClickListener {

    private Boolean isFabOpen = false;
    private FloatingActionButton addButton,noteAddButton,fachAddButton, semesterAddButton;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private static final String TAG = "SemesterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addButton = (FloatingActionButton)findViewById(R.id.add_button);
        noteAddButton = (FloatingActionButton)findViewById(R.id.note_add_button);
        fachAddButton = (FloatingActionButton)findViewById(R.id.fach_add_button);
        semesterAddButton = (FloatingActionButton)findViewById(R.id.semester_add_button);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        addButton.setOnClickListener(this);
        noteAddButton.setOnClickListener(this);
        fachAddButton.setOnClickListener(this);
        semesterAddButton.setOnClickListener(this);

        /* test data
        DatabaseHelper db = new DatabaseHelper(this);

        Semester semester = new Semester("1. Semester", 0.0);
        int semesterId = db.createSemester(semester);

        Fach fach = new Fach("Math", 0.0, 2.0, semesterId);
        int fachId = db.createFach(fach);

        Note note = new Note("1. Test", 6.0, 1.0, semesterId, fachId, "war gut", 1464082396);
        int noteId = db.createNote(note);

        Log.i(TAG, "SemesterId" + semesterId);
        Log.i(TAG, "FachId" + fachId);
        Log.i(TAG, "NoteId" + noteId);*/
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.add_button:
                animateFAB();
                break;

            case R.id.note_add_button:
                break;

            case R.id.fach_add_button:
                break;

            case R.id.semester_add_button:
                break;
        }
    }

    public void animateFAB(){

        if(isFabOpen){

            addButton.startAnimation(rotate_backward);

            noteAddButton.startAnimation(fab_close);
            fachAddButton.startAnimation(fab_close);
            semesterAddButton.startAnimation(fab_close);

            noteAddButton.setClickable(false);
            fachAddButton.setClickable(false);
            semesterAddButton.setClickable(false);

            isFabOpen = false;

        } else {

            addButton.startAnimation(rotate_forward);

            noteAddButton.startAnimation(fab_open);
            fachAddButton.startAnimation(fab_open);
            semesterAddButton.startAnimation(fab_open);

            noteAddButton.setClickable(true);
            fachAddButton.setClickable(true);
            semesterAddButton.setClickable(true);

            isFabOpen = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(this, "hallo suche", Toast.LENGTH_LONG).show();
                return true;
            case R.id.settings:
                Toast.makeText(this, "hallo settings", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
