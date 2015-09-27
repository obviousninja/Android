package us.in_tune.in_tunex3;

/**
 * Created by Randy on 9/25/2015.
 */

/*
* Library for functions that parses HTTP Addresses
* */
public class GeoLibrary {

    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=38.909567,-77.028816&rankby=distance&types=car_repair&sensor=true&key=AIzaSyCURSD_AriIM3vGRqkYok3J9EJ0oszjG0U
    // Ask Query By having the google return the rank base on distance CANNOT SPECIFY DISTANCE
    public String latLngRankByDistance(Double latitude, Double longitude){
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
        String tail = "&rankby=distance&types=car_repair&sensor=true&key=AIzaSyCURSD_AriIM3vGRqkYok3J9EJ0oszjG0U";
        String retVal = new String();

        retVal = retVal.concat(url).concat(latitude.toString()).concat(",").concat(longitude.toString()).concat(tail);

        return retVal;
    }

    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=38.909567,-77.028816&radius=5000&types=car_repair&sensor=true&key=AIzaSyCURSD_AriIM3vGRqkYok3J9EJ0oszjG0U
    // Ask Query by Distance in Meters
    public String latLngDistance(Double latitude, Double longitude, Double radius){
        String retVal = new String();
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
        String tail = "&types=car_repair&sensor=true&key=AIzaSyCURSD_AriIM3vGRqkYok3J9EJ0oszjG0U";

        retVal = retVal.concat(url).concat(latitude.toString()).concat(",").concat(longitude.toString()).concat("&radius=").concat(radius.toString()).concat(tail);

        return retVal;
    }


    //https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=API_KEY
    public String geocodeAddress(String address){
        String retVal = new String();
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        String tail = "&key=AIzaSyCURSD_AriIM3vGRqkYok3J9EJ0oszjG0U";

        String procAddr = address.replace(' ', '+');

        retVal = retVal.concat(url).concat(procAddr).concat(tail);
        //System.out.println(retVal);

        return retVal;
    }
}
