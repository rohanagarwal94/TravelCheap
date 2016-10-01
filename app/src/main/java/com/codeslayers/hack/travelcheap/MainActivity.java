package com.codeslayers.hack.travelcheap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    
    int PLACE_AUTOCOMPLETE_REQUEST_CODE1 = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE2 = 2;

    private RequestQueue requestQueue;
    private String url,uberUrl;
    private ArrayList<Route> routes;
    private Route autoRoute;

    TextView e1,e2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        e1=(TextView) findViewById(R.id.source);
        e2=(TextView)findViewById(R.id.destination);

        e1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(MainActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE1);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        e2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(MainActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE2);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        routes=new ArrayList<>();
        autoRoute=new Route();
        url="https://maps.googleapis.com/maps/api/directions/json?origin=shahdara&destination=preet%20vihar&mode=transit&transit_mode=bus&key=AIzaSyDuZ2e5qarM-fhwOoAS4WNum1k1Ow2lhLs";
        getRoute();
        url="https://maps.googleapis.com/maps/api/directions/json?origin=shahdara&destination=preet%20vihar&mode=transit&transit_mode=rail&key=AIzaSyDuZ2e5qarM-fhwOoAS4WNum1k1Ow2lhLs";
        getRoute();
        routes.add(autoRoute);

        Route uberRoute=new Route();
        uberRoute.setStartAddress(autoRoute.getStartAddress());
        uberRoute.setEndAddress(autoRoute.getEndAddress());
        double startLatitude=37.7759792;
        double startLongitude=-122.41823;
        double endLatitude=0;
        double endLongitude=0;
        getUberRouteAndFare(uberRoute,startLatitude,startLongitude, endLatitude, endLongitude);
    }

    public void getRoute(){
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jor = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            System.out.println(response.toString());
                            JSONArray routesArray = response.getJSONArray("routes");
                            System.out.println(routesArray.length());
                            if(routesArray.length()==0)
                                return;
                            JSONObject routeObject=routesArray.getJSONObject(0);
                            JSONArray legs = routeObject.getJSONArray("legs");
                            System.out.println("legs "+legs.length());
                            Route route=new Route();
                            JSONObject jsonObject = legs.getJSONObject(0);
                            String startAddress=jsonObject.getString("start_address");
                            String endAddress=jsonObject.getString("end_address");
                            JSONObject duration=jsonObject.getJSONObject("duration");
                            JSONObject distance = jsonObject.getJSONObject("distance");
                            int durationValue=duration.getInt("value");
                            int distanceValue=distance.getInt("value");
                            autoRoute.setFare(getAutoFare(distanceValue));
                            autoRoute.setEndAddress(endAddress);
                            autoRoute.setStartAddress(startAddress);
                            autoRoute.setDuration(durationValue);
                            autoRoute.setDistance(distanceValue);
                            route.setDistance(distanceValue);
                            route.setDuration(durationValue);
                            route.setEndAddress(startAddress);
                            route.setEndAddress(endAddress);
                            float routeFare=0;
                            JSONArray steps = jsonObject.getJSONArray("steps");

                            for(int j=0;j<steps.length();j++){
                                JSONObject jsonObject1 = steps.getJSONObject(j);
                                String travelMode=jsonObject1.getString("travel_mode");
                                System.out.println(travelMode);

                                JSONObject duration1=jsonObject1.getJSONObject("duration");
                                JSONObject distance1 = jsonObject1.getJSONObject("distance");
                                int durationValue1=duration1.getInt("value");
                                int distanceValue1=distance1.getInt("value");
                                float stepFare=0;
                                String type="Walking";
                                String arrivalName="";
                                String departureName="";
                                if(travelMode.equals("WALKING")!=true) {
                                    JSONObject transitDetails=jsonObject1.getJSONObject("transit_details");
                                    JSONObject line=transitDetails.getJSONObject("line");
                                    JSONObject vehicle=line.getJSONObject("vehicle");
                                    type=vehicle.getString("type");
                                    JSONObject arrivalStop = transitDetails.getJSONObject("arrival_stop");
                                    JSONObject departureStop = transitDetails.getJSONObject("departure_stop");
                                    arrivalName = arrivalStop.getString("name");
                                    departureName = departureStop.getString("name");
                                    if(type.equals("BUS")) {
                                        stepFare = getBusFare(distanceValue1);
                                        type="Bus "+line.getString("short_name");
                                    }
                                    else if(type.equals("SUBWAY")) {
                                        stepFare = getMetroFare(arrivalName,departureName);
                                        type="Metro "+line.getString("short_name");
                                    }
                                }
                                routeFare=routeFare+stepFare;
                                Step step=new Step(departureName,arrivalName,type,durationValue1,distanceValue1,stepFare);
                                route.addStep(step);
                                System.out.println(departureName+" to "+arrivalName+" via "+type+" price"+routeFare);
                            }
                            route.setFare(routeFare);
                            routes.add(route);
                            System.out.println("route fare "+routeFare);
                        }catch(JSONException e){e.printStackTrace();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Error in loading all posts.",Toast.LENGTH_LONG).show();
                        Log.e("Volley",error.toString());

                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                System.out.println("here it is");
                return headers;
            }

        };
        requestQueue.add(jor);

    }

    public void getUberRouteAndFare(final Route route, double startLatitude, double startLongitude, double endLatitude, double endLongitude){
        requestQueue = Volley.newRequestQueue(MainActivity.this);
        uberUrl="https://api.uber.com/v1/estimates/price?start_latitude="+startLatitude+"&start_longitude="+startLongitude+"&end_latitude="+endLatitude+"&end_longitude="+endLongitude;
        JsonObjectRequest jor = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            System.out.println(response.toString());
                            JSONArray prices=response.getJSONArray("prices");
                            if(prices.length()==0)
                                return;
                            int min=Integer.MAX_VALUE;
                            int minIndex=0;
                            for(int i=0;i<prices.length();i++){
                                JSONObject jsonObject=prices.getJSONObject(i);
                                int value=jsonObject.getInt("low_estimate");
                                if(value<min) {
                                    min = value;
                                    minIndex=i;
                                }
                            }
                            JSONObject price=prices.getJSONObject(minIndex);
                            route.setDuration(price.getInt("duration"));
                            route.setDistance(price.getInt("distance")*100);
                            route.setFare((price.getInt("high_estimate")+price.getInt("low_estimate"))/2);
                            routes.add(route);

                        }catch(JSONException e){e.printStackTrace();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(),"Error in loading all posts.",Toast.LENGTH_LONG).show();
                        Log.e("Volley",error.toString());

                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token a7Fdd_poaxMMIh38oBa_grYTlw8DSyCqkQwrWtop");
                return headers;
            }

        };
        requestQueue.add(jor);

    }

    private float getMetroFare(String arrivalName, String departureName) {
        float fare=0;
        return fare;
    }

    private float getBusFare(int distance){
        float fare=0;
        if(distance>=10)
            fare=15f;
        else if(distance>=4)
            fare=10f;
        else
        fare=5f;
        return fare;
    }

    private float getAutoFare(int distance){
        float fare=25;
        if(distance-2>0){
            fare =fare+8*(distance-2);
        }
        return fare;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                e1.setText(place.getName());
                Log.i("Place name", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("status message", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE2) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                e2.setText(place.getName());
                Log.i("Place name", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("status message", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


}
