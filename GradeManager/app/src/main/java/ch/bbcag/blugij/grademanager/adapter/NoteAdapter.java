package ch.bbcag.blugij.grademanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.sqlite.model.Note;

/**
 * Created by blugij on 24.05.2016.
 */
public class NoteAdapter extends ArrayAdapter<Note> {
    boolean isEditMask = false;
    public NoteAdapter(Context context, int resource) {
        super(context, resource);
    }

    public NoteAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public NoteAdapter(Context context, int resource, Note[] objects) {
        super(context, resource, objects);
    }

    public NoteAdapter(Context context, int resource, int textViewResourceId, Note[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public NoteAdapter(Context context, int resource, List<Note> objects, boolean isEditMask) {
        super(context, resource, objects);
        this.isEditMask = isEditMask;
    }

    public NoteAdapter(Context context, int resource, int textViewResourceId, List<Note> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = getItem(position);

        if (isEditMask){
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view_item, parent, false);
            }

            TextView tvFirst = (TextView) convertView.findViewById(R.id.tvfirst);
            TextView tvSecond = (TextView) convertView.findViewById(R.id.tvsecond);
            TextView tvThird = (TextView) convertView.findViewById(R.id.tvthird);

            tvFirst.setText(note.getBezeichnung());
            if (!isEditMask) {
                tvSecond.setText(note.getGewichtung() + "x");
                tvThird.setText(note.getNote() + "");
            } else {
                tvSecond.setText("");
                tvThird.setText("");
            }
        } else {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view_item, parent, false);
            }

            TextView tvFirst = (TextView) convertView.findViewById(R.id.tvfirst);
            TextView tvSecond = (TextView) convertView.findViewById(R.id.tvsecond);
            TextView tvThird = (TextView) convertView.findViewById(R.id.tvthird);
            TextView tvFourth = (TextView) convertView.findViewById(R.id.tvfourth);
            LinearLayout dateLayout = (LinearLayout) convertView.findViewById(R.id.date_layout);
            dateLayout.bringToFront();

            tvFirst.setText(note.getBezeichnung());
            tvSecond.setText(note.getGewichtung() + "x");
            tvThird.setText(note.getNote() + "");
            tvFourth.bringToFront();
            tvFourth.setText(new SimpleDateFormat("dd.MMM.yyyy", Locale.GERMAN).format(new Date(note.getGeschriebenAm())));
        }
        return convertView;
    }
}
