package us.in_tune.in_tunex3;

/**
 * Created by Randy on 8/25/2015.
 */
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

public class ForgotPassword extends AppCompatActivity {

    Context mContext;
    boolean firstStage=false; //initially this will be false, which indicates that resetQuestion have not yet been posed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpasswordsheet);
        mContext = getApplicationContext();


        View resetSubmit = findViewById(R.id.reset_submit);

        resetSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something

                EditText resetEmail = (EditText) findViewById(R.id.reset_emailinput);
                String reset_emailString = resetEmail.getText().toString();

                EditText resetAnswer = (EditText) findViewById(R.id.reset_answer);
                String reset_answerString = resetAnswer.getText().toString();

                System.out.println("making sure it worksRESTANDRELAX: " + reset_emailString);

                if(reset_emailString.compareTo("")==0 || reset_emailString==null){
                    Toast.makeText(mContext, "Email Cannot Be Empty", Toast.LENGTH_LONG);
                }else{
                    //here email is not empty


                    //we send it to server for further processing
                    //if email matches an loginname, then we retrieve the secret question and use it, if not toast.email invalid

                    //TODO receive the json object that contains both secretquestion and secretanswer

                }


                //check if the answer from the editText field matches the correct answer
                if(reset_answerString.compareTo("") ==0 || reset_answerString == null){
                    //the first stage, where
                }else{

                }


            }
        });

    }

    private class ResetTask extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... params) {
            return null;

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
