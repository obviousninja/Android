package us.in_tune.in_tunex3;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Randy on 8/29/2015.
 */
public class MainScreen extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main_sheet);
        setContentView(R.layout.main_sheet_alternative);
        setUpMapIfNeeded();
        //get the goodie username from the previous starting activity
        Intent intent = getIntent();
        final String validatedUsername = intent.getStringExtra("usernamekey");

        mContext = getApplicationContext();
        /*Intent intent = getIntent();
        System.out.println("BLAH BLAH BLAH: " + intent.getStringExtra("usernamekey"));*/


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

            }
        });

        View billClick = findViewById(R.id.main_screen_mybill);
        billClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        View pendingapptClick = findViewById(R.id.main_screen_pendingappt);
        pendingapptClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        View myhistoryClick = findViewById(R.id.main_screen_myhistory);
        myhistoryClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


}
