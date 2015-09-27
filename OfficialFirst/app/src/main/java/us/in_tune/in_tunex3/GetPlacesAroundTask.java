package us.in_tune.in_tunex3;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Randy on 9/23/2015.
 */
public class GetPlacesAroundTask extends AsyncTask<Location, LocationObjectAndRank, String> {

    private Context curContext;
    private String url;
    GoogleMap mMap;
    //make things easier to toast by passing in current activity's context
    public GetPlacesAroundTask(Context context, String url, GoogleMap map){

        curContext = context;
        this.url = url;
        mMap = map;

    }

    public Context getContext(){
        return curContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Location... params) {

        String charset = java.nio.charset.StandardCharsets.UTF_8.name();

        try{
            String requestString = url;
            URL request = new URL(requestString); //malformedURLException thrown here
            URLConnection conn = request.openConnection();  //IOException thrown here
            conn.setRequestProperty("Accept-Charset", charset);
            InputStream response = conn.getInputStream();

            //processing inputstream called response
            InputStreamReader newReader = new InputStreamReader(response, charset);
            int newchar;
            String newString = new String();
            while((newchar =  newReader.read() ) != -1 ){

                newString = newString + (char)newchar;

            }
            response.close(); //finished with input stream
            //process the json object
            JSONObject object = new JSONObject(newString); //JSONexception thrown here

            //status code processing
            String statusCode = (String) object.get("status");

            if(statusCode.compareTo("OK") == 0){
                System.out.println("Status is: " + statusCode);
                //do the processing because we got some results
                //getting results
                //JSONObject resultObject = object.getJSONObject("results"); //exception could be thrown here if result isn't a json object
                JSONArray resultArray = object.getJSONArray("results"); //exception could be thrown here is result isn't a json object

                System.out.println("Length: " +resultArray.length());

                //System.out.println(resultArray.getJSONObject(0).getString("name"));
                JSONObject curObject;
                for(int i =0; i<resultArray.length(); i++){
                    curObject = resultArray.getJSONObject(i);



                    //construct LocationObject and rank
                    LocationObjectAndRank newObject = new LocationObjectAndRank(curObject, i);

                    //publish custom locationObject and rank
                    publishProgress(newObject); //publishing object



                }


            }else{
                if(statusCode.compareTo("ZERO_RESULTS") == 0){
                    System.out.println("no result returned");
                }else if(statusCode.compareTo("OVER_QUERY_LIMIT") == 0){
                    System.out.println("Over Query Limit");
                }else if(statusCode.compareTo("REQUEST_DENIED") == 0){
                    System.out.println("Request Denied");
                }else if(statusCode.compareTo("INVALID_REQUEST") == 0){
                    System.out.println("INVALID_REQUEST");
                }else{
                    //unknown error
                }

            }

            newReader.close();


        }catch (Exception e){

        }

        return null;
    }

    @Override
    protected void onProgressUpdate(LocationObjectAndRank... values) {
        super.onProgressUpdate(values);
        JSONObject curObject = values[0].getCurLocation();
        Integer distanceRank = values[0].getRank();
        try{
            if(curObject.has("geometry")){
                JSONObject geoObject = curObject.getJSONObject("geometry").getJSONObject("location");
                if(geoObject.has("lat") && geoObject.has("lng")){
                    System.out.println("Latitude: " + geoObject.getDouble("lat")+ " Longitude: " + geoObject.getDouble("lng"));

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(geoObject.getDouble("lat"),geoObject.getDouble("lng")))
                            .title("Distance Rank" + distanceRank));

                }

            }
            if(curObject.has("name")){

                System.out.println("Name: " + curObject.getString("name"));
            }
            if(curObject.has("vicinity")){

                System.out.println(	"Vicinity: " + curObject.getString("vicinity"));
            }
            if(curObject.has("rating")){

                System.out.println("Rating: " + curObject.getDouble("rating"));
            }
        }catch(Exception e){

        }


    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }
}
