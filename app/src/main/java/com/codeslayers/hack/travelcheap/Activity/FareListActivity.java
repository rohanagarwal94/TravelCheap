package com.codeslayers.hack.travelcheap.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codeslayers.hack.travelcheap.Adapter.RecyclerView_Adapter;
import com.codeslayers.hack.travelcheap.R;
import com.codeslayers.hack.travelcheap.Model.Route;
import com.codeslayers.hack.travelcheap.Model.Step;

import java.util.ArrayList;

/**
 * Created by mukulsoftwap on 10/2/2016.
 */

public class FareListActivity extends Activity {
    private RecyclerView recyclerView;
    private RecyclerView_Adapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farelist);

        Bundle extras = getIntent().getExtras();
        System.out.println("new activity");
        Route route= extras.getParcelable("route");
        System.out.println(route.getStartAddress()+" to "+route.getEndAddress()+" by "+route.getMode()+" "+route.getFare());
        ArrayList<Step> steps=route.getSteps();
        for(int j=0;j<steps.size();j++){
            Step step=steps.get(j);
            System.out.println(step.getMode()+" "+step.getFare());
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        RecyclerView_Adapter adapter = new RecyclerView_Adapter(this, route.getSteps());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}
