package intunex3.intunedriverinterface;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;


public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Context mContext;

    // Reference to the LocationManager
    private LocationManager mLocationManager;
    LocationListener locationListener;

    Location curLocation;
    private Long recordedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        mContext = getApplicationContext();

        //initiate time
        recordedTime = System.currentTimeMillis();
        curLocation =null;

        //get current location and periodically update as needed.
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
//check if location service is enabled, if not prompt user to enable it
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false){
            Toast.makeText(getApplicationContext(), "Please Turn on Location Service!", Toast.LENGTH_LONG).show();
            Intent requestLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(requestLocationIntent);
        }




        locationListener = new LocationListener() {
            public void onLocationChanged(final Location location) {

                //get the username
                SharedPreferences curPref = mContext.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
                String defaultvalue = null;
                final String usernameVal = curPref.getString(getString(R.string.curusername),defaultvalue);

                Long curTime = System.currentTimeMillis();
                long compTo = 30000;
                Long diff = Math.abs(curTime-recordedTime);
                if(diff.compareTo(compTo) >= 0){


                    //check if current location is within the tolerable limit
                    if(curLocation == null){
                        curLocation = location;
                    }else{


                        Double curLatitude = new Double(curLocation.getLatitude());
                        Double curLongitude = new Double(curLocation.getLongitude());

                        //simple check, if the curlatitude and curlongitude are the same, then we do not do update.
                        //may be very unlikely, hence refinement may be needed, but it works for now...
                        if(curLatitude.compareTo(location.getLatitude())==0 && curLongitude.compareTo(location.getLongitude()) == 0){
                            return;
                        }
                        //update the curlocation
                        curLocation = location;
                    }

                    //update the recordedTime to reflect the new instance
                    recordedTime = System.currentTimeMillis();

                    // Called when a new location is found by the network location provider.
                    Toast.makeText(mContext, "Username: "+ usernameVal + "Location Updated: Lat= "+location.getLatitude()+ " Lng= "+ location.getLongitude(), Toast.LENGTH_SHORT).show();



                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            new HttpLatLngTask(usernameVal, location.getLatitude(), location.getLongitude()).execute();
                        }
                    });

                }

                //update the current driver location on the map
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(new Double(location.getLatitude()), new Double(location.getLongitude())))
                        .title("You"));

                CameraPosition pos = new CameraPosition(new LatLng(new Double(location.getLatitude()), new Double(location.getLongitude())), 14, 45, 0 );
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));


            }


            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}


        };


        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(locationListener);

    }

   @Override
   public void onStop(){
       super.onStop();
       mLocationManager.removeUpdates(locationListener);

   }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();



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
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapactivity))
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
    }

    private class HttpLatLngTask extends AsyncTask<String, Integer, String> {

        String url ="http://in-tune.us/register_latlng.php";
        String username;
        Double lat;
        Double lng;

        HttpLatLngTask(String username, Double latitude, Double longitude){
            this.username = username;
            lat = latitude;
            lng = longitude;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            //registerLatLng($_POST["drivername"], $_POST["lat"], $_POST["lng"]);

            System.out.println("username is: " + username);
//use this for below API level 19
            String charset = Charset.forName("UTF-8").name();


            if(username == null || username.compareTo("")==0){
                return null;
            }

            try{
                URL request = new URL(url);
                String query = String.format("drivername=%s&lat=%s&lng=%s", URLEncoder.encode(username, charset), URLEncoder.encode(lat.toString(), charset), URLEncoder.encode(lng.toString(), charset));


                //opening connection and set properties for post
                HttpURLConnection conn = (HttpURLConnection) request.openConnection();
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept-Charset", charset);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);


                OutputStream output = conn.getOutputStream(); //get the output stream from the connection
                output.write(query.getBytes(charset));


                InputStream input = conn.getInputStream();
                InputStreamReader newReader = new InputStreamReader(input, charset);
                int newchar;
                String newString = new String();
                while((newchar =  newReader.read() ) != -1 ){

                    newString = newString + (char)newchar;

                }

                //TODO valid answer ONLY happens if the database has the username and the password,
                //so account registration must be implemented before you can test if valid input is properly implemented

                System.out.println(newString);
                input.close(); //close any persistent resources so asyncthread would close down
                newReader.close();
                output.close();
                conn.disconnect();
                return newString;
            }catch(Exception e){
                //do nothing it would just rebound to null value as return
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s == null){
                //impossible, that means location listener is having a wrong answer
                return;
            }

            int retInt = new Integer(s);
            if(retInt == 2) {

            }

        }



        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
