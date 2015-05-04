package dodo.fahd.appli;

import android.app.Activity;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import android.location.Location;
import android.location.LocationManager;
import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;


public class MainActivity extends ActionBarActivity  implements LocationListener {

    // Google Map
    private GoogleMap googleMap;
    private int userIcon, hotelIcon,otherIcon,mosqueIcon,foodIcon,trainIcon;

    //location manager
    private LocationManager locMan;

    //user marker
    private Marker userMarker;

    private Marker[] placeMarkers;
    private final int MAX_PLACES = 20;

    private MarkerOptions[] places;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        placeMarkers = new Marker[MAX_PLACES];
        userIcon = R.drawable.yellow_point;
        hotelIcon = R.drawable.green_point;
        otherIcon = R.drawable.purple_point;
        mosqueIcon = R.drawable.blue_point;
        foodIcon = R.drawable.red_point;
        trainIcon = R.drawable.skyblue_point;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //find out if we already have it
        if(googleMap==null){
            //get the map
            googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
            //check in case map/ Google Play services not available
            if(googleMap!=null){
                //ok - proceed
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                //create marker array
                placeMarkers = new Marker[MAX_PLACES];
                //update location

                updatePlaces();
            }

        }

    }



    @Override
    public void onLocationChanged(Location location) {
        Log.v("MyMapActivity", "location changed");
        updatePlaces();
    }

    @Override
    public void onProviderDisabled(String provider){
        Log.v("MyMapActivity", "provider disabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.v("MyMapActivity", "provider enabled");
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.v("MyMapActivity", "status changed");
    }

    private void updatePlaces(){
        //get location manager
        locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //get last location
        Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double lat = lastLoc.getLatitude();
        double lng = lastLoc.getLongitude();
        //create LatLng
        LatLng lastLatLng = new LatLng(lat, lng);

        //remove any existing marker
        if(userMarker!=null) userMarker.remove();
        //create and set marker properties
        userMarker = googleMap.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title("Vous êtes là")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Votre dernière localisation"));

        //build places query string
        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?query=hotel&location="+lat+","+lng+
                "&radius=8000" +
                "&types=lodging"+
                "&key=AIzaSyAWh6lWf-gdzFu7JnMhJ-xkBSdOT73jWMU";//ADD KEY





        //execute query
        new GetPlaces().execute(placesSearchStr);

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, placesSearchStr, duration);

        //move to location
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);

    }

    private class GetPlaces extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... placesURL) {
            //fetch places
            new Thread(new Runnable() {
                public void run() {
                    // do something
                   // GooglePlaces client = new GooglePlaces("AIzaSyAWh6lWf-gdzFu7JnMhJ-xkBSdOT73jWMU");
                }
            }).start();

              final String LOG_TAG = "ExampleApp";

              final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

              final String TYPE_AUTOCOMPLETE = "/autocomplete";
              final String TYPE_DETAILS = "/details";
              final String TYPE_SEARCH = "/search";



            final String OUT_JSON = "/json";

            // KEY!
              final String API_KEY = "YOUR KEY";


            //build result as string
            StringBuilder placesBuilder = new StringBuilder();
            //process search parameter string(s)
            for (final String placeSearchURL : placesURL) {
           //     List<Place> places = client.getPlacesByQuery(placeSearchURL);




                HttpClient placesClient = new DefaultHttpClient();
                try {


                    //try to fetch the data

                    //HTTP Get receives URL string
                    HttpGet placesGet = new HttpGet(placeSearchURL);



                    //execute GET with Client - return response
                    HttpResponse placesResponse = placesClient.execute(placesGet);


                    //check response status
                    StatusLine placeSearchStatus = placesResponse.getStatusLine();
                    final int lala =  placeSearchStatus.getStatusCode();





                    //only carry on if response is OK
                    if (placeSearchStatus.getStatusCode() == 200) {


                        //get response entity
                        HttpEntity placesEntity = placesResponse.getEntity();
                        //get input stream setup
                        InputStream placesContent = placesEntity.getContent();
                        //create reader
                        InputStreamReader placesInput = new InputStreamReader(placesContent);
                        //use buffered reader to process
                        BufferedReader placesReader = new BufferedReader(placesInput);
                        //read a line at a time, append to string builder
                        String lineIn;
                        while ((lineIn = placesReader.readLine()) != null) {
                            placesBuilder.append(lineIn);
                        }
                    }


                }
                catch(Exception e){
                    e.printStackTrace();
                    final String dodo = e.getMessage();
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run(){
                            //update ui here
                            // display toast here

                            Toast.makeText(MainActivity.this, dodo, Toast.LENGTH_SHORT).show();

                        }
                    });
                }


            }


            return placesBuilder.toString();
        }



            //process data retrieved from doInBackground
        protected void onPostExecute(String result) {
            //parse place data returned from Google Places
            //remove existing markers
            if(placeMarkers!=null){
                for(int pm=0; pm<placeMarkers.length; pm++){
                    if(placeMarkers[pm]!=null)
                        placeMarkers[pm].remove();
                }
            }


            try {
                //parse JSON

                //create JSONObject, pass stinrg returned from doInBackground
                JSONObject resultObject = new JSONObject(result);
                //get "results" array
                JSONArray placesArray = resultObject.getJSONArray("results");
                //marker options for each place returned
                places = new MarkerOptions[placesArray.length()];
                //loop through places
                for (int p=0; p<placesArray.length(); p++) {
                    //parse each place
                    //if any values are missing we won't show the marker
                    boolean missingValue=false;
                    LatLng placeLL=null;
                    String placeName="";
                    String vicinity="";
                    int currIcon = otherIcon;
                    try{
                        //attempt to retrieve place data values
                        missingValue=false;
                        //get place at this index
                        JSONObject placeObject = placesArray.getJSONObject(p);
                        //get location section
                        JSONObject loc = placeObject.getJSONObject("geometry")
                                .getJSONObject("location");
                        //read lat lng
                        placeLL = new LatLng(Double.valueOf(loc.getString("lat")),
                                Double.valueOf(loc.getString("lng")));
                        //get types
                        JSONArray types = placeObject.getJSONArray("types");
                        //loop through types
                        for(int t=0; t<types.length(); t++){
                            //what type is it
                            String thisType=types.get(t).toString();
                            //check for particular types - set icons
                            if(thisType.contains("lodging")){
                                currIcon = hotelIcon;
                                break;
                            }

                            else if(thisType.contains("mosque")){
                                currIcon = mosqueIcon;
                                break;
                            }
                            else if(thisType.contains("food")){
                                currIcon = foodIcon;
                                break;
                            }

                            else if(thisType.contains("train_station")){
                                currIcon = foodIcon;
                                break;
                            }

                        }
                        //vicinity
                        vicinity = placeObject.getString("vicinity");
                        //name
                        placeName = placeObject.getString("name");
                    }
                    catch(JSONException jse){
                        Log.v("PLACES", "missing value");
                        missingValue=true;
                        jse.printStackTrace();
                    }
                    //if values missing we don't display
                    if(missingValue)	places[p]=null;
                    else if(placeName.toLowerCase().contains("hotel") || placeName.toLowerCase().contains("hôtel")
                            || placeName.toLowerCase().contains("motel") || placeName.toLowerCase().contains("auberge")
                            ||placeName.toLowerCase().contains("pension") ||placeName.toLowerCase().contains("hotellerie")
                            ||placeName.toLowerCase().contains("maison")|| placeName.toLowerCase().contains("palais") ||
                               placeName.toLowerCase().contains("camping"))
                        places[p]=new MarkerOptions()
                                .position(placeLL)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .title(placeName)
                                .snippet(vicinity);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if(places!=null && placeMarkers!=null){
                for(int p=0; p<places.length && p<placeMarkers.length; p++){
                    //will be null if a value was missing
                    if(places[p]!=null)
                        placeMarkers[p]=googleMap.addMarker(places[p]);
                }
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(googleMap!=null){
            locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 100, this);
        }    }


    @Override
    protected void onPause() {
        super.onPause();
        if(googleMap!=null){
            locMan.removeUpdates(this);
        }
    }

}