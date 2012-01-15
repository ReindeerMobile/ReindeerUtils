
package com.reindeermobile.mvpexample.view;

import com.reindeermobile.mvpexample.R;
import com.reindeermobile.mvpexample.entities.Result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PlacesListAdapter extends ArrayAdapter<Result> {
    private List<Result> placeResultList;
    private ViewHolder viewHolder;

    public PlacesListAdapter(Context context, int resource, int textViewResourceId,
            List<Result> placesResultList) {
        super(context, resource, textViewResourceId, placesResultList);
        this.placeResultList = placesResultList;
    }

    public PlacesListAdapter(Context context, int textViewResourceId,
            List<Result> placesResultList) {
        super(context, textViewResourceId, placesResultList);
        this.placeResultList = placesResultList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Result result = placeResultList.get(position);
        String name = result.getName();
        String vicinity = result.getVicinity();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_item_places, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.nameTextView = (TextView) convertView
                    .findViewById(R.id.textviewListPlacesName);
            viewHolder.vicinityTextView = (TextView) convertView
                    .findViewById(R.id.textviewListPlacesVicinity);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.nameTextView.setText(name);
        viewHolder.vicinityTextView.setText(vicinity);
        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView vicinityTextView;
    }
}
