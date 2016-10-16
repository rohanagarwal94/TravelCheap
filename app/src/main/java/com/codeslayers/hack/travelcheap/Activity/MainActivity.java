package com.codeslayers.hack.travelcheap.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.codeslayers.hack.travelcheap.Model.Route;
import com.codeslayers.hack.travelcheap.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private List<Route> routes;
    private LocationManager locationManager;
    private GoogleMap mMap;
    private LatLng latLng;
    private ArrayList<Polyline> polylines;
    private Marker m1,m2;
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private Route autoRoute;

    private static final int REQUEST_CALL_LOCATION = 100;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE1 = 1;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE2 = 2;

    @Bind(R.id.recycler_view2)
    RecyclerView recyclerView;

    @Bind(R.id.source)
    TextView source;

    @Bind(R.id.destination)
    TextView destination;

    @OnClick(R.id.source)
    void source() throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("IN").build();
        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(typeFilter).build(MainActivity.this);
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE1);
    }

    @OnClick(R.id.destination)
    void destination() throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("IN").build();
        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(typeFilter).build(MainActivity.this);
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*try {
            String url = "start_latitude="+startLatitude+"&start_longitude="+startLongitude+"&end_latitude="+endLatitude+"&end_longitude="+endLongitude;
            routes = CheapApiService.getInstance().getFeedData(url);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (map != null) {
            mMap = map;
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Location mLastLocation = getLastKnownLocation();
            if(latLng!=null)
                latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            initCamera(new LatLng(28.591407,77.318911));
        }
    }

    private void initCamera(LatLng latlng) {
        CameraPosition position = CameraPosition.builder()
                .target(latlng)
                .zoom(18f)
                .bearing(0.0f)
                .tilt(40f)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);
        int permission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CALL_LOCATION);
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

    }

    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            int permission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CALL_LOCATION);
            }
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                source.setText(place.getName());
                startLatitude=place.getLatLng().latitude;
                startLongitude=place.getLatLng().longitude;
                if(!source.getText().toString().equals("")&&!destination.getText().toString().equals(""))
                    callApis(source.getText().toString().replace(" ",""),destination.getText().toString().replace(" ",""));
                    initCamera(place.getLatLng());
                if(m1!=null || m2!=null)
                {
                    m1.remove();
                    m2.remove();
                }
                m1= mMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                );
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE2) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                destination.setText(place.getName());
                endLatitude=place.getLatLng().latitude;
                endLongitude=place.getLatLng().longitude;
                if(!source.getText().toString().equals("")&&!destination.getText().toString().equals(""))
                    callApis(source.getText().toString().replace(" ",""),destination.getText().toString().replace(" ",""));

                if(m2!=null)
                    m2.remove();
                m2=mMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                );

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                builder.include(m1.getPosition());
                builder.include(m2.getPosition());

                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 90);
                mMap.moveCamera(cu);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void callApis(String source,String destination){
        routes.clear();
        String color="#6A1B9A";
        String url="https://maps.googleapis.com/maps/api/directions/json?origin="+source+"&destination="+destination+"&mode=transit&transit_mode=rail&key=AIzaSyDuZ2e5qarM-fhwOoAS4WNum1k1Ow2lhLs";
        getRoute("metro",url,color);

        color="#5D4037";
        url="https://maps.googleapis.com/maps/api/directions/json?origin="+source+"&destination="+destination+"&mode=transit&transit_mode=bus&key=AIzaSyDuZ2e5qarM-fhwOoAS4WNum1k1Ow2lhLs";
        getRoute("bus",url,color);

        url="https://maps.googleapis.com/maps/api/directions/json?origin="+source+"&destination="+destination+"&mode=driving&key=AIzaSyDuZ2e5qarM-fhwOoAS4WNum1k1Ow2lhLs";
        color="#E53935";
        getDrivingRoute(url,color);

        routes.add(autoRoute);
        autoRoute.setMode("auto");
        Route uberRoute=new Route();
        uberRoute.setStartAddress(autoRoute.getStartAddress());
        uberRoute.setEndAddress(autoRoute.getEndAddress());
        //getUberRouteAndFare(uberRoute,startLatitude,startLongitude, endLatitude, endLongitude);
        //routeAdapter.notifyDataSetChanged();
    }


    /*public void getRoute(final String mode, String url, final String color){
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jor = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray routesArray = response.getJSONArray("routes");
                            if(routesArray.length()==0)
                                return;
                            JSONObject routeObject=routesArray.getJSONObject(0);
                            JSONObject overviewPolyline=routeObject.getJSONObject("overview_polyline");
                            String encodedString=overviewPolyline.getString("points");
                            List<LatLng> list = decodePoly(encodedString);
                            if(polylines.size()==2) {
                                Polyline polyline1 = polylines.get(0);
                                Polyline polyline2 = polylines.get(1);
                                polyline1.remove();
                                polyline2.remove();
                                polylines.clear();
                            }
                            Polyline polyline = mMap.addPolyline(new PolylineOptions()
                                    .addAll(list)
                                    .width(12)
                                    .color(Color.parseColor(color))//Google maps violet color for metro and brown color for bus
                                    .geodesic(true)
                            );
                            polylines.add(polyline);
                            JSONArray legs = routeObject.getJSONArray("legs");
                            Route route=new Route();
                            JSONObject jsonObject = legs.getJSONObject(0);
                            String startAddress=jsonObject.getString("start_address");
                            String endAddress=jsonObject.getString("end_address");
                            System.out.println("Route from "+startAddress+" to "+endAddress+" via "+mode);
                            JSONObject duration=jsonObject.getJSONObject("duration");
                            JSONObject distance = jsonObject.getJSONObject("distance");
                            int durationValue=duration.getInt("value");
                            int distanceValue=distance.getInt("value");
                            route.setDistance(distanceValue);
                            route.setDuration(durationValue);
                            route.setStartAddress(startAddress);
                            route.setEndAddress(endAddress);
                            float routeFare=0;
                            JSONArray steps = jsonObject.getJSONArray("steps");

                            for(int j=0;j<steps.length();j++){
                                JSONObject jsonObject1 = steps.getJSONObject(j);
                                String travelMode=jsonObject1.getString("travel_mode");
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
                                        try {
                                            type = "Bus " + line.getString("short_name");
                                        }catch (JSONException e){
                                            type="Bus " + line.getString("name");
                                        }
                                    }
                                    else if(type.equals("SUBWAY")) {
                                        try {
                                            type = "Metro " + line.getString("short_name");
                                        }catch (JSONException e){
                                            type="Metro " + line.getString("name");
                                        }
                                    }
                                }
                                routeFare=routeFare+stepFare;
                                Step step=new Step(departureName,arrivalName,type,durationValue1,distanceValue1,stepFare);
                                route.addStep(step);
                                System.out.println(departureName+" to "+arrivalName+" via "+type+" price "+stepFare);
                            }
                            if(mode.equals("metro")){
                                routeFare=routeFare + getMetroFare(route.getSteps());
                                System.out.println("Route from "+startAddress+" to "+endAddress);
                            }
                            route.setFare(routeFare);
                            route.setMode(mode);
                            routes.add(route);
                            System.out.println("route fare "+routeFare);
                        }catch(JSONException e){e.printStackTrace();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Error in internet connection.",Toast.LENGTH_LONG).show();
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
    }*/

    /*public void getDrivingRoute(String url, final String color){
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
                            JSONObject overviewPolyline=routeObject.getJSONObject("overview_polyline");
                            String encodedString=overviewPolyline.getString("points");
                            List<LatLng> list = decodePoly(encodedString);
                            if(drivingPolyline!=null)
                                drivingPolyline.remove();
                            drivingPolyline = mMap.addPolyline(new PolylineOptions()
                                    .addAll(list)
                                    .width(12)
                                    .color(Color.parseColor(color))//Google maps red color
                                    .geodesic(true)
                            );
                            JSONArray legs = routeObject.getJSONArray("legs");
                            JSONObject jsonObject = legs.getJSONObject(0);
                            String startAddress=jsonObject.getString("start_address");
                            String endAddress=jsonObject.getString("end_address");
                            System.out.println("Route from "+startAddress+" to "+endAddress+" via auto");
                            JSONObject duration=jsonObject.getJSONObject("duration");
                            JSONObject distance = jsonObject.getJSONObject("distance");
                            int durationValue=duration.getInt("value");
                            int distanceValue=distance.getInt("value");
                            Route autoRoute=new Route();
                            Route uberRoute=new Route();
                            uberRoute.setStartAddress(startAddress);
                            uberRoute.setEndAddress(endAddress);
                            getUberRouteAndFare(uberRoute,startLatitude,startLongitude, endLatitude, endLongitude);
                            autoRoute.setFare(getAutoFare(distanceValue));
                            autoRoute.setEndAddress(endAddress);
                            autoRoute.setStartAddress(startAddress);
                            autoRoute.setDuration(durationValue);
                            autoRoute.setDistance(distanceValue);
                            autoRoute.setMode("auto");
                            routes.add(autoRoute);

                            System.out.println("Route from "+startAddress+" to "+endAddress+" via uber");

                        }catch(JSONException e){e.printStackTrace();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Error in internet connection.",Toast.LENGTH_LONG).show();
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

    public void getUberRouteAndFare(final Route route, double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        requestQueue = Volley.newRequestQueue(MainActivity.this);
        uberUrl="https://api.uber.com/v1/estimates/price?start_latitude="+startLatitude+"&start_longitude="+startLongitude+"&end_latitude="+endLatitude+"&end_longitude="+endLongitude;
        JsonObjectRequest jor = new JsonObjectRequest(uberUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
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
                            route.setMode("uber");
                            System.out.println((price.getInt("high_estimate")+price.getInt("low_estimate"))/2);
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

    }*/

}