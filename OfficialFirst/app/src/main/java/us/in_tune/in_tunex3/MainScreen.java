package us.in_tune.in_tunex3;


import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import us.in_tune.in_tunex3.GeoLibrary;

/**
 * Created by Randy on 8/29/2015.
 */

//Google Place Web Server API Key: AIzaSyCURSD_AriIM3vGRqkYok3J9EJ0oszjG0U

public class MainScreen extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener  {

    /*
      // Google Places serach url's
https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4&key=AIzaSyCURSD_AriIM3vGRqkYok3J9EJ0oszjG0U

     */

    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=38.909567,-77.028816&radius=8046.72&types=car_repair&sensor=true&key=AIzaSyCURSD_AriIM3vGRqkYok3J9EJ0oszjG0U
    // Ask Query by Distance in Meters

//https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=38.909567,-77.028816&rankby=distance&types=car_repair&sensor=true&key=AIzaSyCURSD_AriIM3vGRqkYok3J9EJ0oszjG0U
    // Ask Query By having the google return the rank base on distance CANNOT SPECIFY DISTANCE

   // https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyCURSD_AriIM3vGRqkYok3J9EJ0oszjG0U
    //android geo-coding data

    private final String TAG = "MAIN_SCREEN_ALTERNATIVE";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Context mContext;
    private static final String ServerAPIKEY = "AIzaSyCURSD_AriIM3vGRqkYok3J9EJ0oszjG0U";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //checking network, if it is connected or not.
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if(isConnected) {

            setContentView(R.layout.main_sheet_alternative);
        }else{
            //TODO FLOOD THIS WITH ANOTHER LAYOUT THAT DOESN"T REQURIE NETWORK CONNECTION
            setContentView(R.layout.no_network_connected);
        }

        // Get a reference to the Spinner
        final Spinner spinner = (Spinner) findViewById(R.id.main_search_spinner);

        // Create an Adapter that holds a list of colors
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.radius, R.layout.search_bar_dropdown);

        spinner.setAdapter(adapter);
        spinner.setSelection(2);
        spinner.setSelected(true);



        //TODO flood the main_screen_alternative layout with a search bar that includes
        //address bar, radius bar, and search button


        setUpMapIfNeeded();




        //get the goodie username from the previous starting activity
        Intent intent = getIntent();
        final String validatedUsername = intent.getStringExtra("usernamekey");

        mContext = getApplicationContext();
        /*Intent intent = getIntent();
        System.out.println("BLAH BLAH BLAH: " + intent.getStringExtra("usernamekey"));*/

        //check if location service is enabled, if not prompt user to enable it
        LocationManager newManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(newManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false){
            Toast.makeText(getApplicationContext(), "Please Turn on Location Service!", Toast.LENGTH_LONG).show();
            Intent requestLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(requestLocationIntent);
        }

        View profileClick = findViewById(R.id.main_screen_myprofile);
        profileClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something

                //construct an intent
                Intent profileIntent = new Intent(mContext, MyProfile.class);
                //squeeze in the goodie username
                profileIntent.putExtra("usernamekey1", validatedUsername);
                startActivity(profileIntent);

            }
        });

        View garageClick = findViewById(R.id.main_screen_mygarage);
        garageClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                Intent garageIntent = new Intent(mContext, Garage.class);
                garageIntent.putExtra("usernamekey1", validatedUsername);

                startActivity(garageIntent);
            }
        });

        View billClick = findViewById(R.id.main_screen_mybill);
        billClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //has to retrieve items from the SQL server

            }
        });

        View pendingapptClick = findViewById(R.id.main_screen_pendingappt);
        pendingapptClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //has to retrieve items from the SQL server
            }
        });

        View myhistoryClick = findViewById(R.id.main_screen_myhistory);
        myhistoryClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //has to retrieve items from the SQL server

            }
        });

        Button searchButton = (Button) findViewById(R.id.main_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get address
                EditText addressText = (EditText) findViewById(R.id.main_address);
                final String currentAddressText = addressText.getText().toString();
                if(currentAddressText.isEmpty() || currentAddressText == null){
                    Toast.makeText(mContext, "Please Enter A Valid Address", Toast.LENGTH_LONG).show();
                    return;
                }

                //get item from spinner
                String spinnerSelectionText = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();

                //output both the current address text and spinner text for debugging purpose
                System.out.println("Address: " + currentAddressText + "Radius" + spinnerSelectionText);


                //convert miles to meters
                final Double radiusInMeter = 1609.34*(Integer.parseInt(spinnerSelectionText));
                System.out.print("Mile to Meter: " + radiusInMeter);



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //new GetPlacesAroundTask(mContext, (new GeoLibrary()).latLngRankByDistance(result.getLatitude(), result.getLongitude()), mMap).execute();

                        //find geolocation first
                        //not using threadpoolexecutor ... because i got bored of using it
                        new GetLatLngByAddress(mContext, (new GeoLibrary().geocodeAddress(currentAddressText)), mMap, radiusInMeter).execute();



                    }
                });
                /*

                 */
            }
        });





        //everything is populated, now we add the markers
        //TODO Execute Order66

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){

            new GetCurrentLocationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            new GetCurrentLocationTask().execute();
        }
        //TODO support the search bar function here



    }

    //THIS IS ORDER 66
    //Order 66 finds the current latitute and longitude and place marker on the current latitute and longitude
    //it then finds the surrounding autoshops in X mile radius and place marker on them too
    private class GetCurrentLocationTask extends AsyncTask<Void, Integer, Location>{
        Location curLocation;
        // Reference to the LocationManager
        private LocationManager mLocationManager;
        LocationListener locationListener;

        @Override
        protected void onPreExecute() {

            //getting current location
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Define a listener that responds to location updates

            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    curLocation = location;
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };


           //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


        }


        //derive latitude and longitude
        //returns a location reading, returns null on error
        @Override
        protected Location doInBackground(Void... params) {

            //we wait until we get a read on the location, then we remove the location listener
        try{



         while(curLocation == null){
             Thread.currentThread().sleep(333);
         }
            //unregister the listener so there can be no more updates
            mLocationManager.removeUpdates(locationListener);
            return curLocation;

        }catch(InterruptedException e){
            //do nothing and pass the responsibility to outerscope
        }


            return null;
        }

        //set marker on the google map
        @Override
        protected void onPostExecute(Location result){

            if(result == null){
                return;
            }

           //add the marker to the position
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(result.getLatitude(), result.getLongitude()))
                    .title("You"));

            //Adjust camera position
            CameraPosition pos = new CameraPosition(new LatLng(result.getLatitude(), result.getLongitude()), 14, 45, 0 );
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
            //plots all auto repair shops around the current location


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){

                new GetPlacesAroundTask(mContext, (new GeoLibrary()).latLngRankByDistance(result.getLatitude(), result.getLongitude()), mMap).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }else{

                new GetPlacesAroundTask(mContext, (new GeoLibrary()).latLngRankByDistance(result.getLatitude(), result.getLongitude()), mMap).execute();
            }


        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services! PLACE API
        // The good stuff goes here.
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted. PLACE API
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that  PLACE API
        // may occur while attempting to connect with Google.
        //
        // More about this in the 'Handle Connection Failures' section.

    }
    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        //set custom info window
        mMap.setInfoWindowAdapter(new InTuneInfoWindow(getLayoutInflater()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.generic_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }




}
