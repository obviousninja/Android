package intunex3.intunestoreinterface;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
                        //TODO Peformance sign in actions
                        EditText username = (EditText) findViewById(R.id.usernameinput);
                        EditText password = (EditText) findViewById(R.id.passwordinput);

                        String usernameString = username.getText().toString();
                        String passwordString = password.getText().toString();

                        if(usernameString == null || usernameString.compareTo("") == 0 || passwordString== null || passwordString.compareTo("")==0) {
                            Toast.makeText(mContext, "Username or Password cannot be empty", Toast.LENGTH_LONG).show();
                            return;
                        }


                        //execute task
                        new StoreSigninTask().execute(usernameString, passwordString);
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

private class StoreSigninTask extends AsyncTask<String, Integer, Integer>{

    @Override
    protected Integer doInBackground(String... params) {
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
