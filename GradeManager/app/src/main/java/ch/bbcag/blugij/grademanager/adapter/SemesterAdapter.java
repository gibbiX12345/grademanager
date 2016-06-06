package ch.bbcag.blugij.grademanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;

/**
 * Created by blugij on 24.05.2016.
 */
public class SemesterAdapter extends ArrayAdapter<Semester> {
    boolean isEditMask = false;
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

    public SemesterAdapter(Context context, int resource, List<Semester> objects, boolean isEditMask) {
        super(context, resource, objects);
        this.isEditMask = isEditMask;
    }

    public SemesterAdapter(Context context, int resource, int textViewResourceId, List<Semester> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Semester semester = getItem(position);

        if (isEditMask){
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view_item_one_column, parent, false);
            }

            TextView tvFirst = (TextView) convertView.findViewById(R.id.tveditfirst);

            tvFirst.setText(semester.getBezeichnung());
        } else {
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view_item, parent, false);
            }

            TextView tvFirst = (TextView) convertView.findViewById(R.id.tvfirst);
            TextView tvSecond = (TextView) convertView.findViewById(R.id.tvsecond);
            TextView tvThird = (TextView) convertView.findViewById(R.id.tvthird);

            DatabaseHelper databaseHelper = new DatabaseHelper(getContext());

            tvFirst.setText(semester.getBezeichnung());
            tvSecond.setText("");
            tvThird.setText(semester.getDurchschnitt(databaseHelper) + "");

            if (semester.getDurchschnitt(databaseHelper) < 4.0){
                tvThird.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
            } else {
                tvThird.setTextColor(getContext().getResources().getColor(R.color.black));
            }
        }

        return convertView;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Semester semester = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view_item_one_column, parent, false);
        }

        TextView tv = (TextView) convertView;

        tv.setText(semester.getBezeichnung());

        return convertView;
    }
}
