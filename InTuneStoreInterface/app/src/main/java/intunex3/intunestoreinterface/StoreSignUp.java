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
 * Created by Randy on 10/2/2015.
 */
public class StoreSignUp extends AppCompatActivity {

    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_signup_activity);
        mContext = getApplicationContext();

        View registerButon = findViewById(R.id.registration);

        registerButon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //todo perform registration action

                                EditText usernameField, passwordField, addressField;
                                usernameField = (EditText) findViewById(R.id.registerEmail);
                                passwordField = (EditText) findViewById(R.id.registerPassword);
                                addressField = (EditText) findViewById(R.id.registerCity);

                                String usernameString, passwordString, addrString;
                                usernameString = usernameField.getText().toString();
                                passwordString = passwordField.getText().toString();
                                addrString = addressField.getText().toString();

                                if(usernameString == null || usernameString.compareTo("") == 0 || passwordString== null || passwordString.compareTo("")== 0 || addrString == null || addrString.compareTo("") == 0){
                                    Toast.makeText(mContext, "All Fields Are Required", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                //execute the task
                                new StoreSignUpTask().execute(usernameString, passwordString, addrString);


                            }
                        });
                    }
                }

        );
    }

    private class StoreSignUpTask extends AsyncTask<String, Integer, String>{

        private final String url = "http://in-tune.us/register_store.php";

        @Override
        protected String doInBackground(String... params) {

            //use this for below API level 19
            String charset = Charset.forName("UTF-8").name();
            String username, password, city;

            if(params[0] != null && params[1] != null && params[2] != null){
        /*
        Escape param strings and initialize
         */
                username = Html.escapeHtml(params[0]);
                password = Html.escapeHtml(params[1]);
                city = Html.escapeHtml(params[2]);

                try{
                    URL request = new URL(url);
                    //php params loginName, loginPass, city
                    String query = String.format("loginName=%s&loginPass=%s&city=%s", URLEncoder.encode(username, charset)
                            , URLEncoder.encode(password, charset),URLEncoder.encode(city, charset));

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

                }

            }




            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            System.out.println("Result is: THIS: " + result);
            int retInt = new Integer(result);
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
