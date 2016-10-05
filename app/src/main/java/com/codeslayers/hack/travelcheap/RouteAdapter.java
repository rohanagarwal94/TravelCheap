package com.codeslayers.hack.travelcheap;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by mukulsoftwap on 9/23/2016.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.MyViewHolder> {

    private Activity activity;
    private List<Route> feedItems;
//    private AdapterCallback mAdapterCallback;
    private ArrayList<Step> commentItems;


    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView timestamp,statusMsg,url;

        public MyViewHolder(View view) {
            super(view);
            view.bringToFront();
            timestamp = (TextView) view
                    .findViewById(R.id.duration1);

//            getComments.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    FeedItem forComment=feedItems.get(getPosition());
////                    FeedItem forComment=feedItems.get(getAdapterPosition());   //Test this
//                    commentItems = new ArrayList<>();
//                    if(forComment.getCommentsSize()!=0) {
//                        commentItems.addAll(forComment.getCommentItems());
//                    }
//                    if(commentItems.size()!=0)
////                        showDialog(v);     //This is ins
//                        mAdapterCallback.onMethodCallback(commentItems);
//                    else
//                        Snackbar.make(v, "No Comments Yet :(", Snackbar.LENGTH_SHORT)
//                                .setAction("Action", null).show();
//
//                }
//            });
        }
    }

    public RouteAdapter(Activity activity,List<Route> feedItems) {
        this.feedItems=feedItems;
        this.activity=activity;
//        try {
//            this.mAdapterCallback = ((AdapterCallback) activity);
//        } catch (ClassCastException e) {
//            throw new ClassCastException("Activity must implement AdapterCallback.");
//        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.routeholder, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final Route item=feedItems.get(position);

        int a=item.getDuration();
        holder.timestamp.setText(String.valueOf(a));

//        // Checking for null feed url
//        if (item.getUrl() != null) {
//            holder.url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
//                    + item.getUrl() + "</a> "));
//
//            // Making url clickable
//            holder.url.setMovementMethod(LinkMovementMethod.getInstance());
//            holder.url.setVisibility(View.VISIBLE);
//        } else {
//            // url is null, remove from the view
//            holder.url.setVisibility(View.GONE);
//        }

    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }


//    public static interface AdapterCallback {
//        void onMethodCallback(ArrayList<Step> commentItems);
//    }


}