package us.in_tune.in_tunex3;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Randy on 9/14/2015.
 */
public class MyProfile extends Activity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile); //correct code, temporarily offline
        mContext = getApplicationContext();

        //System.out.println("Spy is here: " + getIntent().getStringExtra("usernamekey1"));





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
                //get the text from secretquestion, secretanswer, and firstname and last name EditText fields

                EditText set_prof_secretQuestion, set_prof_secretAnswer, set_prof_firstname, set_prof_lastname;
                set_prof_secretQuestion = (EditText) findViewById(R.id.ProfileSecretQuestion);
                set_prof_secretAnswer = (EditText) findViewById(R.id.ProfileSecretAnswer);
                set_prof_firstname = (EditText) findViewById(R.id.profilefirstname);
                set_prof_lastname = (EditText) findViewById(R.id.profilelastname);

                String prof_string_secretQuestion, prof_string_secretAnswer, prof_string_firstname, prof_string_lastname;
                prof_string_secretQuestion = set_prof_secretQuestion.getText().toString();
                prof_string_secretAnswer = set_prof_secretAnswer.getText().toString();
                prof_string_firstname = set_prof_firstname.getText().toString();
                prof_string_lastname = set_prof_lastname.getText().toString();


                System.out.println("got here save prof");
                //execute the order 66

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                    new SaveProfileTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getIntent().getStringExtra("usernamekey1"), prof_string_secretQuestion, prof_string_secretAnswer, prof_string_firstname, prof_string_lastname);
                }else{
                    new SaveProfileTask().execute(getIntent().getStringExtra("usernamekey1"), prof_string_secretQuestion, prof_string_secretAnswer, prof_string_firstname, prof_string_lastname);
                }

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
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private class SaveProfileTask extends AsyncTask<String, Integer, String> {

        private String task_email, task_secretQuestion, task_secretAnswer, task_firstname, task_lastname;
        private final String url = "http://in-tune.us/setProfile.php";


        //returns null if result is invalid, otherwise, return null
        @Override
        protected String doInBackground(String... params) {
            String charset = java.nio.charset.StandardCharsets.UTF_8.name();


            try{


              task_email=  Html.escapeHtml(params[0].toString()).toString();
                task_secretQuestion = Html.escapeHtml(params[1].toString()).toString();
                task_secretAnswer = Html.escapeHtml(params[2].toString()).toString();
                task_firstname = Html.escapeHtml(params[3].toString()).toString();
                task_lastname = Html.escapeHtml(params[4].toString()).toString();

                System.out.println("task email: " + task_email);
                System.out.println("secret question" + task_secretQuestion);
                System.out.println("secret answer" + task_secretAnswer);
                System.out.println("first name" + task_firstname);
                System.out.println("last name" + task_lastname);

                if(task_email == null || task_email.compareTo("")==0 ){
                    return null;
                }

                if(task_secretQuestion == null || task_secretQuestion.compareTo("")==0 ){
                    return null;
                }

                if(task_secretAnswer == null || task_secretAnswer.compareTo("")==0 ){
                    return null;
                }

                if(task_firstname == null || task_firstname.compareTo("")==0 ){
                    return null;
                }
                if(task_lastname == null || task_lastname.compareTo("")==0 ){
                    return null;
                }



                URL request = new URL(url);
                String query = String.format("loginName=%s&profilesecretquestion=%s&profilesecretanswer=%s&profilefirstname=%s&profilelastname=%s",
                        URLEncoder.encode(task_email, charset), URLEncoder.encode(task_secretQuestion, charset), URLEncoder.encode(task_secretAnswer, charset),
                        URLEncoder.encode(task_firstname, charset), URLEncoder.encode(task_lastname, charset));


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

                //free up resources
                newReader.close();
                input.close();
                output.close();
                conn.disconnect();

                return newString;



            }catch(Exception e){
                System.out.println("Exceptioned e");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


        }

        @Override
        protected void onPostExecute(String result){
            //do something
            if(result == null) {
                Toast.makeText(mContext, "Not Saved, Please Fill in All Blanks", Toast.LENGTH_LONG).show();
                return;
            }

            System.out.println("result code: " + result);

            int retInt = new Integer(result);
            if(retInt == 2){
                //success
                Toast.makeText(mContext, "Saved!", Toast.LENGTH_LONG).show();
                EditText set_prof_secretQuestion, set_prof_secretAnswer, set_prof_firstname, set_prof_lastname;
                set_prof_secretQuestion = (EditText) findViewById(R.id.ProfileSecretQuestion);
                set_prof_secretAnswer = (EditText) findViewById(R.id.ProfileSecretAnswer);
                set_prof_firstname = (EditText) findViewById(R.id.profilefirstname);
                set_prof_lastname = (EditText) findViewById(R.id.profilelastname);

                set_prof_firstname.setText("");
                set_prof_lastname.setText("");
                set_prof_secretAnswer.setText("");
                set_prof_secretQuestion.setText("");

            }else{

                Toast.makeText(mContext, "Not Saved, Please Fill in All Blanks", Toast.LENGTH_LONG).show();
            }
        }
    }
}
