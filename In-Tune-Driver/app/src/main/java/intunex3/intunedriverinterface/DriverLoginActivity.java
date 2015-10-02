package intunex3.intunedriverinterface;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DriverLoginActivity extends AppCompatActivity {

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_login_activity);
        mContext = getApplicationContext();



        View signinButton = findViewById(R.id.signin);

        signinButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                        System.out.println("usernameString: " + usernameString +"passwordString: "+ passwordString);
                        //execute the signin action with params from the edittext box
                        new DriverSignin().execute(usernameString, passwordString);


                    }
                }

        );

    }

private class DriverSignin extends AsyncTask<String, Integer, Integer> {

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
