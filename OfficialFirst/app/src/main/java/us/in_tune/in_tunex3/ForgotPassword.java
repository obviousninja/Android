package us.in_tune.in_tunex3;

/**
 * Created by Randy on 8/25/2015.
 */
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.lang.Integer;

public class ForgotPassword extends AppCompatActivity {

    Context mContext;
    String reset_answer;

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
                String reset_answerString = resetAnswer.getText().toString(); //first submit will have reset_answerString as null

                System.out.println("making sure it worksRESTANDRELAX: " + reset_emailString);

                if(reset_emailString.compareTo("")==0 || reset_emailString==null){
                    Toast.makeText(mContext, "Email Cannot Be Empty", Toast.LENGTH_LONG).show();
                }else{
                    //here email is not empty
                    //we send it to server for further processing
                    //if email matches an loginname, then we retrieve the secret question and use it, if not toast.email invalid


                    //TODO receive the json object that contains both secretquestion and secretanswer
                    new ResetTask().execute(reset_emailString, reset_answerString); //at first round, reset_answerstring will be null
                }



            }
        });

    }

    private class ResetTask extends AsyncTask<String, Integer, String>{
        String loginName;
        String url = "http://in-tune.us/email_password_reset.php";
        @Override
        protected String doInBackground(String... params) {

            try{
                String charset = java.nio.charset.StandardCharsets.UTF_8.name();

                loginName = Html.escapeHtml(params[0]);
                reset_answer = Html.escapeHtml(params[1]);
                //loginName cannot possibly be null or empty at this point, hence check skipped

                    //check validity of loginName on the server

                    URL request = new URL(url);
                    String query = String.format("loginName=%s&secretanswer=%s", URLEncoder.encode(loginName, charset), URLEncoder.encode(reset_answer, charset));

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

            }catch (Exception e){
                System.out.println("exceptioned");
            }






            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


        }
        @Override
        protected void onPostExecute(String result){
            //contruct the json object here
            //json decode

            try{
                int resultInt = Integer.parseInt(result);
                if(resultInt == 2){
                    //email sent
                    Toast.makeText(mContext, "Password Reset Sent", Toast.LENGTH_LONG).show();
                    View hiddenView = findViewById(R.id.reset_hiddenlayout);
                    hiddenView.setVisibility(View.GONE);

                    EditText questionView = (EditText) findViewById(R.id.reset_answer);
                    questionView.setHint("Hint Cleared");
                    questionView.setText("");

                    EditText emailView = (EditText) findViewById(R.id.reset_emailinput);
                    emailView.setEnabled(true);
                    emailView.setFocusable(true);
                    emailView.setText("");

                }else if(resultInt == 3){
                    //email doesn't exist
                    Toast.makeText(mContext, "User/Answer Incorrect", Toast.LENGTH_LONG).show();
                }else if(resultInt == 4){
                    //error
                    Toast.makeText(mContext, "Please Input Email/Answer", Toast.LENGTH_LONG).show();
                }else{
                    //can't be any other numbers

                }

            }catch(NumberFormatException e){
                //its not a number hence we parse it via json

                try{
                    JSONObject object = new JSONObject(result);
                    String[] resultArray = new String[object.length()];
                    System.out.println("Length: " + object.length());
                    Iterator<String> newIter = object.keys();
                    int counter =0;
                    while(newIter.hasNext()){
                        //first element will always be question and the second element will be answer
                        resultArray[counter] = newIter.next();
                        counter++;
                    }

                    View hiddenView = findViewById(R.id.reset_hiddenlayout);
                    hiddenView.setVisibility(View.VISIBLE);

                    EditText questionView = (EditText) findViewById(R.id.reset_answer);
                    questionView.setHint(resultArray[0]);

                    EditText emailView = (EditText) findViewById(R.id.reset_emailinput);
                    emailView.setEnabled(false);
                    emailView.setFocusable(false);

                    reset_answer = resultArray[1];

                }catch(Exception e1){
                    //not a number and not a json, critical error, can't be possible
                }

            }








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
