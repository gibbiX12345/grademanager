package ch.bbcag.blugij.grademanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

import ch.bbcag.blugij.grademanager.R;
import ch.bbcag.blugij.grademanager.sqlite.helper.DatabaseHelper;
import ch.bbcag.blugij.grademanager.sqlite.model.Fach;

/**
 * Created by blugij on 24.05.2016.
 */
public class FachAdapter extends ArrayAdapter<Fach> implements SpinnerAdapter {
    boolean isEditMask = false;
    public FachAdapter(Context context, int resource) {
        super(context, resource);
    }

    public FachAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public FachAdapter(Context context, int resource, Fach[] objects) {
        super(context, resource, objects);
    }

    public FachAdapter(Context context, int resource, int textViewResourceId, Fach[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public FachAdapter(Context context, int resource, List<Fach> objects, boolean isEditMask) {
        super(context, resource, objects);
        this.isEditMask = isEditMask;
    }

    public FachAdapter(Context context, int resource, int textViewResourceId, List<Fach> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Fach fach = getItem(position);

        if (isEditMask){
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view_item_one_column, parent, false);
            }

            TextView tvFirst = (TextView) convertView.findViewById(R.id.tveditfirst);

            tvFirst.setText(fach.getBezeichnung());
        } else {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view_item, parent, false);
            }

            DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
            double fachDurchschnitt = fach.getDurchschnitt(databaseHelper);

            TextView tvFirst = (TextView) convertView.findViewById(R.id.tvfirst);
            TextView tvSecond = (TextView) convertView.findViewById(R.id.tvsecond);
            TextView tvThird = (TextView) convertView.findViewById(R.id.tvthird);

            tvFirst.setText(fach.getBezeichnung());
            tvSecond.setText("");
            if (fachDurchschnitt == 0.0){
                tvThird.setText(getContext().getResources().getString(R.string.not_avaliable));
            } else {
                tvThird.setText(fachDurchschnitt + "");
            }

            if (fachDurchschnitt < 4.0 && fachDurchschnitt > 0.0){
                tvThird.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
            } else {
                tvThird.setTextColor(getContext().getResources().getColor(R.color.black));
            }
        }

        return convertView;
    }



    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Fach fach = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view_item_one_column, parent, false);
        }

        TextView tv = (TextView) convertView;

        tv.setText(fach.getBezeichnung());
        tv.setTextColor(getContext().getResources().getColor(R.color.black));

        return convertView;
    }
}
