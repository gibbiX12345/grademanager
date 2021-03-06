package ch.bbcag.blugij.grademanager.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.adapter.NoteAdapter;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Note;
import ch.bbcag.blugij.grademanager.utils.UIHelper;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private Boolean isFabOpen = false;
    private FloatingActionButton addButton,noteAddButton;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private static final String TAG = "NoteActivity";
    private ListView noteListView;
    private DatabaseHelper databaseHelper;
    public static final String INTENT_EXTRA_FACH_ID = "ch.bbcag.blugij.grademanager.INTENT_EXTRA_FACH_ID";
    private int semesterId;
    private SharedPreferences sharedPreferences;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);

        setTitle(databaseHelper.getUniqueSemester(sharedPreferences.getInt(getString(R.string.current_semester_id),0)).getBezeichnung() + " ➤ " +
                databaseHelper.getUniqueFach(sharedPreferences.getInt(getString(R.string.current_fach_id),0)).getBezeichnung());

        addButton = (FloatingActionButton)findViewById(R.id.add_button);
        noteAddButton = (FloatingActionButton)findViewById(R.id.note_add_button);

        Intent intent = getIntent();
        semesterId = intent.getIntExtra(INTENT_EXTRA_FACH_ID, 0);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        addButton.setOnClickListener(this);
        noteAddButton.setOnClickListener(this);

        noteAddButton.setOnLongClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteListView = (ListView) findViewById(R.id.note_list_view);
        adapter = new NoteAdapter(this, R.layout.custom_list_view_item, databaseHelper.getAllNotesByFach(semesterId), false);
        TextView textView = (TextView) findViewById(R.id.no_elements_found);
        if (adapter.isEmpty()){
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        noteListView.setAdapter(adapter);

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DisplayNoteActivity.class);
                Note note = (Note) parent.getItemAtPosition(position);
                intent.putExtra(DisplayNoteActivity.INTENT_EXTRA_NOTE_ID, note.getId());
                startActivity(intent);
            }
        });

        registerForContextMenu(noteListView);
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
                                databaseHelper.deleteNote(item_id);
                                UIHelper.makeToast(NoteActivity.this, getResources().getString(R.string.toast_text_note_deleted), Toast.LENGTH_LONG);
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
                Intent intent = new Intent(this, EditNoteActivity.class);
                intent.putExtra(EditNoteActivity.INTENT_EXTRA_NOTE_ID, adapter.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position).getId());
                startActivity(intent);
                return true;
            default:

                return super.onContextItemSelected(item);
        }

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
        }
    }

    @Override
    public boolean onLongClick(View v) {

        int id = v.getId();
        float posX = v.getX();
        float posY = v.getY();

        Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);

        switch (id) {
            case R.id.note_add_button:
                toast.setText(getResources().getString(R.string.title_activity_edit_note));
                break;

            default:
                return true;
        }

        toast.show();
        return true;
    }

    public void animateFAB(){

        if(isFabOpen){

            addButton.startAnimation(rotate_backward);
            noteAddButton.startAnimation(fab_close);
            noteAddButton.setClickable(false);

            isFabOpen = false;

        } else {

            addButton.startAnimation(rotate_forward);
            noteAddButton.startAnimation(fab_open);
            noteAddButton.setClickable(true);

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
            case R.id.delete_all:
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.message_delete_title))
                        .setMessage(getResources().getString(R.string.message_delete_all_text))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                                databaseHelper.onUpgrade(db, 0, 0);
                                UIHelper.makeToast(NoteActivity.this, getResources().getString(R.string.toast_text_everything_deleted), Toast.LENGTH_LONG);

                                Intent intent = new Intent(getApplicationContext(), SemesterActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
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
