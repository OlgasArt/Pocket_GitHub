package com.example.android.pocketgithub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

//created by OlgaS Art
// https://github.com/OlgasArt

public class RepoAdapter extends ArrayAdapter<Event> implements Filterable {


    List<Event> mData;
    List<Event> mStringFilterList;
    ValueFilter valueFilter;


    public RepoAdapter(Context context, List<Event> repos) {
        super(context, 0, repos);

        mData = repos;
        mStringFilterList = repos;
        getFilter();

    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Event getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Find the repository at the given position in the list of repositories
        Event currentRepo = getItem(position);

        TextView nameTextView = listItemView.findViewById(R.id.name_text_view);
        String name = currentRepo.getName();
        nameTextView.setText(name);


        String description = currentRepo.getDescription();
        TextView descriptionView = (TextView) listItemView.findViewById(R.id.description_text_view);
        descriptionView.setText(description);

        String date = currentRepo.getDate();
        TextView dateTextView = listItemView.findViewById(R.id.date_text_view);
        dateTextView.setText(date);

        ImageView imageView = listItemView.findViewById(R.id.condIcon);
        imageView.setVisibility(View.VISIBLE);

        Picasso.with(getContext()).load(currentRepo.getImage()).into(imageView);
        // Resize to the width specified maintaining aspect ratio
        //Picasso.with(getContext()).load(currentRepo.iconName).into(iconView);
        // Resize to the width specified maintaining aspect ratio
        // resize(1000, 0).into(iconView);

        //String score = currentRepo.getScore();
        TextView scoreTextView = listItemView.findViewById(R.id.score_text_view);

        String formattedScore = formatScore(currentRepo.getScore());
        // Display the formatted score of the current repository in that TextView
        scoreTextView.setText(formattedScore);

        // Return the list item view
        return listItemView;
    }

    /**
     * Return the formatted magnitude string showing 1 decimal place (i.e. "3.2")
     * from a decimal magnitude value.
     */
    private String formatScore(double score) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(score);
    }


    //Filtering results of the query

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<Event> filterList = new ArrayList<Event>();
                for (int i = 0; i < mStringFilterList.size(); i++) {

                    if ((mStringFilterList.get(i).getName().toLowerCase()).contains(constraint.toString().toLowerCase())) {

                        Event eventData = new Event(mStringFilterList.get(i).
                                getDescription(),
                                mStringFilterList.get(i).getName(),
                                mStringFilterList.get(i).getUrl(),
                                mStringFilterList.get(i).getDate(),
                                mStringFilterList.get(i).getImage(),
                                mStringFilterList.get(i).getScore()
                        );

                        filterList.add(eventData);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mData = (List<Event>) results.values;
            notifyDataSetChanged();
        }

    }
}