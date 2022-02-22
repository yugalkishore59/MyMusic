package com.fetchhere.mymusic.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fetchhere.mymusic.R;
import com.fetchhere.mymusic.RecyclerViewAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class now_playing_fragment extends Fragment {

    Button btnPlay, btnNext, btnPrevious;
    TextView txtSongName,txtArtistName, txtSongStart, txtSongEnd;
    SeekBar seekMusicBar;


    ImageView imgAlbumImage;

    public ArrayList<File> AllSongsArrayList;
    Context thisContext;
    MediaPlayer mediaPlayer;
    int pos = -1; //current song temp pos

    SharedPreferences sharedPreferencesVariables;
    SharedPreferences.Editor editor;

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
        sharedPreferencesVariables=this.getActivity().getSharedPreferences("shared Preferences Variables", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesVariables.edit();
        return inflater.inflate(R.layout.fragment_now_playing_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPlay = (Button) view.findViewById(R.id.btn_play_pause);
        btnNext = (Button) view.findViewById(R.id.btn_next);
        btnPrevious = (Button) view.findViewById(R.id.btn_previous);

        txtSongName = (TextView) view.findViewById(R.id.now_playing_song_name);
        txtSongStart = (TextView) view.findViewById(R.id.now_playing_song_cur_time);
        txtSongEnd = (TextView) view.findViewById(R.id.now_playing_song_end_time);
        txtArtistName=(TextView) view.findViewById(R.id.now_playing_song_artist);

        seekMusicBar = (SeekBar) view.findViewById(R.id.now_playing_song_seekBar);

        imgAlbumImage = (ImageView) view.findViewById(R.id.now_playing_song_image);

    }

    public void play_current_song(final int pos){

        if (mediaPlayer!=null){ mediaPlayer.stop();}
            mediaPlayer = MediaPlayer.create(thisContext, Uri.parse(AllSongsArrayList.get(pos).toString()));
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    ///mediaPlayer.release();
                    if(pos<AllSongsArrayList.size()+1){
                        play_current_song(pos+1);
                    }
                    else
                        play_current_song(0);
                }
            });

        mediaPlayer.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(pos==sharedPreferencesVariables.getInt("currentSongIndex",0)){

        }
        else{
            pos=sharedPreferencesVariables.getInt("currentSongIndex",0);
            setSongDetails(pos);
            play_current_song(pos);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //mediaPlayer.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mediaPlayer.release();
    }

    void setSongDetails(int position){
        //File song = AllSongsArrayList.get(position);
        //txtSongName.setText(song.getName().toString().replace(".mp3", "").replace(".wav", ""));
        //getting other info
        String CanonicalPath=AllSongsArrayList.get(position).getAbsolutePath();
        try {
            CanonicalPath = AllSongsArrayList.get(position).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Cursor c = thisContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.TRACK,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.YEAR
                },
                MediaStore.Audio.Media.DATA + " = ?",
                new String[] {
                        CanonicalPath
                },
                "");

        if (null == c) {
            // ERROR
        }

        while (c.moveToNext()) {
            c.getString(0);
            txtArtistName.setText(c.getString(1)); //getting artist name
            txtSongName.setText(c.getString(3)); //getting song name
            c.getString(4);
            c.getString(5);
            txtSongEnd.setText(c.getString(6)); //getting length
            c.getString(7);
        }
    }
}