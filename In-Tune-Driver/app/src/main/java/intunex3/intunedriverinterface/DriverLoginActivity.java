package intunex3.intunedriverinterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.text.Html;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class DriverLoginActivity extends AppCompatActivity {

    Context mContext;
    SharedPreferences curPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_login_activity);
        mContext = getApplicationContext();

        curPreference = mContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        View signinButton = findViewById(R.id.signin);

        signinButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //todo perform sign in action

                                EditText username = (EditText) findViewById(R.id.usernameinput);
                                EditText password = (EditText) findViewById(R.id.passwordinput);

                                String usernameString = username.getText().toString();
                                String passwordString = password.getText().toString();

                                System.out.println("before the null check");
                                System.out.println("usernameString: " + usernameString +"passwordString: "+ passwordString);
                                //cannot have username or password empty
                                if(usernameString == null || usernameString.compareTo("") == 0 || passwordString== null || passwordString.compareTo("")==0) {
                                    Toast.makeText(mContext, "Username or Password cannot be empty", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                System.out.println("after null check");
                                System.out.println("usernameString: " + usernameString + "passwordString: " + passwordString);
                                //execute the signin action with params from the edittext box
                                Toast.makeText(mContext, "Connecting...", Toast.LENGTH_SHORT).show();

                                new HttpTaskSignIn().execute(usernameString, passwordString);


                            }
                        });


                    }
                }

        );
    }

    /*
    * Driver Sign In Task
    * Essentially, it spawns an asyntask and do a database check
     */
    private class HttpTaskSignIn extends AsyncTask<String, String, String > {

        private String usernameParam,passwordParam, typeParam;
        private final String url = "http://in-tune.us/loginCheck.php";
        private Intent mainIntent = new Intent(mContext, MapsActivity.class);

        @Override
        protected void onPreExecute() {



        }
        //returns null if result is valid, otherwise, return null
        @Override
        protected String doInBackground(String... params) {

            //use this for below API level 19
            String charset = Charset.forName("UTF-8").name();

            //have to escape html from usernameparam and password param in order to foil the attackers
            //username param

            //  System.out.println("i got here");

            try{

                usernameParam = Html.escapeHtml(params[0].toString()).toString();
                passwordParam = Html.escapeHtml(params[1].toString()).toString();
                typeParam = "drivers";

                //System.out.println("interior: " + params[0] + " " + params[1]);
                //System.out.println("interior: " + usernameParam + " " + passwordParam);
                if(usernameParam == null || usernameParam.compareTo("")==0){
                    return null;
                }
                if(passwordParam == null || passwordParam.compareTo("")==0){
                    return null;
                }

                //passing the param from login1
                mainIntent.putExtra("usernamekey", usernameParam);


                URL request = new URL(url);
                String query = String.format("loginInput=%s&passInput=%s&type=%s", URLEncoder.encode(usernameParam, charset), URLEncoder.encode(passwordParam, charset), URLEncoder.encode(typeParam, charset));


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
            }catch(MalformedURLException e){
                //returns right away, because if the url is wrong, then there is really nothing to do

                e.printStackTrace();
            }catch(IOException e){

                e.printStackTrace();

            }
            return null; //exception thrown, nothing returned
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);


        }

        @Override
        protected void onPostExecute(String result){



            if(result == null){
                Toast.makeText(mContext, "Please Enter Username and Password", Toast.LENGTH_LONG).show();
                return;
            }

            int retInt = new Integer(result);
            if(retInt == 2){
                //correct username and password, so we should just start the next activity
                //we may need to pass in some variables to the intent before the transmission

                //pass variables here
                EditText usernamefield, passwordField;

                usernamefield = (EditText) findViewById(R.id.usernameinput);



                passwordField = (EditText) findViewById(R.id.passwordinput);

                //saving user name via sharePreference
                SharedPreferences.Editor curEdit = curPreference.edit();
                curEdit.putString(getString(R.string.curusername),usernamefield.getText().toString());
                curEdit.commit();

                usernamefield.setText("");
                passwordField.setText("");




                //starting activity
                startActivity(mainIntent);

            }else if(retInt == 3){
                //incorrect username and password
                Toast.makeText(mContext, "Invalid Username/Password", Toast.LENGTH_LONG).show();
            }else{
                //error
                Toast.makeText(mContext, "Error", Toast.LENGTH_LONG).show();
            }

        }
    }

}
