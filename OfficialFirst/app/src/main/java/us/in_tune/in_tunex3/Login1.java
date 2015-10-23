package us.in_tune.in_tunex3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import android.text.Html;

import java.nio.charset.Charset;

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

                //TODO check username, it needs to be an email


                String[] newString = {givenUserName, givenPassword};
                //String[] newString = {"<script>location.href('http://www.hacked.com')</script>", givenPassword}; //this is a test

                System.out.println(givenUserName + " " + givenPassword);

                //username and password error handling

                Toast.makeText(currentContext, "Connecting to Server...", Toast.LENGTH_SHORT).show();


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){

                    new HttpTaskSignIn().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, newString);
                }else{

                    new HttpTaskSignIn().execute(newString);
                }

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
                System.out.println("Here is the requestCode: " + requestCode + "Result Code: " + resultCode);
                if(resultCode == -1) {
                    System.out.println("MYResult_OK");
                    Toast.makeText(currentContext, "MYRESULT_OK", Toast.LENGTH_LONG).show();
                }else if(resultCode == 0){
                    System.out.println("MYActivityCanceled");
                    Toast.makeText(currentContext, "Return", Toast.LENGTH_LONG).show();
                }else if(resultCode == 1){
                    System.out.println("RESULT_FIRST_USER");
                    Toast.makeText(currentContext, "result_first_user", Toast.LENGTH_LONG).show();
                }else if(resultCode == 2){
                    System.out.println("Success");
                    Toast.makeText(currentContext, "Registered", Toast.LENGTH_LONG).show();
                }else if(resultCode == 3){
                    System.out.println("Failure OMG FAILURE");
                    Toast.makeText(currentContext, "Not Registered", Toast.LENGTH_LONG).show();
                }else{
                    System.out.println("Erorr! SERVER OMG ERROR!");
                    Toast.makeText(currentContext, "", Toast.LENGTH_LONG).show();
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
       if (id == R.id.actionbar_my_reward) {
            Intent rewardIntent = new Intent(getApplicationContext(),MyReward.class);
           startActivity(rewardIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpTaskSignIn extends AsyncTask<String, String, String > {

        private String usernameParam,passwordParam, typeParam;
        private final String url = "http://in-tune.us/loginCheck.php";
        private Intent mainIntent = new Intent(currentContext, MainScreen.class);

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
                typeParam = "users";

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
                Toast.makeText(currentContext, "Please Enter Username and Password", Toast.LENGTH_LONG).show();
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

                //putting items in share preference
                SharedPreferences newPref = currentContext.getSharedPreferences(getString(R.string.share_pref_name),Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = newPref.edit();
                editor.putString(getString(R.string.username), usernamefield.getText().toString());
                editor.putString(getString(R.string.password), passwordField.getText().toString());
                editor.commit();


                usernamefield.setText("");
                passwordField.setText("");

                //starting activity
                startActivity(mainIntent);

            }else if(retInt == 3){
                //incorrect username and password
                Toast.makeText(currentContext, "Invalid Username/Password", Toast.LENGTH_LONG).show();
            }else{
                //error
                Toast.makeText(currentContext, "Error", Toast.LENGTH_LONG).show();
            }

        }
    }
}
