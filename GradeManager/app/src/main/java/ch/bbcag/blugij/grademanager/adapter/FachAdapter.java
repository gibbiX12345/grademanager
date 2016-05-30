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
import ch.bbcag.blugij.grademanager.sqlite.model.Fach;
import ch.bbcag.blugij.grademanager.sqlite.model.Semester;

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

            TextView tvFirst = (TextView) convertView.findViewById(R.id.tvfirst);
            TextView tvSecond = (TextView) convertView.findViewById(R.id.tvsecond);
            TextView tvThird = (TextView) convertView.findViewById(R.id.tvthird);

            tvFirst.setText(fach.getBezeichnung());
            tvSecond.setText(fach.getGewichtung() + "x");
            tvThird.setText(fach.getDurchschnitt() + "");
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
