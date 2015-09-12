package us.in_tune.in_tunex3;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.text.Html;

public class Login1 extends AppCompatActivity {

     static final int resetPassRequest = 1, registerAccountRequest=2, signinRequest=3, fbloginRequest=4, ggleLoginRequest=5;
    protected Context currentContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        currentContext = getApplicationContext();

        //forgot password activity activation.
        View forgotPass = findViewById(R.id.forgotPassword);
       forgotPass.setOnClickListener(new OnClickListener(){

           @Override
           public void onClick(View v){
               Intent resetPassIntent = new Intent(getApplicationContext(), ForgotPassword.class);
               startActivityForResult(resetPassIntent, resetPassRequest);
           }

       });

        //register activity activation
        View needAcccount = findViewById(R.id.needAnAccount);
        needAcccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(getApplicationContext(), NeedAccount.class),registerAccountRequest);

            }
        });

        View signIn = findViewById(R.id.SignInNow);
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                Bundle usernameAndPass = new Bundle();  //used to store and pass the user name and pass to the server

                String givenUserName, givenPassword, dbUsername, dbPassword, dbUrl;
                EditText usernamefield, passwordField;

                usernamefield = (EditText) findViewById(R.id.usernameinput);
                givenUserName = usernamefield.getText().toString();


                passwordField = (EditText) findViewById(R.id.passwordinput);
                givenPassword = passwordField.getText().toString();


                String[] newString = {givenUserName, givenPassword};
                //String[] newString = {"<script>location.href('http://www.hacked.com')</script>", givenPassword}; //this is a test

                System.out.println(givenUserName + " " + givenPassword);

                //username and password error handling

                Toast.makeText(currentContext, "Connecting to Server...", Toast.LENGTH_SHORT).show();

                new HttpTaskSignIn().execute(newString);


                //things that starts the main screen activity if it passes the checks
               // startActivityForResult(new Intent(getApplicationContext(), MainScreen.class), signinRequest);
            }
        });

        View facebooklog =  findViewById(R.id.loginFacebook);
        facebooklog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(currentContext, "Function Not Yet Supported", Toast.LENGTH_SHORT).show();
            }
        });


        View googlelog = findViewById(R.id.loginGoogle);
        googlelog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(currentContext, "Function Not Yet Supported", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 1:
                //reset request call back
                break;
            case 2:
                //register account request call back
                break;
            case 3:


                //normal sign in request call back
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpTaskSignIn extends AsyncTask<String, String, String > {

        private String usernameParam,passwordParam;
        private final String url = "http://in-tune.us/loginCheck.php";

        //returns null if result is valid, otherwise, return null
        @Override
        protected String doInBackground(String... params) {

            String charset = java.nio.charset.StandardCharsets.UTF_8.name();

            //have to escape html from usernameparam and password param in order to foil the attackers
            //username param

          //  System.out.println("i got here");

            try{
                usernameParam = Html.escapeHtml(params[0].toString()).toString();
                passwordParam = Html.escapeHtml(params[1].toString()).toString();
                //System.out.println("interior: " + params[0] + " " + params[1]);
                //System.out.println("interior: " + usernameParam + " " + passwordParam);
                if(usernameParam == null || usernameParam.compareTo("")==0){
                    return null;
                }
                if(passwordParam == null || passwordParam.compareTo("")==0){
                    return null;
                }


               // Toast.makeText(currentContext, usernameParam + " " + passwordParam, Toast.LENGTH_LONG );


                URL request = new URL(url);
                String query = String.format("loginInput=%s&passInput=%s", URLEncoder.encode(usernameParam, charset), URLEncoder.encode(passwordParam, charset));


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
            //do nothing
            if(result == null || result.compareTo("") ==0){
                Toast.makeText(currentContext, "Invalid Username or Password", Toast.LENGTH_LONG).show();

            }else{
                //TODO verify result
                Toast.makeText(currentContext, "Verified", Toast.LENGTH_LONG).show();
            }



        }
    }
}
