package intunex3.intunestoreinterface;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class MapsActivity extends AppCompatActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Context mContext;
    private final Handler handler = new Handler();
    Thread runningThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        mContext = getApplicationContext();



        //check if location service is enabled, if not prompt user to enable it
        LocationManager newManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(newManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false){
            Toast.makeText(getApplicationContext(), "Please Turn on Location Service!", Toast.LENGTH_LONG).show();
            Intent requestLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(requestLocationIntent);
        }



        View addDriver = findViewById(R.id.store_main_add_driver);
        addDriver.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fire off add driver activity
                Intent newIntent = new Intent(mContext, StoreDriverSignUp.class);
                startActivity(newIntent);
            }
        });

        View showDrivers = findViewById(R.id.store_main_showall_drivers);
        showDrivers.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        SharedPreferences newPref = mContext.getSharedPreferences(getString(R.string.sharefilename),Context.MODE_PRIVATE);
                        String defaultValue = null;
                        final String usernameVal = newPref.getString("username", defaultValue);
                        final String passwordVal = newPref.getString("password", defaultValue);

                        if(usernameVal == null || passwordVal == null){
                            Toast.makeText(mContext, "Error, username or password unspecified", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //new HttpGetAllDriverJSON(usernameVal, passwordVal, "storemanagers").execute();
                        if(runningThread == null) {
                            runningThread = new Thread(new HttpRetrieveDriversTask());
                            runningThread.start();
                        }
                    }
                });
            }
        });

        View stopShowDrivers = findViewById(R.id.store_main_stop_show_driver);
        stopShowDrivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stopping the thread here
                mMap.clear();
                if(runningThread == null){
                    return;
                }
                if (runningThread.isInterrupted() == false) {
                    System.out.println("TAG: Running Thread is not Interrupted, Now Interrupting");
                    runningThread.interrupt();
                } else {
                    System.out.println("TAG: Running Thread is Already Interrupted");
                    System.out.println("TAG: status: " + runningThread.isAlive());
                }

            }
        });


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
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(runningThread == null){
            return;
        }
        if (runningThread.isInterrupted() == false) {
            System.out.println("TAG: Running Thread is not Interrupted, Now Interrupting");
            runningThread.interrupt();
        } else {
            System.out.println("TAG: Running Thread is Already Interrupted");
            System.out.println("TAG: status: " + runningThread.isAlive());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 1:
                //reset request call back
                break;
            case 2:
                //register account request call back
                System.out.println("Here is the requestCode: " + requestCode + "Result Code: " + resultCode);
                if(resultCode == -1) {
                    System.out.println("MYResult_OK");
                    Toast.makeText(mContext, "MYRESULT_OK", Toast.LENGTH_LONG).show();
                }else if(resultCode == 0){
                    System.out.println("MYActivityCanceled");
                    Toast.makeText(mContext, "Return", Toast.LENGTH_LONG).show();
                }else if(resultCode == 1){
                    System.out.println("RESULT_FIRST_USER");
                    Toast.makeText(mContext, "result_first_user", Toast.LENGTH_LONG).show();
                }else if(resultCode == 2){
                    System.out.println("Success");
                    Toast.makeText(mContext, "Registered", Toast.LENGTH_LONG).show();
                }else if(resultCode == 3){
                    System.out.println("Failure OMG FAILURE");
                    Toast.makeText(mContext, "Not Registered", Toast.LENGTH_LONG).show();
                }else{
                    System.out.println("Erorr! SERVER OMG ERROR!");
                    Toast.makeText(mContext, "", Toast.LENGTH_LONG).show();
                }

                break;
            case 3:
                //normal sign in request call back, there is no sign in call back, once you signed in
                //that's it
                break;
            case 4:
                //facebook login request call back
                break;
            case 5:
                //google + login request call back
                break;
            default:
                break;
        }
    }


    private class HttpRetrieveDriversTask implements Runnable{



        @Override
        public void run() {

            String url = "http://in-tune.us/retrieve_all_drivers.php";

            SharedPreferences curPref = mContext.getSharedPreferences(getString(R.string.sharefilename), MODE_PRIVATE);
            String defaultValue = null;


            String store_username = curPref.getString("username", defaultValue);
            String store_password = curPref.getString("password", defaultValue);
            String user_type = "storemanagers";
            //use this for below API level 19
            String charset = Charset.forName("UTF-8").name();

            if(store_username == null || store_password == null){
                //impossible, case scenario no store username and password
                return;
            }


            try{
                while(Thread.currentThread().isInterrupted() == false){


                    URL request = new URL(url);
                    String query = String.format("loginInput=%s&passInput=%s&type=%s", URLEncoder.encode(store_username, charset), URLEncoder.encode(store_password, charset), URLEncoder.encode(user_type, charset));

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

                    //System.out.println(newString);
                    //clear the current map
                    //construct the jsonarray
                    handler.post(new Runnable(){
                       @Override
                        public void run(){
                           mMap.clear();
                       }
                    });

                    JSONObject newObject = new JSONObject(newString);

                    for(Integer i=0; i<newObject.length(); i++){
                        JSONObject curObject = newObject.getJSONObject(i.toString());
                        //System.out.println(curObject.toString());
                        final String driver_name = curObject.getString("drivername");
                        final String lati = curObject.getString("lat");
                        final String longi = curObject.getString("lng");

                        //System.out.println("DriverName: " + driver_name +  "Lati: " + lati +"Longi: "+ longi);
                        //do something with the JSON response String
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                //add the marker to the position

                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(new Double(lati), new Double(longi)))
                                        .title(driver_name));

                            }
                        });
                    }


                    input.close(); //close any persistent resources so asyncthread would close down
                    newReader.close();
                    output.close();
                    conn.disconnect();



                    System.out.println("Running Still Running");
                    runningThread.sleep(30000); //pause and wait for 30 seconds before checking again
                }
            }catch(InterruptedException e){
                System.out.println("TAG: Running Thread Interrupted");
                //reset the running thread to null so it can spawn again without spawning into a whole mess of threads
                runningThread = null;
            }catch(Exception e){
                e.printStackTrace();
            }


        }
    }
}
