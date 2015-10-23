package intunex3.intunestoreinterface;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Created by Randy on 10/15/2015.
 *
 * what is a service option? below is a service option format
 * Service Option = ServiceOptionText + # + how_much_cost_extra_dollar_amount;
 *
 * user inputted service option cannot contain "#" or there will be some dire ramifications
 */
public class StoreAddServiceOption extends AppCompatActivity {

    Context mContext;
    Intent prevIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_service_options);
        mContext = getApplicationContext();
        prevIntent = getIntent();



        View finishButton = findViewById(R.id.add_service_item_finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                setResult(10);
                finish();
            }

        });


        View addButton = findViewById(R.id.store_service_option_item_add);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String e_service_name = prevIntent.getStringExtra(getString(R.string.service_name));
                String e_service_price =  prevIntent.getStringExtra(getString(R.string.service_price));
                String e_service_desc =  prevIntent.getStringExtra(getString(R.string.service_description));
                String e_service_dura = prevIntent.getStringExtra(getString(R.string.service_duration));

                if(e_service_desc == null || e_service_name == null || e_service_price == null || e_service_dura==null){
                    //fatal error
                    Toast.makeText(mContext, "Impossible. desc/name/price invalid", Toast.LENGTH_SHORT).show();
                    return;
                }

                EditText serviceOptionText = (EditText) findViewById(R.id.store_serviceoption_input);
                String serviceOption = serviceOptionText.getText().toString();

                EditText serviceOptionMultiplierText = (EditText) findViewById(R.id.store_serviceoption_pricemultiplier);
                String servicOptionMultiplier = serviceOptionMultiplierText.getText().toString();

                if(serviceOption == null || serviceOption.compareTo("")==0 || serviceOptionMultiplierText == null || servicOptionMultiplier.compareTo("") == 0){
                    Toast.makeText(mContext, "Both Service Option and Its Multiplier Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }



                new HttpAddItemRequest(e_service_name, e_service_desc, e_service_price, e_service_dura,serviceOption+"#"+servicOptionMultiplier).execute();

            }

        });


    }

    private class HttpAddItemRequest extends AsyncTask<String, Integer, String>{


        String serviceName, serviceDescription, servicePrice, serviceDuration, serviceOption;


        public HttpAddItemRequest(String serviceName, String serviceDescription, String servicePrice, String serviceDuration ,String serviceOption){

            this.serviceName = Html.escapeHtml(serviceName);
            this.serviceDescription = Html.escapeHtml(serviceDescription);
            this.servicePrice = Html.escapeHtml(servicePrice);
            this.serviceOption = Html.escapeHtml(serviceOption);
            this.serviceDuration = Html.escapeHtml(serviceDuration);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String url = "http://in-tune.us/addservice.php";
            String charset = Charset.forName("UTF-8").name();

            //note: all instance variables instantiated by construction cannot possibly be null or empty string, hence there is no need for any checking
            try{
                URL request = new URL(url);
                String query = String.format("serviceName=%s&servicedesc=%s&serviceprice=%s&serviceoption=%s&serviceDuration=%s",
                        URLEncoder.encode(serviceName, charset), URLEncoder.encode(serviceDescription, charset), URLEncoder.encode(servicePrice, charset),
                        URLEncoder.encode(serviceOption, charset), URLEncoder.encode(serviceDuration,charset));

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

                //free up resources
                newReader.close();
                input.close();
                output.close();
                conn.disconnect();

                return newString;

            }catch(Exception e){
                //malformed url isn't going to happen
                e.printStackTrace();
            }




            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s == null){
                //exceptioned
                Toast.makeText(mContext,"critical error occurred, NULL Value Request", Toast.LENGTH_SHORT).show();
                return;
            }

            int retVal = Integer.parseInt(s);
            if(retVal == 2){
                Toast.makeText(mContext, "Service Item Added", Toast.LENGTH_SHORT).show();
                return;
            }else if(retVal == 3){
                Toast.makeText(mContext, "Service Option Exist or Incorrect Service Option", Toast.LENGTH_SHORT).show();
                return;
            }else if(retVal == 4){
                Toast.makeText(mContext, "Entries Cannot Be Empty", Toast.LENGTH_SHORT).show();
                return;
            }
            //impossible
            Toast.makeText(mContext, "Impossible, Server Error", Toast.LENGTH_SHORT).show();
            return;



        }
    }

}
