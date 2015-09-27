package us.in_tune.in_tunex3;

import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Randy on 9/27/2015.
 */
public class InTuneInfoWindow implements GoogleMap.InfoWindowAdapter {

    private final View info_window_content_view;

    InTuneInfoWindow(LayoutInflater inflatoor){
       info_window_content_view = inflatoor.inflate(R.layout.map_info_win_layout, null);

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return info_window_content_view;
    }
}
