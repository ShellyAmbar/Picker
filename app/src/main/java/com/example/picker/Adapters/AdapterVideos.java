package com.example.picker.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.picker.Models.ModelVideo;
import com.example.picker.R;

import java.io.File;
import java.util.List;

public class AdapterVideos extends RecyclerView.Adapter<AdapterVideos.viewHolder>{

    private List<ModelVideo> modelVideoList;
    private Context context;
    private VideoView videoView;

    public AdapterVideos(List<ModelVideo> modelVideoList, Context context, VideoView videoView) {
        this.modelVideoList = modelVideoList;
        this.context = context;
        this.videoView=videoView;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video,parent,false);
        return new AdapterVideos.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        final ModelVideo modelVideo = modelVideoList.get(position);
        holder.video_title.setText(modelVideo.getVideoName());

        holder.video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                videoView.setVideoURI(Uri.parse(modelVideo.getVideoUri()));
                videoView.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelVideoList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        private ImageButton video_btn;
        private TextView video_title;
        public viewHolder(View itemView) {
            super(itemView);

            video_btn = itemView.findViewById(R.id.video_btn);
            video_title=itemView.findViewById(R.id.video_title);

        }
    }
    public void setList(List<ModelVideo> newList){
        modelVideoList=newList;
    }
}
