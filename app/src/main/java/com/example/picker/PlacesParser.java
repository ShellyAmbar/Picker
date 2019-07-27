package com.example.picker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlacesParser  {

    private HashMap<String,String> GetPlace(JSONObject googlePlaceJson){
        HashMap<String,String> googlePlaceHashMap = new HashMap<>();

        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        try {
             if(!googlePlaceJson.isNull("name")){

                    placeName = googlePlaceJson.getString("name");
             }
            if(!googlePlaceJson.isNull("vicinity")){

                vicinity = googlePlaceJson.getString("vicinity");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");

            googlePlaceHashMap.put("placeName",placeName);
            googlePlaceHashMap.put("vicinity",vicinity);
            googlePlaceHashMap.put("latitude",latitude);
            googlePlaceHashMap.put("longitude",longitude);
            googlePlaceHashMap.put("reference",reference);





        }catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceHashMap;
    }

    private List<HashMap<String,String>> GetAllPlaces (JSONArray jsonArray){

        int count = jsonArray.length();
        List<HashMap<String,String>> hashMapList = new ArrayList<>();
        HashMap<String,String> placeHashMap = null;

        for(int i=0;i<count; i++){
            try {
                placeHashMap = GetPlace((JSONObject) jsonArray.get(i));
                hashMapList.add(placeHashMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return hashMapList;
    }

    public List<HashMap<String,String>> Parse(String jsonData){

        JSONArray jsonArray = null;
        JSONObject jsonObject ;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return GetAllPlaces(jsonArray);
    }
}
