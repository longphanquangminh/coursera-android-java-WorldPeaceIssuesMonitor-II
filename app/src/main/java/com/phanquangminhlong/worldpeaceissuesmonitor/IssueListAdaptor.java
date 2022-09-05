//
// IssueListAdapter.java
//
// This object render the data in our issues ArrayList to the screen
//
// This file is part of the course "Build a Firebase Android Application"
//
// Written by Harrison Kong @ coursera.org
//

package com.phanquangminhlong.worldpeaceissuesmonitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class IssueListAdaptor extends BaseAdapter {

    private Context context;
    private ArrayList<Issue> items;

    // constructor

    public IssueListAdaptor (Context context, ArrayList<Issue> items) {
        this.context = context;
        this.items = items;
    }

    // interface

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return items != null ? items.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        class ViewHolder {
            ImageView severityImageView;
            ImageView resolvedImageView;
            TextView  descriptionTextView;

            public ViewHolder(ImageView severityImageView, ImageView resolvedImageView,
                              TextView descriptionTextView) {
                this.severityImageView = severityImageView;
                this.resolvedImageView = resolvedImageView;
                this.descriptionTextView = descriptionTextView;
            }
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.issue_list_item, parent, false);

            // build the view holder for future use

            ViewHolder holder = new ViewHolder(
                    (ImageView)convertView.findViewById(R.id.severityImage),
                    (ImageView)convertView.findViewById(R.id.resolvedImage),
                    (TextView)convertView.findViewById(R.id.issueDescription)
            );
            convertView.setTag(holder);
        }

        ViewHolder viewHolder = (ViewHolder)convertView.getTag();

        final Issue currentItem = (Issue)getItem(position);

            // process the severity

            int severityIconResourceId;

            switch (currentItem.getSeverity()) {
                case "minor" :
                    severityIconResourceId = R.drawable.yellow_led;
                    break;
                case "moderate" :
                    severityIconResourceId = R.drawable.orange_led;
                    break;
                case "major" :
                    severityIconResourceId = R.drawable.red_led;
                    break;
                default:
                    severityIconResourceId = R.drawable.black_question_mark_led;
            }

            viewHolder.severityImageView.setImageResource(severityIconResourceId);

            // process the resolved status

            int resolvedIconResourceID;

            switch (currentItem.getResolved()) {
                case "yes":
                    resolvedIconResourceID = R.drawable.green_check_mark;
                    break;
                case "no":
                    resolvedIconResourceID = R.drawable.red_hourglass;
                    break;
                default:
                    resolvedIconResourceID = R.drawable.black_question_mark_led;
            }

            viewHolder.resolvedImageView.setImageResource(resolvedIconResourceID);

            // process the description

            viewHolder.descriptionTextView.setText(currentItem.getDescription());

            // attach a click listener for the resolved Status image

            viewHolder.resolvedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof MainActivity) {
                       ((MainActivity) context).openStatusToggleDialog(position);
                    }
                }
            });

        return convertView;
    }
}
