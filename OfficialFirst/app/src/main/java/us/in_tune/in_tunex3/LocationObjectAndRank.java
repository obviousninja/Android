package us.in_tune.in_tunex3;

import android.location.Location;

import org.json.JSONObject;

/**
 * Created by Randy on 9/26/2015.
 */
public class LocationObjectAndRank {

    JSONObject curLocation;
    int rank;

    LocationObjectAndRank(JSONObject location, int integerToBeTaken){
        curLocation = location;
        rank = integerToBeTaken;
    }

    public JSONObject getCurLocation(){
        return curLocation;
    }

    public int getRank(){
        return rank;
    }
}
