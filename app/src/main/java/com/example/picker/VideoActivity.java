package com.example.picker;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.picker.Adapters.AdapterVideos;
import com.example.picker.Models.ModelVideo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoActivity extends AppCompatActivity {
    private AdapterVideos adapterVideos;
    private List<ModelVideo> modelVideoList;
    private VideoView video_view;
    private Button add_video_btn,record_video_btn;
    private RecyclerView recycler;
    private Uri videoUri;
    private EditText video_title;
    static final int REQUEST_VIDEO_CAPTURE = 100;
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=200;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);
        video_view=findViewById(R.id.video_view);
        add_video_btn=findViewById(R.id.add_video_btn);
        record_video_btn=findViewById(R.id.record_video_btn);
        recycler=findViewById(R.id.recycler);
        video_title=findViewById(R.id.video_title);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        ///request permission
        RequestPermissionReadExternalStorage();
        modelVideoList= GetAllVideoFromMemory();
        //modelVideoList=new ArrayList<>();
        adapterVideos=new AdapterVideos(modelVideoList, VideoActivity.this,video_view);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapterVideos);
        videoUri=null;




        record_video_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            REQUEST_VIDEO_CAPTURE);
                }else{
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                    }
                }


            }
        });

        add_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title;
                if(videoUri!=null)
                {
                    title=video_title.getText().toString();
                    ModelVideo modelVideo = new ModelVideo();
                    modelVideo.setVideoName(title);
                    modelVideo.setVideoUri(videoUri.toString());

                    modelVideoList.add(modelVideo);
                    adapterVideos.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(VideoActivity.this, "You need to record a new video.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }




    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUri = intent.getData();
            video_view.setVideoURI(videoUri);
            video_view.start();
        }
    }


    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_VIDEO_CAPTURE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }

            } else {

                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();

            }

        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        SetAllVideosToMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SetAllVideosToMemory();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SetAllVideosToMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
       modelVideoList= GetAllVideoFromMemory();
       adapterVideos.setList(modelVideoList);
       adapterVideos.notifyDataSetChanged();
    }

    private void SetAllVideosToMemory(){

        SharedPreferences sharedPreferences =getSharedPreferences("share preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor =sharedPreferences.edit();
        Gson gson =new Gson();
        String gsonList = gson.toJson(modelVideoList);
        editor.putString("modelList",gsonList);
        editor.apply();



    }
    private List<ModelVideo> GetAllVideoFromMemory(){
        List<ModelVideo> list = new ArrayList<>();

        SharedPreferences sharedPreferences =getSharedPreferences("share preferences",MODE_PRIVATE);
        Gson gson =new Gson();
        String gasonList = sharedPreferences.getString("modelList",null);
        Type type = new TypeToken<ArrayList<ModelVideo>>(){}.getType();
        list = gson.fromJson(gasonList,type);
        if(list==null){
            list = new ArrayList<>();
        }

        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void RequestPermissionReadExternalStorage(){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {



            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }
    }




}
