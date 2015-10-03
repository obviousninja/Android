package intunex3.intunestoreinterface;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
                        AsyncTask newTask = new StoreSignUpTask().execute(usernameString, passwordString, addrString);
                    }
                }

        );
    }

    private class StoreSignUpTask extends AsyncTask<String, Integer, Integer>{

        @Override
        protected Integer doInBackground(String... params) {

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
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Integer integer) {
            super.onCancelled(integer);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
