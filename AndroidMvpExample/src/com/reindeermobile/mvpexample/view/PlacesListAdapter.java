
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
    private final List<Result> placeResultList;
    private ViewHolder viewHolder;

    public PlacesListAdapter(final Context context, final int resource,
            final int textViewResourceId,
            final List<Result> placesResultList) {
        super(context, resource, textViewResourceId, placesResultList);
        this.placeResultList = placesResultList;
    }

    public PlacesListAdapter(final Context context, final int textViewResourceId,
            final List<Result> placesResultList) {
        super(context, textViewResourceId, placesResultList);
        this.placeResultList = placesResultList;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Result result = this.placeResultList.get(position);
        final String name = result.getName();
        final String vicinity = result.getVicinity();

        if (convertView == null) {
            final LayoutInflater inflater = (LayoutInflater) this.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_item_places, parent, false);

            this.viewHolder = new ViewHolder();
            this.viewHolder.nameTextView = (TextView) convertView
                    .findViewById(R.id.textviewListPlacesName);
            this.viewHolder.vicinityTextView = (TextView) convertView
                    .findViewById(R.id.textviewListPlacesVicinity);
            convertView.setTag(this.viewHolder);
        } else {
            this.viewHolder = (ViewHolder) convertView.getTag();
        }
        this.viewHolder.nameTextView.setText(name);
        this.viewHolder.vicinityTextView.setText(vicinity);
        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView vicinityTextView;
    }
}
