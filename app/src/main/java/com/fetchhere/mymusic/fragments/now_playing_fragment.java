package com.fetchhere.mymusic.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fetchhere.mymusic.R;
import com.fetchhere.mymusic.RecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;

public class now_playing_fragment extends Fragment {

    public ArrayList<File> AllSongsArrayList;
    Context thisContext;
    MediaPlayer mediaPlayer;

    public now_playing_fragment(ArrayList<File> SongsArrayList){
        AllSongsArrayList=SongsArrayList;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisContext=container.getContext();
        return inflater.inflate(R.layout.fragment_now_playing_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void play_current_song(int pos){

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(thisContext, Uri.parse(AllSongsArrayList.get(pos).toString()));
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                }
            });
        }

        mediaPlayer.start();
    }
}