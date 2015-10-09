package intunex3.intunestoreinterface;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;


public class StoreLogin extends AppCompatActivity {

    Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_login_activity);
        mContext = getApplicationContext();

        View signInButton = findViewById(R.id.signin);
        signInButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //TODO Peformance sign in actions
                                EditText username = (EditText) findViewById(R.id.usernameinput);
                                EditText password = (EditText) findViewById(R.id.passwordinput);

                                String usernameString = username.getText().toString();
                                String passwordString = password.getText().toString();

                                if (usernameString == null || usernameString.compareTo("") == 0 || passwordString == null || passwordString.compareTo("") == 0) {
                                    Toast.makeText(mContext, "Username or Password cannot be empty", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                Toast.makeText(mContext, "Connecting...", Toast.LENGTH_SHORT).show();
                                new HttpTaskSignIn().execute(usernameString, passwordString);
                            }
                        });

                        //execute task

                    }
                }
        );


        View signUpButton = findViewById(R.id.signup);
        signUpButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //todo perform signup action
                        Intent signupintent = new Intent(mContext, StoreSignUp.class);

                        startActivity(signupintent);

                    }
                }

        );

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
                typeParam = "storemanagers";

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

                SharedPreferences newPref = mContext.getSharedPreferences(getString(R.string.sharefilename),Context.MODE_PRIVATE);
                SharedPreferences.Editor newEdit = newPref.edit();
                newEdit.putString("username", usernamefield.getText().toString());
                newEdit.putString("password", passwordField.getText().toString());
                newEdit.commit();

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
