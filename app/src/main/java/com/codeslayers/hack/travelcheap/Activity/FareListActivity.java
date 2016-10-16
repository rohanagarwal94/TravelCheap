package com.codeslayers.hack.travelcheap.Activity;

import android.app.Activity;
import android.os.Bundle;

import com.codeslayers.hack.travelcheap.R;

/**
 * Created by mukulsoftwap on 10/2/2016.
 */

public class FareListActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farelist);

        Bundle extras = getIntent().getExtras();
        System.out.println("new activity");
        /*ArrayList<Route> routes= extras.getParcelableArrayList("routes");
        System.out.println("no of routes are "+routes.size());
        for(int i=0;i<routes.size();i++){
            Route route=routes.get(i);
            System.out.println(route.getStartAddress()+" to "+route.getEndAddress()+" by "+route.getMode()+" "+route.getFare());
            ArrayList<Step> steps=route.getSteps();
            for(int j=0;j<steps.size();j++){
                Step step=steps.get(j);
                System.out.println(step.getMode()+" "+step.getFare());
            }
        }*/
    }
}
