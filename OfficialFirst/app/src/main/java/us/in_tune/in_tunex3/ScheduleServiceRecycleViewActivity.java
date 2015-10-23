package us.in_tune.in_tunex3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.*;


/**
 * Created by Randy on 10/8/2015.
 */
public class ScheduleServiceRecycleViewActivity extends AppCompatActivity {
    Context mContext;
    //item name
    ArrayList<NameAndPrice> itemCart = new ArrayList<NameAndPrice>();

    private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();

        SharedPreferences newPref = mContext.getSharedPreferences(getString(R.string.share_pref_name), Context.MODE_PRIVATE);
        String defaultVal = null;
        String username = newPref.getString(getString(R.string.username), defaultVal);
        String password = newPref.getString(getString(R.string.password), defaultVal);
        String user_type = "users";
     //   System.out.println("username: " + username + "password: " + password + "usertype: " + user_type);



        if(username != null && password != null){


            System.out.println("got into here");
            setContentView(R.layout.schedule_recycle_view);
            System.out.println("got into here1");
            View ovalFloatButton = findViewById(R.id.oval_float_icon);
            ovalFloatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(itemCart.isEmpty() == true){
                        Toast.makeText(mContext, "Item Cart Empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    System.out.println("ItemCart Items: " + itemCart.toString());
                    Intent summaryIntent = new Intent(mContext, ScheduleServiceRecycleViewSummary.class);

                    //make the item into a class object
                    ItemCartPackage passOver = new ItemCartPackage(itemCart);
                    Gson gS = new Gson();

                    //convert item to JSON
                    String passString = gS.toJson(passOver, ItemCartPackage.class);

                    System.out.println("pass string: "+passString);
                    summaryIntent.putExtra(getString(R.string.itemcart), passString);

                    startActivity(summaryIntent);
                }

            });

           //setting menu item menu.findItem(R.id.action_bar_total_price).setTitle("blahblahblah");

            //retrieve the items from the server
            new HttpRetrieveServerItems(username, password, user_type).execute();



        }else{
            //error, fails
            setContentView(R.layout.user_or_pass_notfound);
        }




    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        itemCart.clear();
    }

    //action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.total_price_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //modifying action bar title

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bar_total_price) {

            Toast.makeText(mContext, "Current Total: "+item.getTitle()+"$", Toast.LENGTH_SHORT ).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //private adapter class
    private class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder>{

        //JSONObject[] holdingObject;
        ArrayList<Map<String, String>> newArray = new ArrayList<Map<String, String>>();

        public CardViewAdapter(JSONObject[] curObject){


            //Map<String, String> newMap = new HashMap<String, String>();
            //this.holdingObject = curObject;
            try{
                for(Integer i=0; i<curObject.length; i++){
                    JSONObject object = curObject[i];
                    Map<String, String> newMap = new HashMap<String, String>();
                    newMap.put("serviceName", object.getString("serviceName"));
                    newMap.put("serviceDescription", object.getString("serviceDescription"));
                    newMap.put("servicePrice", object.getString("servicePrice"));
                    newMap.put("serviceOption", object.getString("serviceOption"));
                    newArray.add(newMap);
                }
            }catch(Exception e){
                //jsonexception
                e.printStackTrace();
            }


        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public View View;
            public ViewHolder(View v) {
                super(v);
                View = v;
            }
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            //View curView = LayoutInflater.from(parent.getContext())
             //       .inflate(R.layout.schedule_card_item, parent, false);

            View curView =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.schedule_summary_card_item, parent, false);

            ViewHolder vh = new ViewHolder(curView);


            return vh;
        }

        public String firstHalf(String wholeServiceOptionString){
            String[] serviceOptionString = wholeServiceOptionString.split("#");

            return serviceOptionString[0];
        }

        public String secondHalf(String wholeServiceOptionString){
            String[] serviceOptionString = wholeServiceOptionString.split("#");

            return serviceOptionString[1];
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            final int currentpos = position;
            TextView serve_n = (TextView) holder.View.findViewById(R.id.summary_service_name);
            TextView descript_text = (TextView) holder.View.findViewById(R.id.summary_service_description);
            TextView price_text = (TextView) holder.View.findViewById(R.id.summary_price);

            try{
                String serviceOptionQuantityText = newArray.get(position).get("serviceOption");
                JSONArray curServiceJSONArray = new JSONArray(serviceOptionQuantityText);
                //serviceOptionName#serviceOptionQuantity

                ArrayList<String> serviceOptionText = new ArrayList<String>();
                ArrayList<String> serviceOptionPriceText = new ArrayList<String>();
                JSONArray curObject = curServiceJSONArray.getJSONArray(0);
                for(int i=0; i<curObject.length(); i++){

                    System.out.println(firstHalf(curObject.getString(i)));
                    serviceOptionText.add(firstHalf(curObject.getString(i)) + "(" + secondHalf(curObject.getString(i)) + "$ )");

                    System.out.println(secondHalf(curObject.getString(i)));
                    serviceOptionPriceText.add(secondHalf(curObject.getString(i)));

                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.summary_spinner_item, serviceOptionText);
                final Spinner servicename_spinner = (Spinner) holder.View.findViewById(R.id.summary_service_spinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                servicename_spinner.setAdapter(adapter);
                servicename_spinner.setSelection(0);
                servicename_spinner.setSelected(true);
                //TODO construct an adapter

                //TODO set adapter


                final Spinner quantity_spinner = (Spinner) holder.View.findViewById(R.id.summary_quantity_spinner);
                ArrayList<Integer> quanityList = new ArrayList<Integer>();
                quanityList.add(1);
                quanityList.add(2);
                quanityList.add(3);
                quanityList.add(4);
                quanityList.add(5);
                quanityList.add(6);
                quanityList.add(7);
                quanityList.add(8);
                quanityList.add(9);
                quanityList.add(0);

                ArrayAdapter<Integer> adapterPrice = new ArrayAdapter<Integer>(mContext,R.layout.summary_spinner_item, quanityList );
                adapterPrice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                quantity_spinner.setAdapter(adapterPrice);
                quantity_spinner.setSelection(0);
                quantity_spinner.setSelected(true);
                //TODO construct an adapter
                //TODO set adapter


                //to set menu total number
                //menu.getItem(0).setTitle("great")

                final ToggleButton tButton = (ToggleButton) holder.View.findViewById(R.id.summary_toggler);
                //needed items
                final ArrayList<String> dupAdditionalPrice = serviceOptionPriceText;
                final ArrayList<String> dupServiceOptionText = serviceOptionText;
                final ViewHolder myHolder = holder;
                //
                tButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (tButton.isChecked()) {
                            //checked
                            //itemCart.add(new NameAndPrice(newArray.get(curPosition).get("serviceName"), newArray.get(curPosition).get("servicePrice")));
                            itemCart.add(new NameAndPrice(newArray.get(currentpos).get("serviceName"), newArray.get(currentpos).get("servicePrice"),
                                    dupAdditionalPrice.get(servicename_spinner.getSelectedItemPosition()),
                                    quantity_spinner.getSelectedItem().toString(),
                                    dupServiceOptionText.get(servicename_spinner.getSelectedItemPosition())
                                            )

                            );

                            //get item's total and add it to the total
                            TextView elementPrice = (TextView) myHolder.View.findViewById(R.id.summary_price);
                            Integer toAdd = Integer.parseInt(elementPrice.getText().toString());

                            //current menu price
                            Integer curMenuTotal = Integer.parseInt(menu.getItem(0).getTitle().toString());

                            Integer newTotal = curMenuTotal + toAdd;

                            menu.getItem(0).setTitle(newTotal.toString());


                            //disable the spinners
                            servicename_spinner.setEnabled(false);
                            servicename_spinner.setClickable(false);
                            quantity_spinner.setEnabled(false);
                            quantity_spinner.setClickable(false);

                        } else {
                            //NameAndPrice namePriceObject = new NameAndPrice(newArray.get(curPosition).get("serviceName"), newArray.get(curPosition).get("servicePrice"));
                            //not checked

                            itemCart.remove(new NameAndPrice(newArray.get(currentpos).get("serviceName"), newArray.get(currentpos).get("servicePrice"),
                                            dupAdditionalPrice.get(servicename_spinner.getSelectedItemPosition()),
                                            quantity_spinner.getSelectedItem().toString(),
                                            dupServiceOptionText.get(servicename_spinner.getSelectedItemPosition())
                                    )
                            );

                            //get item's total and substract it from the total
                            //get item's total and add it to the total
                            TextView elementPrice = (TextView) myHolder.View.findViewById(R.id.summary_price);
                            Integer toRemove = Integer.parseInt(elementPrice.getText().toString());

                            //current menu price
                            Integer curMenuTotal = Integer.parseInt(menu.getItem(0).getTitle().toString());

                            Integer newTotal = curMenuTotal-toRemove;
                            menu.getItem(0).setTitle(newTotal.toString());

                            //enable the spinners
                            servicename_spinner.setEnabled(true);
                            servicename_spinner.setClickable(true);
                            quantity_spinner.setEnabled(true);
                            quantity_spinner.setClickable(true);

                        }
                    }

                });




                servicename_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {

                        // Display a Toast message indicating the currently selected
                        // item
                        //Toast.makeText(parent.getContext(), parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
                        //get total and add the total sum to the total
                        //quantity
                        Integer quantitySelected = Integer.parseInt(quantity_spinner.getSelectedItem().toString());
                        //base price
                        Integer basePrice = Integer.parseInt(newArray.get(currentpos).get("servicePrice"));
                        //additional price
                        Integer additionalPrice = Integer.parseInt(dupAdditionalPrice.get(pos));
                        //total is as follow: quantity*(base price + additional price)

                        Integer total = quantitySelected*(basePrice + additionalPrice);

                        TextView elementPrice = (TextView) myHolder.View.findViewById(R.id.summary_price);
                        elementPrice.setText(total.toString());




                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                quantity_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {



                        //get total and add the total sum to the total
                        //quantity
                        Integer quantitySelected = Integer.parseInt(parent.getItemAtPosition(pos).toString());
                        //base price
                        Integer basePrice = Integer.parseInt(newArray.get(currentpos).get("servicePrice"));
                        //additional price
                        Integer additionalPrice = Integer.parseInt(dupAdditionalPrice.get(servicename_spinner.getSelectedItemPosition()));
                        //total is as follow: quantity*(base price + additional price)

                        Integer total = quantitySelected*(basePrice + additionalPrice);

                        TextView elementPrice = (TextView) myHolder.View.findViewById(R.id.summary_price);
                        elementPrice.setText(total.toString());

                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                serve_n.setText(newArray.get(position).get("serviceName"));
                descript_text.setText(newArray.get(position).get("serviceDescription"));
                price_text.setText(newArray.get(position).get("servicePrice"));
            }catch(Exception e){

            }



        }




        @Override
        public int getItemCount() {

           return newArray.size();
        }
    }

    private class HttpRetrieveServerItems extends AsyncTask<String, Integer, JSONObject[]>{

        String usernameItem, passwordItem, user_type;
        String url ="http://in-tune.us/retrieveAllServiceItems.php";


        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter;

        public HttpRetrieveServerItems(String usernameItem, String passwordItem, String user_type) {

            this.usernameItem = usernameItem;
            this.passwordItem = passwordItem;
            this.user_type = user_type;
        }

        @Override
        protected JSONObject[] doInBackground(String... params) {

            String charset = Charset.forName("UTF-8").name();
            //double check
            if(usernameItem == null || usernameItem.compareTo("")==0 || passwordItem==null || passwordItem.compareTo("")==0|| user_type == null || user_type.compareTo("")==0){
                return null;
            }

            try{

                URL request = new URL(url);
                String query = String.format("loginInput=%s&passInput=%s&type=%s", URLEncoder.encode(usernameItem, charset), URLEncoder.encode(passwordItem, charset), URLEncoder.encode(user_type, charset));


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
                JSONObject newObject = new JSONObject(newString);
                JSONObject[] resultArray = new JSONObject[newObject.length()];
                for(Integer i=0; i<newObject.length(); i++){
                    resultArray[i] = newObject.getJSONObject(i.toString());
                }

                input.close(); //close any persistent resources so asyncthread would close down
                newReader.close();
                output.close();
                conn.disconnect();
                return resultArray;

            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject[] result) {
            super.onPostExecute(result);
            System.out.println("got into here3");
            //populate the recycleview
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);

            System.out.println("got into here4");
            //setting adapter
            mAdapter = new CardViewAdapter(result);
            System.out.println("got into here5");
            mRecyclerView.setAdapter(mAdapter);
            System.out.println("got into here6");


        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(JSONObject[] result) {
            super.onCancelled(result);


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}