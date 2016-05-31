package ch.bbcag.blugij.grademanager.activity;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.adapter.FachAdapter;
import ch.bbcag.blugij.grademanager.adapter.SemesterAdapter;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Fach;
import ch.bbcag.blugij.grademanager.sqlite.model.Note;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;

public class SemesterActivity extends AppCompatActivity implements View.OnClickListener {

    private Boolean isFabOpen = false;
    private FloatingActionButton addButton,noteAddButton,fachAddButton, semesterAddButton;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private static final String TAG = "SemesterActivity";
    private ListView semesterListView;
    private DatabaseHelper databaseHelper;
    private SemesterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        databaseHelper = new DatabaseHelper(this);

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
        Log.i(TAG, "NoteId" + noteId);
        */
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.current_semester_id), 0);
        editor.apply();

        semesterListView = (ListView) findViewById(R.id.semester_list_view);
        adapter = new SemesterAdapter(this, R.layout.custom_list_view_item, databaseHelper.getAllSemesters(), false);
        semesterListView.setAdapter(adapter);
        semesterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Semester semester = (Semester) parent.getItemAtPosition(position);

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(getString(R.string.current_semester_id), semester.getId());
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), FachActivity.class);
                intent.putExtra(FachActivity.INTENT_EXTRA_SEMESTER_ID, semester.getId());
                startActivity(intent);
            }
        });


        registerForContextMenu(semesterListView);


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.item_delete:
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.message_delete_title))
                        .setMessage(getResources().getString(R.string.message_delete_text))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int item_id = adapter.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position).getId();
                                databaseHelper.deleteSemester(item_id);
                                onResume();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onResume();
                            }
                        })
                        .show();


                return true;
            case R.id.item_modify:
                Intent intent = new Intent(this, EditSemesterActivity.class);
                intent.putExtra(EditSemesterActivity.INTENT_EXTRA_SEMESTER_ID, adapter.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position).getId());
                startActivity(intent);
                return true;
            default:

                return super.onContextItemSelected(item);
        }

    }

    @Override
    public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.add_button:
                    animateFAB();
                    break;

                case R.id.note_add_button:
                    Intent intentNote = new Intent(this, EditNoteActivity.class);
                    startActivity(intentNote);
                    break;

                case R.id.fach_add_button:
                    Intent intentFach = new Intent(this, EditFachActivity.class);
                    startActivity(intentFach);
                    break;

                case R.id.semester_add_button:
                    Intent intent = new Intent(this, EditSemesterActivity.class);
                    startActivity(intent);
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
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(this, "hallo suche", Toast.LENGTH_LONG).show();
                return true;
            case R.id.settings:
                Toast.makeText(this, "hallo settings", Toast.LENGTH_LONG).show();
                return true;

            case R.id.delete_all:
                // TODO: 30.05.2016 Absturz verhindern
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.message_delete_title))
                        .setMessage(getResources().getString(R.string.message_delete_text))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int item_id = adapter.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position).getId();
                                databaseHelper.deleteSemester(item_id);
                                onResume();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onResume();
                            }
                        })
                        .show();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
