package com.codeslayers.hack.travelcheap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    int PLACE_AUTOCOMPLETE_REQUEST_CODE1 = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE2 = 2;

    private RequestQueue requestQueue;
    private String url, uberUrl;
    private ArrayList<Route> routes;
    private Polyline drivingPolyline;
    private Marker m1,m2;
    double startLatitude;
    double startLongitude;
    double endLatitude;
    double endLongitude;
    private TextView e1, e2;
    private Button b1;
    private ArrayList<Polyline> polylines;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng latLng;
    private LocationManager locationManager;
    private static final int REQUEST_CALL_LOCATION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .build();
        }

        SupportMapFragment supportMapFragment = ((SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map));
        supportMapFragment.getMapAsync(this);

        e1 = (TextView) findViewById(R.id.source);
        e2 = (TextView) findViewById(R.id.destination);

        e1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("IN").build();
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .setFilter(typeFilter)
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
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("IN").build();
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .setFilter(typeFilter)
                                    .build(MainActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE2);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        polylines=new ArrayList<>();
        routes=new ArrayList<>();
        b1=(Button)findViewById(R.id.button6);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i= new Intent(getApplicationContext(),FareListActivity.class);

//                float min=Integer.MAX_VALUE;
//                int index=0;
//                for(int j=0;j<routes.size();j++){
//                    if(routes.get(j).getFare()<min) {
//                        min=routes.get(j).getFare();
//                        index = j;
//                    }
//                }
                Bundle bundle=new Bundle();
                bundle.putParcelableArrayList("routes",routes);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    public void getDrivingRoute(String url, final String color){
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

    public void getRoute(final String mode, String url, final String color){
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
                            }
                            if(mode.equals("metro")){
                                routeFare=routeFare + getMetroFare(route.getSteps());
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
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (map != null) {
            mMap = map;
//            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//            Location mLastLocation = getLastKnownLocation();
//            if(latLng!=null)
//                latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            initCamera(new LatLng(28.591407,77.318911));
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
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
    }

    public void getUberRouteAndFare(final Route route, double startLatitude, double startLongitude,
                                    double endLatitude, double endLongitude) {
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

    }

    private float getMetroFare(ArrayList<Step> steps) {

        String arrivalName="";
        String departureName="";
        int i=0;
        for(;i<steps.size();i++){
            if(steps.get(i).getMode().contains("Metro")) {
                arrivalName = steps.get(i).getSource();
                System.out.println(arrivalName+" starting metro journey");
                break;
            }
        }

        for(i=steps.size()-1;i>=0;i--) {
            if (steps.get(i).getMode().contains("Metro")) {
                departureName = steps.get(i).getDestination();
                System.out.println(departureName+" ending metro journey");
                break;
            }
        }

        if(arrivalName.equals("")||departureName.equals(""))
            return 0;
        float fare=0;
        String json = null;
        int arrivalId=0,deparureId=0;
        arrivalName=arrivalName.replace("Metro Station","").trim();
        departureName=departureName.replace("Metro Station","").trim();
        boolean arrivalFlag=false,departureFlag=false;
        try {
            InputStream is = this.getAssets().open("metro.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return fare;
        }
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray metroArray = obj.getJSONArray("metro");
            System.out.println(arrivalName+" "+departureName);
            JSONObject jsonObject=metroArray.getJSONObject(0);
            for(int j=1;j<metroArray.length();j++){
                if(arrivalName.equalsIgnoreCase(jsonObject.getString(""+j))) {
                    arrivalId = j;
                    arrivalFlag=true;
                }
                if(departureName.equalsIgnoreCase(jsonObject.getString(""+j))) {
                    deparureId = j;
                    departureFlag=true;
                }
                if(arrivalFlag&&departureFlag)
                    break;
            }
            if(deparureId==0||arrivalId==0) {
                System.out.println("no metro is present in the route.");
                return fare;
            }
            JSONObject fareObject=metroArray.getJSONObject(deparureId);
            fare=fareObject.getInt(""+arrivalId);
            System.out.println("fare is "+fare);
        } catch (JSONException e) {
            e.printStackTrace();
            return fare;
        }

        return fare;
    }

    private float getBusFare(int distance1){
        float distance=distance1/1000;
        float fare=0;
        if(distance>=10)
            fare=15f;
        else if(distance>=4)
            fare=10f;
        else
            fare=5f;
        return fare;
    }

    private float getAutoFare(int distance1){
        float fare=25;
        float distance=distance1/1000;
        if(distance-2>0){
            fare =fare+8*(distance-2);
        }
        System.out.println("auto "+fare);
        return fare;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                e1.setText(place.getName());
                startLatitude=place.getLatLng().latitude;
                startLongitude=place.getLatLng().longitude;
                if(!e1.getText().toString().equals("")&&!e2.getText().toString().equals(""))
                    callApis(e1.getText().toString().replace(" ",""),e2.getText().toString().replace(" ",""));
                initCamera(place.getLatLng());
                if(m1!=null || m2!=null)
                {
                    m1.remove();
                    m2.remove();
                }
                m1= mMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                );
                Log.i("Place name", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("status message", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE2) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                e2.setText(place.getName());
                endLatitude=place.getLatLng().latitude;
                endLongitude=place.getLatLng().longitude;

                if(!e1.getText().toString().equals("")&&!e2.getText().toString().equals(""))
                    callApis(e1.getText().toString().replace(" ",""),e2.getText().toString().replace(" ",""));

                //zoom out the map
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

    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}
