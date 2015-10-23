package us.in_tune.in_tunex3;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Randy on 10/13/2015.
 */
public class ScheduleServiceRecycleViewSummary extends AppCompatActivity {

    Context mContext;
    FragmentManager mSupportFragmentManager;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_addr_entry);
        mSupportFragmentManager = getFragmentManager();
        mContext = getApplicationContext();

        mRecyclerView = (RecyclerView) findViewById(R.id.service_addr_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //retrieving package from the itemcart
        Intent firedIntent = getIntent();
        String gsString = firedIntent.getStringExtra(getString(R.string.itemcart));
        Gson gS = new Gson();
        final ItemCartPackage storedPackage = gS.fromJson(gsString, ItemCartPackage.class);
        //data ArrayList<NameAndPrice> items;
        System.out.println("storePackage to String: " + storedPackage.toString());


        //set adapter
        mAdapter = new SummaryCardViewAdapter(storedPackage.getItems());
        mRecyclerView.setAdapter(mAdapter);


        Button timeChanger = (Button) findViewById(R.id.pick_time_button);
        timeChanger.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Show the DatePickerDialog
                showTimePickerDialog(v);
            }
        });
        Button dateChanger = (Button) findViewById(R.id.pick_date_button);
        dateChanger.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Show the DatePickerDialog
                showDatePickerDialog(v);
            }
        });

        View summaryOvalFloat = findViewById(R.id.service_addr_entry);
        summaryOvalFloat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                SharedPreferences curPref = mContext.getSharedPreferences(getString(R.string.share_pref_name) ,Context.MODE_PRIVATE);
                String defaultValue = null;
                String pref_username = curPref.getString("username", defaultValue);
                String pref_password = curPref.getString("password", defaultValue);

                if(pref_username == null || pref_password == null){
                    Toast.makeText(mContext, "Pref Username or Password Invalid", Toast.LENGTH_SHORT).show();
                    return;
                }

                TextView timeText = (TextView) findViewById(R.id.time_text);
                TextView dateText = (TextView) findViewById(R.id.date_text);

                String timeString = timeText.getText().toString();
                String dateString = dateText.getText().toString();
                if(timeString == null || timeString.compareTo("")==0 || dateString == null || dateString.compareTo("")==0){
                    Toast.makeText(mContext, "Time or Date Invalid", Toast.LENGTH_SHORT).show();
                    return;
                }

                EditText streetname, city, state, zipcode;

                streetname = (EditText) findViewById(R.id.service_entry_street_addr);
                city = (EditText) findViewById(R.id.service_entry_city);
                state = (EditText) findViewById(R.id.service_entry_state);
                zipcode = (EditText) findViewById(R.id.service_entry_zipcode);

                String streetnameString, cityString, stateString, zipcodeString;

                streetnameString = streetname.getText().toString();
                cityString = city.getText().toString();
                stateString = state.getText().toString();
                zipcodeString = zipcode.getText().toString();

                String wholeAddr = streetnameString + ", " + cityString + ", " + stateString + ", " + zipcodeString;

                new HttpApptRequestEvent(pref_username, pref_password, storedPackage, dateString, timeString, wholeAddr).execute();

            }
        });

    }

    public void showTimePickerDialog(View v) {

        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(mSupportFragmentManager, "TimePicker");
    }

    public void showDatePickerDialog(View v){
        DialogFragment dateFragment = new DatePickerFragment();
        dateFragment.show(mSupportFragmentManager, "DatePicker");
    }

    private class HttpApptRequestEvent extends AsyncTask<String, Integer, String>{

        String username, password, dateappt, timeappt, service_addr;
        ItemCartPackage service_request = null;
        String url;

        public HttpApptRequestEvent(String username, String password, ItemCartPackage service_requested, String dateappt, String timeappt, String service_addr){
            this.username = username;
            this.password = password;
            this.service_request = service_requested;
            this.dateappt = dateappt;
            this.timeappt = timeappt;
            this.service_addr = service_addr;
        }

        /*
 * create json object map from base price, extraprice, servicequantity
 */
        public JSONObject interiorJSONElement(String serviceoption, String baseprice, String extraprice, String serviceQuantity){

            HashMap<String, String> retMap = new HashMap<String, String>();

            retMap.put("servicebaseprice", baseprice);
            retMap.put("serviceextraprice", extraprice);
            retMap.put("servicequantity", serviceQuantity);
            retMap.put("serviceoption", serviceoption);

            JSONObject newobject = new JSONObject(retMap);

            System.out.println("interior JSONElement" + newobject.toString());

            return newobject;
        }

        /*
         * create json object map from service name and mapobject
         */
        public JSONObject exteriorJSONElement(String servicename, JSONObject mapObject){
            HashMap<String, JSONObject> retObject = new HashMap<String, JSONObject>();

            retObject.put("servicename", mapObject);


            return new JSONObject(retObject);
        }

        @Override
        protected String doInBackground(String... params) {
            String charset = Charset.forName("UTF-8").name();
            url = "http://in-tune.us/client_appt_request.php";

            if(username == null || username.compareTo("")==0 || password==null || password.compareTo("")==0||service_request==null || dateappt==null
                    ||dateappt.compareTo("") == 0 || timeappt==null || timeappt.compareTo("")==0|| service_addr==null||service_addr.compareTo("")==0){
                return null;
            }

            JSONArray resultArray = new JSONArray();
            ArrayList<NameAndPrice> namePriceList = service_request.getItems();

            for(int i=0; i<namePriceList.size(); i++){

                resultArray.put(
                        exteriorJSONElement(namePriceList.get(i).getServiceName(), interiorJSONElement(namePriceList.get(i).getServiceOption(), namePriceList.get(i).getBasePrice(), namePriceList.get(i).getExtraPrice(),
                                namePriceList.get(i).getQuantity()
                        ))
                );

            }



            //storage string for jsonized result
            String jsonServiceRequestObject = resultArray.toString();

            try{

                URL request = new URL(url);
                String query = String.format("username=%s&password=%s&type=%s&datepref=%s&timepref=%s&serviceAddr=%s&servicerequest=%s",
                        URLEncoder.encode(username, charset), URLEncoder.encode(password, charset), URLEncoder.encode(new String("users"), charset),URLEncoder.encode(dateappt, charset),
                        URLEncoder.encode(timeappt, charset), URLEncoder.encode(service_addr, charset), URLEncoder.encode(jsonServiceRequestObject, charset)
                );

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

                System.out.println(newString);
                return newString;

            }catch(Exception e){

            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s == null){
                Toast.makeText(mContext, "Please Make Sure All Inputs Are Valid", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Integer retval = Integer.parseInt(s);
                if(retval == 2){
                    //success
                    Toast.makeText(mContext, "Appointment Successful, Awaiting Approval (24 Hours)", Toast.LENGTH_SHORT).show();
                }else if(retval == 3){
                    //failure
                    Toast.makeText(mContext, "Appointment Failed, Duplicate Appointment", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, "Please Make Sure All Inputs Are Valid", Toast.LENGTH_SHORT).show();
                }

            }catch(Exception e){
                Toast.makeText(mContext, "Impossible, Server Exception", Toast.LENGTH_SHORT).show();
            }



        }


    }

}
