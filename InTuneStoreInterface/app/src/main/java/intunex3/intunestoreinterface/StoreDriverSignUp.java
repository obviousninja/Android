package intunex3.intunestoreinterface;

import android.content.Context;
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
 * Created by Randy on 10/4/2015.
 */
public class StoreDriverSignUp extends AppCompatActivity{

    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_driver_signup_activity);
        mContext = getApplicationContext();

        View storeDriverButton = findViewById(R.id.store_driver_registration);

        storeDriverButton.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        EditText drivername = (EditText) findViewById(R.id.store_driver_register);
                        EditText driverpassword = (EditText) findViewById(R.id.store_driver_registerPassword);

                        String drivernameString = drivername.getText().toString();
                        String driverpasswordString = driverpassword.getText().toString();

                        if(drivernameString == null || drivernameString.compareTo("") == 0 || driverpasswordString == null || driverpasswordString.compareTo("")==0){
                            Toast.makeText(mContext, "Driver Name Or Password Field Cannot Be Empty.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        new HttpDriverSignUpTask().execute(drivernameString, driverpasswordString);


                    }
                });

            }

        });

        View storeDriverCancel = findViewById(R.id.store_driver_back);

        storeDriverCancel.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }

        );
    }

    private class HttpDriverSignUpTask extends AsyncTask<String, Integer, String>{

        private final String url = "http://in-tune.us/register_driver.php";

        @Override
        protected String doInBackground(String... params) {
//use this for below API level 19
            String charset = Charset.forName("UTF-8").name();
            String username, password;

            if(params[0] == null || params[0].compareTo("") == 0 || params[1]==null || params[1].compareTo("")==0){
                return null;
            }

            username =  Html.escapeHtml(params[0]);
            password =  Html.escapeHtml(params[1]);

            System.out.println("username: " + username + " password: " + password);
            try{

                URL request = new URL(url);
                //php params loginName, loginPass, city
                String query = String.format("loginName=%s&loginPass=%s", URLEncoder.encode(username, charset)
                        , URLEncoder.encode(password, charset));

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

                newReader.close();
                input.close();
                output.close();
                conn.disconnect();
                return newString;

            }catch(Exception e){
                System.out.println("exceptioned here");
            }

            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            System.out.println("RESULT IS: " + s);
            int retInt = new Integer(s);


            if(retInt == 3){
                Toast.makeText(mContext, "Account Already Exist", Toast.LENGTH_LONG).show();
                EditText emailEText = (EditText) findViewById(R.id.registerEmail);
                EditText passEText = (EditText) findViewById(R.id.registerPassword);

                emailEText.setText("");
                passEText.setText("");
                return;
            }

            setResult(retInt);
            finish();
        }


    }
}
