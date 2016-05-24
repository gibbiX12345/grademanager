package ch.bbcag.blugij.grademanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.adapter.FachAdapter;
import ch.bbcag.blugij.grademanager.adapter.NoteAdapter;
import ch.bbcag.blugij.grademanager.adapter.SemesterAdapter;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Fach;

public class FachActivity extends AppCompatActivity implements View.OnClickListener {
    private Boolean isFabOpen = false;
    private FloatingActionButton addButton,noteAddButton,fachAddButton;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private static final String TAG = "FachActivity";
    private ListView fachListView;
    private DatabaseHelper databaseHelper;
    public static final String INTENT_EXTRA_SEMESTER_ID = "ch.bbcag.blugij.grademanager.INTENT_EXTRA_SEMESTER_ID";
    private int semesterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fach);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseHelper = new DatabaseHelper(this);

        addButton = (FloatingActionButton)findViewById(R.id.add_button);
        noteAddButton = (FloatingActionButton)findViewById(R.id.note_add_button);
        fachAddButton = (FloatingActionButton)findViewById(R.id.fach_add_button);

        Intent intent = getIntent();
        semesterId = intent.getIntExtra(INTENT_EXTRA_SEMESTER_ID, 0);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        addButton.setOnClickListener(this);
        noteAddButton.setOnClickListener(this);
        fachAddButton.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        fachListView = (ListView) findViewById(R.id.fach_list_view);
        FachAdapter adapter = new FachAdapter(this, R.layout.custom_list_view_item, databaseHelper.getAllFachsBySemester(semesterId));
        fachListView.setAdapter(adapter);
        fachListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fach fach = (Fach) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                intent.putExtra(NoteActivity.INTENT_EXTRA_FACH_ID, fach.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
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
        }
    }

    public void animateFAB(){

        if(isFabOpen){

            addButton.startAnimation(rotate_backward);

            noteAddButton.startAnimation(fab_close);
            fachAddButton.startAnimation(fab_close);

            noteAddButton.setClickable(false);
            fachAddButton.setClickable(false);

            isFabOpen = false;

        } else {

            addButton.startAnimation(rotate_forward);

            noteAddButton.startAnimation(fab_open);
            fachAddButton.startAnimation(fab_open);

            noteAddButton.setClickable(true);
            fachAddButton.setClickable(true);

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
