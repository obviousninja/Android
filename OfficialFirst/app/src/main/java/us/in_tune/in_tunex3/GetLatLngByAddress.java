package us.in_tune.in_tunex3;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Randy on 9/26/2015.
 */

//class that retrieves the geocoding of current address provided
public class GetLatLngByAddress extends AsyncTask<String, Integer, JSONObject>{

    private Context curContext;
    private String url;
    GoogleMap mMap;
    Double radius;

    public GetLatLngByAddress(Context context, String url, GoogleMap map, Double radius){

        curContext = context;
        this.url = url;
        mMap = map;
        this.radius = radius;
    }

    @Override
    protected void onPreExecute(){

    }

    @Override
    protected JSONObject doInBackground(String... params) {
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


                    if(curObject.has("geometry")){
                        JSONObject geoObject = curObject.getJSONObject("geometry").getJSONObject("location");
                        if(geoObject.has("lat") && geoObject.has("lng")){
                            System.out.println("Latitude: " + geoObject.getDouble("lat")+ " Longitude: " + geoObject.getDouble("lng"));
                            //return JSONObject that has lat and lng attributes
                            return geoObject;
                        }

                    }


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


        }catch(Exception e){

        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        super.onPostExecute(s);
        //process google map and place the initial marker
        //System.out.println("Latitude: " + geoObject.getDouble("lat")+ " Longitude: " + geoObject.getDouble("lng"));
        //add the marker to the position
        if(s == null){
            Toast.makeText(curContext, "Please Enter a valid Address", Toast.LENGTH_LONG).show();
            return;
        }
        mMap.clear();
        try{
            mMap.addMarker(new MarkerOptions()
                    // .position(new LatLng(result.getLatitude(), result.getLongitude()))
                    .position(new LatLng(s.getDouble("lat"), s.getDouble("lng")))
                    .title("You"));

            //Adjust camera position
            CameraPosition pos = new CameraPosition(new LatLng(s.getDouble("lat"), s.getDouble("lng")), 14, 45, 0 );
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));

            //get the surroundings
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){

                new GetPlacesAroundTask(curContext, (new GeoLibrary()).latLngDistance(s.getDouble("lat"), s.getDouble("lng"), this.radius), mMap).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }else{

                new GetPlacesAroundTask(curContext, (new GeoLibrary()).latLngDistance(s.getDouble("lat"), s.getDouble("lng"), this.radius), mMap).execute();
            }
        }catch(Exception e){
            //json exception, which is pretty much impossible at this point so... if it does happen
            System.out.println("JsonException on POSTEXECUTE IN GETLALNGBYADDRESS");
        }




    }
}
