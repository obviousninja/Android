package us.in_tune.in_tunex3;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Randy on 9/14/2015.
 */
public class MyProfile extends Activity {

Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile); //correct code, temporarily offline
        System.out.println("Spy is here: " + getIntent().getStringExtra("usernamekey1"));

        //get the text from secretquestion, secretanswer, and firstname and last name EditText fields

        EditText set_prof_secretQuestion, set_prof_secretAnswer, set_prof_firstname, set_prof_lastname;
        set_prof_secretQuestion = (EditText) findViewById(R.id.ProfileSecretQuestion);
        set_prof_secretAnswer = (EditText) findViewById(R.id.ProfileSecretAnswer);
        set_prof_firstname = (EditText) findViewById(R.id.profilefirstname);
        set_prof_lastname = (EditText) findViewById(R.id.profilelastname);

        final String prof_string_secretQuestion, prof_string_secretAnswer, prof_string_firstname, prof_string_lastname;
        prof_string_secretQuestion = set_prof_secretQuestion.getText().toString();
        prof_string_secretAnswer = set_prof_secretAnswer.getText().toString();
        prof_string_firstname = set_prof_firstname.getText().toString();
        prof_string_lastname = set_prof_lastname.getText().toString();

        View prof_cancel = findViewById(R.id.profilecancel);
        prof_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0);
                finish();
            }
        });

        View prof_save = findViewById(R.id.profilesave);
        prof_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //execute the order 66
                new SaveProfileTask().execute(prof_string_secretQuestion, prof_string_secretAnswer, prof_string_firstname, prof_string_lastname);

            }
        });

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

    private class SaveProfileTask extends AsyncTask<String, Integer, String> {

        private String task_secretQuestion, task_secretAnswer, task_firstname, task_lastname;
        private final String url = "http://in-tune.us/setProfile.php";

        //returns null if result is invalid, otherwise, return null
        @Override
        protected String doInBackground(String... params) {
            String charset = java.nio.charset.StandardCharsets.UTF_8.name();


            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


        }

        @Override
        protected void onPostExecute(String result){
            //do something
        }
    }
}
