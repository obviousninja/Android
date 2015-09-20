package us.in_tune.in_tunex3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Context;
import android.widget.EditText;
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

/**
 * Created by Randy on 8/29/2015.
 */
public class NeedAccount extends AppCompatActivity {

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.need_accountsheet);
        mContext = getApplicationContext();

        View registerAccount = (View) findViewById(R.id.registration);
        registerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText emailEText = (EditText) findViewById(R.id.registerEmail);
                EditText passEText = (EditText) findViewById(R.id.registerPassword);
                EditText phoneEText = (EditText) findViewById(R.id.registerPhoneNumber);

                //extracted email, password and phone from edit text field
                String exEmail, exPassword, exPhone;
                exEmail = emailEText.getText().toString();
                exPassword = passEText.getText().toString();
                exPhone = phoneEText.getText().toString();


                new CreateAccountTask().execute(exEmail,exPhone,exPassword);
            }
        });

    }

    //sub class for interacting with server and creating an account
    private class CreateAccountTask extends AsyncTask<String, Integer, String> {

        private String emailString, passString, phoneString;
        private final String url = "http://in-tune.us/register.php";

        @Override
        protected void onPreExecute() {

        }

        //returns null if the output is invalid, otherwise return a string
        @Override
        protected String doInBackground(String... params) {

            String charset = java.nio.charset.StandardCharsets.UTF_8.name();

            //process params
            emailString = Html.escapeHtml(params[0].toString()).toString();
            phoneString = Html.escapeHtml(params[1].toString()).toString();
            passString= Html.escapeHtml(params[2].toString()).toString();
            System.out.println(emailString+ " " +passString + " " + phoneString);

            //checking params
            if(emailString == null || emailString.compareTo("")==0)
                return null;
            if(passString == null || passString.compareTo("")==0)
                return null;
            if(phoneString==null || phoneString.compareTo("")==0)
                return null;

            //initiate connection
            try{
                URL request = new URL(url);
                //php params emailString, passString, phoneString
                String query = String.format("emailString=%s&phoneString=%s&passString=%s", URLEncoder.encode(emailString, charset)
                        , URLEncoder.encode(phoneString, charset),URLEncoder.encode(passString, charset));

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

            }catch(MalformedURLException e){

                e.printStackTrace();
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }

        //exceptioned, hence return null
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
           return;
        }

        @Override
        protected void onPostExecute(String result) {

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
}
