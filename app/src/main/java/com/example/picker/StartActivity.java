package com.example.picker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.compat.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.ui.PlacePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,View.OnClickListener
{

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code=99;
    private String myLocationName;
    private static final int REQEST_CODE_SPEECH_INPUT =1000 ;
    private String ResultOfSpeach;
    private ImageButton search_btn,search_button_places,drop_down_places;
    private AutoCompleteTextView search_edit_text;
    private AutoCompleteTextView search_edit_places;

    private String searchResult;
    private String searchPlacesResult;
    private CircleImageView imageView;
    private Context context;
    private double longitude;
    private double latitude;
    private LatLng currentLocation;
    private ArrayList<LatLng> arrayListOfPoints;
    private ArrayList<LatLng> arrayListFromCurrentPoint;
    private LatLng myLocation;
    private android.speech.tts.TextToSpeech mTTs;
    private Route route;
    private LatLng firstPoint;
    private final  int PLACE_PICKER_REQUEST = 1;
    private final int PLACE_PERMISSION=2;
    private String UserName;
    private int PROXIMITY_RADIUS= 10000;
    private ArrayAdapter<String> adapterPlaces;
    private GoogleApiClient mGoogleApiClient1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        search_btn=findViewById(R.id.search_button);
        search_button_places= findViewById(R.id.search_button_places);
        search_edit_text=findViewById(R.id.search_edit_frame);
        search_edit_places= findViewById(R.id.search_edit_places);
        drop_down_places=findViewById(R.id.drop_down_places);
        imageView=findViewById(R.id.image);
        route=null;
        context=getApplicationContext();
        arrayListOfPoints = new ArrayList<>();
        arrayListFromCurrentPoint = new ArrayList<>();
        UserName="User";
        Userdialog();
        fab.setOnClickListener(this);

        adapterPlaces=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,getResources().getStringArray(R.array.places_list));

        search_edit_places.setAdapter(adapterPlaces);
        search_edit_places.setThreshold(0);
        search_edit_places.setDropDownHeight(400);




        mGoogleApiClient1 = new GoogleApiClient.Builder(this)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(this,this)
                .addOnConnectionFailedListener(this)
                .build();


        mGoogleApiClient1.connect();


        PlaceAutocompleteAdapter adapterCities=new PlaceAutocompleteAdapter(StartActivity.this, mGoogleApiClient1,LAT_LNG_BOUNDS,null);
        search_edit_text.setAdapter(adapterCities);
        search_edit_text.setThreshold(0);
        search_edit_text.setDropDownHeight(400);

        search_btn.setOnClickListener(this);

        search_button_places.setOnClickListener(this);
        drop_down_places.setOnClickListener(this);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.fab:
                Speaker();
                break;
            case R.id.search_button:
                searchResult=search_edit_text.getText().toString().toLowerCase();
                search_edit_text.setText("");
                search_edit_text.setHint(R.string.search_hint);

                mMap.clear();
                getLongLatFromCityName(searchResult);
                break;
            case R.id.search_button_places:
                searchPlacesResult=search_edit_places.getText().toString();
                search_edit_places.setText("");
                search_edit_places.setHint(R.string.search_places_hint);
                mMap.clear();
                ShowAllPlacesInMap(searchPlacesResult);
                break;
            case R.id.drop_down_places:
               search_edit_places.showDropDown();
                search_edit_places.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        search_edit_places.setText(search_edit_places.getText().toString());
                    }
                });
                break;

        }
    }

    private void ShowAllPlacesInMap(String searchPlacesResult) {
        String placeName = "";

        switch (searchPlacesResult){
            case "hospitals":
                placeName = "Hospitals";
                break;
            case "pharmacy":
                placeName = "pharmacy";
                break;
            case "police":
                placeName = "police";
                break;
            case "shopping mall":
                placeName = "shopping_mall";
                break;
            case "restaurants":
                placeName = "Restaurants";
                break;
            case "museum":
                placeName = "museum";
                break;
            case "library":
                placeName = "library";
                break;
            case "night club":
                placeName = "night_club";
                break;
            case "parking":
                placeName = "parking";
                break;
            case "post office":
                placeName = "post_office";
                break;
            case "school":
                placeName = "school";
                break;
            case "synagogue":
                placeName = "synagogue";
                break;
            case "taxi stand":
                placeName = "taxi_stand";
                break;
            case "train station":
                placeName = "train_station";
                break;
            case "zoo":
                placeName = "zoo";
                break;
            case "bars":
                placeName = "bars";
                break;
            case "laundry":
                placeName = "laundry";
                break;
            case "bank":
                placeName = "bank";
                break;
            case "airport":
                placeName = "airport";
                break;
            case "gym":
                placeName = "gym";
                break;
            case "gas station":
                placeName = "gas_station";
                break;
            case "cafe":
                placeName = "cafe";
                break;
            case "electronics store":
                placeName = "electronics_store";
                break;
            case "car wash":
                placeName = "car_wash";
                break;
        }
        mMap.clear();
        String url = getUrlFromPlceName(placeName);
        Object objectTransfer[] = new Object[2];
        objectTransfer[0]  = mMap;
        objectTransfer[1] = url;

        PlacesData placesData = new PlacesData();
        placesData.execute(objectTransfer);
        Toast.makeText(this, "Showing nearby "+ placeName, Toast.LENGTH_SHORT).show();

    }

    private String getUrlFromPlceName(String placeName) {


        String str_org = (lastLocation.getLatitude()) +","+(lastLocation.getLongitude());

        String key = "AIzaSyDNI6tfWpCP5JUG4DtkEdllgKLGztbMnvs";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+str_org);
        stringBuilder.append("&radius="+ PROXIMITY_RADIUS);
        stringBuilder.append("&types="+ placeName);
        stringBuilder.append("&sensor=true");
        stringBuilder.append("&key="+ key);

        return stringBuilder.toString();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        getSupportFragmentManager().executePendingTransactions();
    }



//drawer

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.nav_share:
                ShareHelp();
                break;
            case R.id.nav_video:
                Video_button();
                break;

            case R.id.nav_calender:
                Calender_button();
                break;
            case R.id.nav_call:
                CallSomeone();
                break;

            case R.id.send_new_message:
                SendNewMessage();
                break;
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




//map

    public boolean checkUserPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            return false;
        }else{
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Request_User_Location_Code:
                //if permissions is granted
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if(googleApiClient==null){

                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);

                    }
                }else{
                    Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        checkUserPermissions();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);


        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {

                if(arrayListOfPoints.size()==2)
                {
                    arrayListOfPoints.clear();
                    mMap.clear();

                }

                arrayListOfPoints.add(latLng);

                if(arrayListOfPoints.size()==1){

                    mMap.clear();

                    firstPoint=latLng;

                    mMap.addMarker(new MarkerOptions()
                            .position(latLng).title("My Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,5f));
                }else if(arrayListOfPoints.size()==2){
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng).title("Destination")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,3f));

                    String url = getRequestUrl(arrayListOfPoints.get(0),arrayListOfPoints.get(1));

                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                    taskRequestDirections.execute(url);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPoint,1f));

                    dialogTwoMarkers();

                }


            }
        });




    }


    protected synchronized void buildGoogleApiClient(){
        googleApiClient=new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }


    @Override
    public void onLocationChanged(Location location) {
        lastLocation=location;

        if(currentUserLocationMarker!= null){
            currentUserLocationMarker.remove();
        }




        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        arrayListFromCurrentPoint.clear();
        arrayListFromCurrentPoint.add(latlng);
        myLocation = latlng;
        MarkerOptions markerOptions= new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title("current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentUserLocationMarker=mMap.addMarker(markerOptions);
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        // mMap.animateCamera(CameraUpdateFactory.zoomBy(14));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15f));


        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List<Address> adressList = geocoder.getFromLocation(latitude, longitude, 1);
            myLocationName = adressList.get(0).getAddressLine(0).toString();

            Toast.makeText(this, myLocationName, Toast.LENGTH_SHORT).show();




        }catch (IOException e) {
            e.printStackTrace();
        }

        if(googleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }



    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest=new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);



        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
        }



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
//end map


    private void Speaker(){

        speechToText();

    }

    private void speechToText() {

        final String text = "Hey! how can I help you?";

        mTTs=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    int result= mTTs.setLanguage(Locale.getDefault());
                    if(result==TextToSpeech.LANG_MISSING_DATA
                            || result==TextToSpeech.LANG_NOT_SUPPORTED){

                        Log.e("TTS","language not supported");
                        Toast.makeText(StartActivity.this, "language not supported"
                                , Toast.LENGTH_SHORT).show();

                    }



                    mTTs.speak(text,TextToSpeech.QUEUE_FLUSH,null);

                }else{
                    Log.e("TTS","Initialization Failed");
                    Toast.makeText(StartActivity.this
                            , "Initialization Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


       Handler handler=new Handler();
       handler.postDelayed(new Runnable() {
           @Override
           public void run() {

               Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
               intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
               intent.putExtra(RecognizerIntent.EXTRA_RESULTS,10);

               intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hey! what would you like me do ?");



               try{
                   startActivityForResult(intent,REQEST_CODE_SPEECH_INPUT);

               }catch (Exception e){
                   Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
               }
           }
       },2000);
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQEST_CODE_SPEECH_INPUT:
                if(resultCode==RESULT_OK && data!=null){
                    getResultSpeech(data);
                }
                break;
            case PLACE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(data, this);
                    String toastMsg = String.format("Place: %s", place.getName());
                    Toast.makeText(StartActivity.this, toastMsg, Toast.LENGTH_LONG).show();
                    search_edit_text.setText(toastMsg);

                }
                break;


        }
    }



    private void getResultSpeech(Intent data) {

        //get string of text
        ArrayList<String> result= data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

        ResultOfSpeach=result.get(0);

        RecognizeSpeachToAction(ResultOfSpeach);

    }

    private void RecognizeSpeachToAction(String resultOfSpeach) {
        switch (resultOfSpeach){

            case "open calendar":
                Calender_button();
                break;

            case "send new message":
                SendNewMessage();
                break;
            case "record video":
                Video_button();
                break;
            case "open contacts":
                CallSomeone();
                break;
            case "get help":
                ShareHelp();

        }
    }

    private void ShareHelp() {
        Intent intent= new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.prompt));
        intent.setType("text/*");
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    private void Calender_button(){
        Intent CalenderIntent =new Intent(StartActivity.this,CalenderActivity.class);
        startActivity(CalenderIntent);
    }

    private void Video_button(){
        Intent videoIntent = new Intent(StartActivity.this, VideoActivity.class);
        videoIntent.putExtra("location",myLocationName);
        startActivity(videoIntent);
    }

    private void FindMe()
    {


        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(StartActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void CallSomeone(){
        Intent Call =new Intent(StartActivity.this,CallSomeone.class);
        Call.putExtra("location",myLocationName);
        Call.putExtra("userName",UserName);
        startActivity(Call);
    }


    private void SendNewMessage(){

        Intent intent = new Intent(StartActivity.this,SendMessage.class);
        intent.putExtra("userName",UserName);
        startActivity(intent);
    }





    private void getLongLatFromCityName(final String userCity) {

        Geocoder geocoder=new Geocoder(getApplicationContext());
        try {
            List<Address> adressList=geocoder.getFromLocationName(userCity,10);
            if(adressList!=null){

                longitude=adressList.get(0).getLongitude();
                latitude=adressList.get(0).getLatitude();
                currentLocation=new LatLng(latitude,longitude);

                arrayListFromCurrentPoint.clear();

                mMap.addMarker(new MarkerOptions()
                        .position(myLocation).title("My Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                arrayListFromCurrentPoint.add(myLocation);



                arrayListFromCurrentPoint.add(currentLocation);
                mMap.addMarker(new MarkerOptions()
                        .position(currentLocation).title("Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,4f));

                String url = getRequestUrl(arrayListFromCurrentPoint.get(0),arrayListFromCurrentPoint.get(1));
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                taskRequestDirections.execute(url);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,2f));

                dialog(userCity);
            }else{
                Toast.makeText(context, "Sorry, Could not find this address.", Toast.LENGTH_SHORT).show();
            }




        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void dialog(String userCity){

        final AlertDialog.Builder alertDialog=
                new AlertDialog.Builder(StartActivity.this,R.style.AlertDialogCustom);
        alertDialog.setTitle( context.getString(R.string.nevigate_sure) + "  " + userCity );


        final TextView textView =new TextView(context);
        textView.setText("");
        textView.setTextColor(ContextCompat.getColor(context,R.color.text_color));
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);


        final TextView textViewDetails =new TextView(context);
        if(route!=null){
            textViewDetails.setText("The distance is: "+route.distance.value+" The duration is: "+route.duration.value);
        }else{
            textViewDetails.setText("");
        }

        textViewDetails.setTextColor(ContextCompat.getColor(context,R.color.text_color));
        textViewDetails.setTextSize(18);
        textViewDetails.setGravity(Gravity.CENTER_HORIZONTAL);


        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL| Gravity.CENTER_VERTICAL);
        linearLayout.addView(textView,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        ,ViewGroup.LayoutParams.WRAP_CONTENT,0));

        linearLayout.addView(textViewDetails,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        ,ViewGroup.LayoutParams.WRAP_CONTENT,0));
        alertDialog.setView(linearLayout);

        alertDialog.setNeutralButton(R.string.no_navigation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton(R.string.start_navigate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //navigate
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,3f));
                dialog.dismiss();

            }
        });


        final AlertDialog alert = alertDialog.create();
        alert.show();



        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                textView.setText("" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {

                alert.dismiss();
            }

        }.start();

        alert.getButton(DialogInterface.BUTTON_NEUTRAL).setBackgroundResource(R.drawable.button_cancel);
        alert.getButton(DialogInterface.BUTTON_NEUTRAL).setPadding(10,5,10,5);
        alert.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(context,R.color.text_color));

        alert.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundResource(R.drawable.button_light);
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);

    }

    private void dialogTwoMarkers(){

        final AlertDialog.Builder alertDialog=
                new AlertDialog.Builder(StartActivity.this,R.style.AlertDialogCustom);
        alertDialog.setTitle( context.getString(R.string.navigate_two_markers)  );


        final TextView textViewDetails =new TextView(context);
        if(route!=null){
            textViewDetails.setText("The distance is: "+route.distance.value+" The duration is: "+route.duration.value);
        }else{
            textViewDetails.setText("");
        }

        textViewDetails.setTextColor(ContextCompat.getColor(context,R.color.text_color));
        textViewDetails.setTextSize(18);
        textViewDetails.setGravity(Gravity.CENTER_HORIZONTAL);

        final TextView textView =new TextView(context);
        textView.setText("");
        textView.setTextColor(ContextCompat.getColor(context,R.color.text_color));
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);


        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL| Gravity.CENTER_VERTICAL);
        linearLayout.addView(textView,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        ,ViewGroup.LayoutParams.WRAP_CONTENT,0));
        linearLayout.addView(textViewDetails,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        ,ViewGroup.LayoutParams.WRAP_CONTENT,0));
        alertDialog.setView(linearLayout);

        alertDialog.setNeutralButton(R.string.no_navigation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton(R.string.start_navigate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //navigate

                dialog.dismiss();

            }
        });


        final AlertDialog alert = alertDialog.create();
        alert.show();



        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                textView.setText("" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {

                alert.dismiss();
            }

        }.start();

        alert.getButton(DialogInterface.BUTTON_NEUTRAL).setBackgroundResource(R.drawable.button_cancel);
        alert.getButton(DialogInterface.BUTTON_NEUTRAL).setPadding(10,5,10,5);
        alert.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(context,R.color.text_color));

        alert.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundResource(R.drawable.button_light);
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);

    }

    private String getRequestUrl(LatLng start_point, LatLng dest_point) {

        String str_org = "origin="+ String.valueOf(start_point.latitude) +","+String.valueOf(start_point.longitude);
        String str_dest = "destination="+ String.valueOf(dest_point.latitude)+","+String.valueOf(dest_point.longitude);
        String sensor = "sensor=false";

        String key = "key=AIzaSyAn2siGJTLUfFTjxJmh1A4cc5jYNt17hzw";
        String mode = "mode=driving";
        String param =str_org+ "&" + str_dest+"&"+ mode+"&"+key;


        String url = "https://maps.googleapis.com/maps/api/directions/json?"+param;
        return url;
    }

    private String requsetDirections(String req_url) throws IOException {
        String responseString ="";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(req_url);
            httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.connect();

            //get response result
            inputStream=httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader =new InputStreamReader(inputStream);
            BufferedReader bufferedReader =new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer =new StringBuffer();
            String line ="";
            while ((line=bufferedReader.readLine())!=null)
            {
                stringBuffer.append(line);
            }
            responseString=stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();


        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(inputStream!=null)
            {
                inputStream.close();
            }
            httpURLConnection.disconnect();

        }
        return responseString;
    }



    public class TaskRequestDirections extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... strings) {

            String responseString ="";
            try{
                responseString = requsetDirections(strings[0]);

            }catch (IOException e)
            {

                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //parse jason here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);


        }
    }

    public class TaskParser extends AsyncTask<String,Void,List<List<HashMap<String, String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {

            JSONObject jsonObject=null;

            List<List<HashMap<String, String>>> routs=null;
            try {
                jsonObject= new JSONObject(strings[0]);
                Log.d("json",jsonObject.toString());
                DirectionsParser directionsParser = new DirectionsParser();
                routs=directionsParser.parse(jsonObject);
                route = directionsParser.route;



            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  routs;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
           //get list of routes and display it on map

            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            for(List<HashMap<String, String>> path : lists)
            {
                points=new ArrayList();
                polylineOptions=new PolylineOptions();

                for(HashMap<String, String> point : path)
                {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));
                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);

            }
            if(polylineOptions!=null)
            {
                mMap.addPolyline(polylineOptions);

            }else{
                Toast.makeText(context, "Direction not found!", Toast.LENGTH_SHORT).show();

            }

        }
    }


    private void Userdialog(){

        final AlertDialog.Builder alertDialog=
                new AlertDialog.Builder(StartActivity.this,R.style.AlertDialogCustom);
        alertDialog.setTitle(R.string.enter );

        final EditText textView =new EditText(context);
        textView.setText("");
        textView.setTextColor(ContextCompat.getColor(context,R.color.text_color));
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);


        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL| Gravity.CENTER_VERTICAL);
        linearLayout.addView(textView,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        ,ViewGroup.LayoutParams.WRAP_CONTENT,0));
        alertDialog.setView(linearLayout);

        alertDialog.setNeutralButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton(R.string.start_navigate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //navigate
                UserName=textView.getText().toString();
                dialog.dismiss();

            }
        });


        final AlertDialog alert = alertDialog.create();
        alert.show();


        alert.getButton(DialogInterface.BUTTON_NEUTRAL).setBackgroundResource(R.drawable.button_cancel);
        alert.getButton(DialogInterface.BUTTON_NEUTRAL).setPadding(10,5,10,5);
        alert.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(context,R.color.text_color));

        alert.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundResource(R.drawable.button_light);
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);

    }


}


