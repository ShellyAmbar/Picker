package com.example.picker;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class PlacesData extends AsyncTask<Object,String,String> {
    private String googlePlacesData;
    private GoogleMap googleMap;
    private String Url;
    @Override
    protected String doInBackground(Object... objects) {
        googleMap = (GoogleMap) objects[0];
        Url =(String) objects[1];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.ReadUrl(Url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> nearbyPlacesList = null;
        PlacesParser placesParser =new PlacesParser();
        nearbyPlacesList = placesParser.Parse(s);

        ShowNearbyPlaces(nearbyPlacesList);

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    private void ShowNearbyPlaces(List<HashMap<String,String>> nearbyPlacesList){

        for(HashMap<String,String> hashMap :  nearbyPlacesList ){

            MarkerOptions markerOptions = new MarkerOptions();
            String placeName = hashMap.get("placeName");
            String vicinity = hashMap.get("vicinity");
            double latitude = Double.parseDouble(hashMap.get("latitude"));
            double longitude =Double.parseDouble(hashMap.get("longitude"));
            String reference = hashMap.get("reference");

            LatLng latLng = new LatLng(latitude,longitude);
            markerOptions.position(latLng);
            markerOptions.title(placeName +" :" + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        }
    }
}
