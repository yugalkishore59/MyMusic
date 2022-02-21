package com.fetchhere.mymusic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.fetchhere.mymusic.fragments.now_playing_fragment;

import java.io.File;
import java.util.List;

public class RecyclerViewAdapter extends  RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<File> songtList;

    public RecyclerViewAdapter(Context context, List<File> songtList) {
        this.context = context;
        this.songtList = songtList;
    }
    // Where to get the single card as viewholder Object
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_info_card, parent, false);
        return new ViewHolder(view);
    }

    // What to do with the viewholder object
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        File song = songtList.get(position);

        holder.songName.setText(song.getName().toString().replace(".mp3", "").replace(".wav", ""));
        //holder.artistName.setText(song.());
    }

    //how many  viewholder objects
    @Override
    public int getItemCount() {
        return songtList.size();
    }

    //what to do when we click on viewholder object
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView songName;
        public TextView artistName;
        public ImageView iconButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            songName = itemView.findViewById(R.id.song_name_card);
            artistName = itemView.findViewById(R.id.artist_name_card);
            iconButton = itemView.findViewById(R.id.song_image_card);

            iconButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();
            ViewPager viewPager = (ViewPager) ((Activity)context).findViewById(R.id.view_pager);
            viewPager.setCurrentItem(1);
        }
    }
}


