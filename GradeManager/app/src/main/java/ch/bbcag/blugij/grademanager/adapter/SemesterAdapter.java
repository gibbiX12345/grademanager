package ch.bbcag.blugij.grademanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;

/**
 * Created by blugij on 24.05.2016.
 */
public class SemesterAdapter extends ArrayAdapter<Semester> {
    public SemesterAdapter(Context context, int resource) {
        super(context, resource);
    }

    public SemesterAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public SemesterAdapter(Context context, int resource, Semester[] objects) {
        super(context, resource, objects);
    }

    public SemesterAdapter(Context context, int resource, int textViewResourceId, Semester[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public SemesterAdapter(Context context, int resource, List<Semester> objects) {
        super(context, resource, objects);
    }

    public SemesterAdapter(Context context, int resource, int textViewResourceId, List<Semester> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Semester semester = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view_item, parent, false);
        }

        TextView tvFirst = (TextView) convertView.findViewById(R.id.tvfirst);
        TextView tvSecond = (TextView) convertView.findViewById(R.id.tvsecond);
        TextView tvThird = (TextView) convertView.findViewById(R.id.tvthird);

        tvFirst.setText(semester.getBezeichnung());
        tvSecond.setText("");
        tvThird.setText(semester.getDurchschnitt() + "");
        return convertView;
    }
}
