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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.adapter.SemesterAdapter;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;
import ch.bbcag.blugij.grademanager.utils.UIHelper;

public class SemesterActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

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

        noteAddButton.setOnLongClickListener(this);
        fachAddButton.setOnLongClickListener(this);
        semesterAddButton.setOnLongClickListener(this);
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
        TextView textView = (TextView) findViewById(R.id.no_elements_found);
        if (adapter.isEmpty()){
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        semesterListView.setAdapter(adapter);
        semesterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Semester semester = (Semester) parent.getItemAtPosition(position);

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(getString(R.string.current_semester_id), semester.getId());
                editor.apply();
                if (isFabOpen){
                    animateFAB();
                }
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
        inflater.inflate(R.menu.context_menu_semester, menu);
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
                                UIHelper.makeToast(SemesterActivity.this, getResources().getString(R.string.toast_text_semester_deleted), Toast.LENGTH_LONG);
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
            case R.id.item_duplicate:
                int item_id = adapter.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position).getId();
                Semester semester = databaseHelper.getUniqueSemester(item_id);
                databaseHelper.duplicateSemester(semester, "Kopie von " + semester.getBezeichnung());
                UIHelper.makeToast(SemesterActivity.this, getResources().getString(R.string.toast_text_semester_duplicated), Toast.LENGTH_LONG);
                onResume();

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
                    return;

                case R.id.note_add_button:
                    if (databaseHelper.getAllFachs().isEmpty()){
                        UIHelper.toastFunctionNotAvailable(this);
                    } else {
                        Intent intentNote = new Intent(this, EditNoteActivity.class);
                        startActivity(intentNote);
                    }
                    break;

                case R.id.fach_add_button:
                    if (databaseHelper.getAllSemesters().isEmpty()){
                        UIHelper.toastFunctionNotAvailable(this);
                    } else {
                        Intent intentFach = new Intent(this, EditFachActivity.class);
                        startActivity(intentFach);
                    }
                    break;

                case R.id.semester_add_button:
                    Intent intentSemester = new Intent(this, EditSemesterActivity.class);
                    startActivity(intentSemester);
                    break;
            }
        if (isFabOpen){
            animateFAB();
        }
    }


    public void animateFAB(){

        if(isFabOpen){

            addButton.startAnimation(rotate_backward);

            noteAddButton.startAnimation(fab_close);
            fachAddButton.startAnimation(fab_close);
            semesterAddButton.startAnimation(fab_close);

            noteAddButton.setEnabled(false);
            fachAddButton.setEnabled(false);
            semesterAddButton.setEnabled(false);

            isFabOpen = false;

        } else {

            addButton.startAnimation(rotate_forward);

            noteAddButton.startAnimation(fab_open);
            fachAddButton.startAnimation(fab_open);
            semesterAddButton.startAnimation(fab_open);

            noteAddButton.setEnabled(true);
            fachAddButton.setEnabled(true);
            semesterAddButton.setEnabled(true);

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
                        .setTitle(getResources().getString(R.string.delete_all_title))
                        .setMessage(getResources().getString(R.string.message_delete_all_text))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                                databaseHelper.onUpgrade(db, 0, 0);
                                UIHelper.makeToast(SemesterActivity.this, getResources().getString(R.string.toast_text_everything_deleted), Toast.LENGTH_LONG);

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

            case R.id.fach_add_button:
                toast.setText(getResources().getString(R.string.title_activity_edit_fach));
                break;

            case R.id.semester_add_button:
                toast.setText(getResources().getString(R.string.title_activity_edit_semester));
                break;

            default:
                return true;
        }

        toast.show();
        return true;
    }
}
