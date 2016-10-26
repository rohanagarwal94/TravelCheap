package com.codeslayers.hack.travelcheap.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by mukulsoftwap on 9/23/2016.
 */

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.codeslayers.hack.travelcheap.Activity.FareListActivity;
import com.codeslayers.hack.travelcheap.Model.Route;
import com.codeslayers.hack.travelcheap.R;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.MyViewHolder> {

    private Activity activity;
    private List<Route> routes;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mode,fare,duration;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mode = (TextView) view.findViewById(R.id.mode);
            fare = (TextView) view.findViewById(R.id.fare);
            duration = (TextView) view.findViewById(R.id.duration);
        }

        @Override
        public void onClick(View v) {
            System.out.println("clicked");
            int position=getAdapterPosition();
            Intent intent=new Intent(activity, FareListActivity.class);
            Bundle bundle=new Bundle();
            bundle.putParcelable("route",routes.get(position));
            intent.putExtras(bundle);
            activity.startActivity(intent);
        }
    }

    public RouteAdapter(Activity activity,List<Route> routes) {
        this.routes = routes;
        this.activity=activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.routeholder, parent, false);
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RecyclerView recyclerView=(RecyclerView)activity.findViewById(R.id.recycler_view5);
//                int position=recyclerView.getChildLayoutPosition(v);
//                Intent intent=new Intent(activity, FareListActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putParcelable("route",routes.get(position));
//                intent.putExtras(bundle);
//                activity.startActivity(intent);
//            }
//        });
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final Route item= routes.get(position);
        System.out.println("no of items "+ routes.size());
        float a=(float)item.getDuration()/60;
        holder.duration.setText(a+" mins");
        holder.fare.setText(""+item.getFare());
        holder.mode.setText(item.getMode());
        System.out.println("here "+position);

    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

}