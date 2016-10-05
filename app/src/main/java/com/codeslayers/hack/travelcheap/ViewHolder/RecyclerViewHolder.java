package com.codeslayers.hack.travelcheap.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeslayers.hack.travelcheap.R;

/**
 * Created by mukulsoftwap on 9/23/2016.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder  {
    // View holder for gridview recycler view as we used in listview
    public TextView fare;
    public TextView duration;
    public TextView distance;
    public TextView from;
    public TextView to;
    public TextView  mode;
    public ImageView image;
    public ImageView image2;

    public RecyclerViewHolder(View view) {
        super(view);
        this.image=(ImageView)view.findViewById(R.id.iconN);
        this.image2=(ImageView)view.findViewById(R.id.image2);
        this.duration=(TextView)view.findViewById(R.id.duration);
        this.distance=(TextView)view.findViewById(R.id.distance);
        this.fare=(TextView)view.findViewById(R.id.fare);
        this.from=(TextView)view.findViewById(R.id.from);
        this.to=(TextView)view.findViewById(R.id.to);
        this.mode=(TextView)view.findViewById(R.id.mode);
    }
}