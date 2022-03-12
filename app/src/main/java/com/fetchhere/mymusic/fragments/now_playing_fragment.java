package com.fetchhere.mymusic.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fetchhere.mymusic.MainActivity;
import com.fetchhere.mymusic.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class now_playing_fragment extends Fragment {

    Button btnPlay, btnNext, btnPrevious;
    TextView txtSongName,txtArtistName, txtSongStart, txtSongEnd;
    SeekBar seekMusicBar;
    boolean isSeeking=false;

    ImageView imgAlbumImage;

    Thread updateSeekBar;

    public ArrayList<File> queue;
    Context thisContext;
    static MediaPlayer mediaPlayer;
    int pos = -1; //current song temp pos

    SharedPreferences sharedPreferencesVariables;
    SharedPreferences.Editor editor;
    public static final String SHARED_PREF_KEY = "shared Preferences Variables";
    public static final String CURR_SONG_KEY = "current Song Index";

    public now_playing_fragment(ArrayList<File> queueList){
        //allSongs =allSongsList;
        queue =queueList;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*thisContext=container.getContext();
        sharedPreferencesVariables=this.getActivity().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);*/
        //SharedPreferences.Editor editor = sharedPreferencesVariables.edit();
        thisContext=this.getActivity();
        sharedPreferencesVariables=thisContext.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferencesVariables.edit();

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

        //Implementing OnClickListener for previous Button
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPlay.setBackgroundResource(R.drawable.ic_pause);

                //Checking for list start
                if (pos>0) {
                    pos-=1; //previous index
                    //SharedPreferences.Editor editor = sharedPreferencesVariables.edit();
                    editor.putInt(CURR_SONG_KEY, pos);
                    editor.commit();
                    if(queue.size()>0&& queue.get(pos).exists()) {
                        setSongDetails(pos);
                        play_current_song(pos);
                    }
                    else{
                        txtSongName.setText("Song Doesn't Exist!!");
                        txtArtistName.setText("please update list by rescanning");
                        btnPlay.setBackgroundResource(R.drawable.ic_play);
                        if(mediaPlayer!=null){
                            mediaPlayer.stop();
                        }
                    }

                } else {
                    pos= queue.size()-1;
                    //SharedPreferences.Editor editor = sharedPreferencesVariables.edit();
                    editor.putInt(CURR_SONG_KEY, pos);
                    editor.commit();
                    if(queue.size()>0&& queue.get(pos).exists()) {
                        setSongDetails(pos);
                        play_current_song(pos);
                    }
                    else{
                        txtSongName.setText("Song Doesn't Exist!!");
                        txtArtistName.setText("please update list by rescanning");
                        btnPlay.setBackgroundResource(R.drawable.ic_play);
                        if(mediaPlayer!=null){
                            mediaPlayer.stop();
                        }
                    }
                }
            }
        });

        //Implementing OnClickListener for next Button
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPlay.setBackgroundResource(R.drawable.ic_pause);

                //Checking for list end
                if (pos< queue.size()-1) {
                    pos+=1; //next index
                    //SharedPreferences.Editor editor = sharedPreferencesVariables.edit();
                    editor.putInt(CURR_SONG_KEY, pos);
                    editor.commit();
                    if(queue.size()>0&& queue.get(pos).exists()) {
                        setSongDetails(pos);
                        play_current_song(pos);
                    }
                    else{
                        txtSongName.setText("Song Doesn't Exist!!");
                        txtArtistName.setText("please update list by rescanning");
                        btnPlay.setBackgroundResource(R.drawable.ic_play);
                        if(mediaPlayer!=null){
                            mediaPlayer.stop();
                        }
                    }

                } else {
                    pos=0;
                    //SharedPreferences.Editor editor = sharedPreferencesVariables.edit();
                    editor.putInt(CURR_SONG_KEY, pos);
                    editor.commit();
                    if(queue.size()>0&& queue.get(pos).exists()) {
                        setSongDetails(pos);
                        play_current_song(pos);
                    }
                    else{
                        txtSongName.setText("Song Doesn't Exist!!");
                        txtArtistName.setText("please update list by rescanning");
                        btnPlay.setBackgroundResource(R.drawable.ic_play);
                        if(mediaPlayer!=null){
                            mediaPlayer.stop();
                        }
                    }
                }
            }
        });

        //Implementing OnClickListener for Play and Pause Button
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Checking playing any songs or not
                if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {

                    //setting the play icon
                    btnPlay.setBackgroundResource(R.drawable.ic_play);

                    //Pausing the current media
                    mediaPlayer.pause();

                } else {

                    //Setting the pause icon
                    btnPlay.setBackgroundResource(R.drawable.ic_pause);

                    //Starting the media player
                    if(pos==sharedPreferencesVariables.getInt(CURR_SONG_KEY,0)){

                        if(queue.size()>0&& queue.get(pos).exists()) {
                            mediaPlayer.start();
                        }
                        else{
                            txtSongName.setText("Song Doesn't Exist!!");
                            txtArtistName.setText("please update list by rescanning");
                            btnPlay.setBackgroundResource(R.drawable.ic_play);
                            if(mediaPlayer!=null){
                                mediaPlayer.stop();
                            }
                        }
                    }
                    else{
                        pos=sharedPreferencesVariables.getInt(CURR_SONG_KEY,0);
                        if(queue.size()>0&& queue.get(pos).exists()) {
                            setSongDetails(pos);
                            play_current_song(pos);
                        }
                        else{
                            txtSongName.setText("Song Doesn't Exist!!");
                            txtArtistName.setText("please update list by rescanning");
                            btnPlay.setBackgroundResource(R.drawable.ic_play);
                            if(mediaPlayer!=null){
                                mediaPlayer.stop();
                            }
                        }
                    }
                }
            }
        });



    }

    public void play_current_song(final int position){

        if (mediaPlayer!=null){ mediaPlayer.stop();}
            mediaPlayer = MediaPlayer.create(thisContext, Uri.parse(queue.get(position).toString()));
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    ///mediaPlayer.release();
                    if(pos< queue.size()-1){
                        pos+=1;
                        //SharedPreferences.Editor editor = sharedPreferencesVariables.edit();
                        editor.putInt(CURR_SONG_KEY, pos);
                        editor.commit();
                        if(queue.size()>0&& queue.get(pos).exists()) {
                            setSongDetails(pos);
                            play_current_song(pos);
                        }
                        else{
                            txtSongName.setText("Song Doesn't Exist!!");
                            txtArtistName.setText("please update list by rescanning");
                            btnPlay.setBackgroundResource(R.drawable.ic_play);
                            if(mediaPlayer!=null){
                                mediaPlayer.stop();
                            }
                        }
                    }
                    else {
                        pos=0;
                        //SharedPreferences.Editor editor = sharedPreferencesVariables.edit();
                        editor.putInt(CURR_SONG_KEY, 0);
                        editor.commit();
                        if(queue.size()>0&& queue.get(pos).exists()) {
                            setSongDetails(pos);
                            play_current_song(pos);
                        }
                        else{
                            txtSongName.setText("Song Doesn't Exist!!");
                            txtArtistName.setText("please update list by rescanning");
                            btnPlay.setBackgroundResource(R.drawable.ic_play);
                            if(mediaPlayer!=null){
                                mediaPlayer.stop();
                            }
                        }
                    }
                }
            });

        mediaPlayer.start();
        updateSeekBar = new Thread() {
            @Override
            public void run() {

                int TotalDuration = mediaPlayer.getDuration();
                int CurrentPosition = 0;

                while (CurrentPosition < TotalDuration) {
                    try {
                        if(!isSeeking){
                            sleep(500);
                            CurrentPosition = mediaPlayer.getCurrentPosition();
                            seekMusicBar.setProgress(CurrentPosition);
                        }


                    } catch (InterruptedException | IllegalStateException e) {

                        e.printStackTrace();
                    }
                }

            }
        };
        seekMusicBar.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();
        seekMusicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            isSeeking=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //getting the progress of the seek bar and setting it to Media Player
                if(queue.size()>0&& queue.get(pos).exists()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
                else{
                    seekBar.setMax(0);
                    seekBar.setProgress(0);
                }
                isSeeking=false;

            }
        });
        //Creating the Handler to update the current duration
        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //Getting the current duration from the media player
                String currentTime = createDuration(mediaPlayer.getCurrentPosition());

                //Setting the current duration in textView
                txtSongStart.setText(currentTime);
                handler.postDelayed(this, delay);

            }
        }, delay);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(queue!=((MainActivity)getActivity()).readListFromPref(thisContext.getApplicationContext(),((MainActivity)getActivity()).QUEUE_KEY)){
            queue=((MainActivity)getActivity()).readListFromPref(thisContext.getApplicationContext(),((MainActivity)getActivity()).QUEUE_KEY);
        }

        //ERROR : if shared preference is greater than song list. FIXED ig
        if(pos==sharedPreferencesVariables.getInt(CURR_SONG_KEY,0)){
            if(mediaPlayer==null) btnPlay.setBackgroundResource(R.drawable.ic_play);
            else if(mediaPlayer.isPlaying()&& queue.size()>0&& queue.get(pos).exists())  btnPlay.setBackgroundResource(R.drawable.ic_pause);
            else btnPlay.setBackgroundResource(R.drawable.ic_play);
        }
        else{
            btnPlay.setBackgroundResource(R.drawable.ic_pause);
            pos=sharedPreferencesVariables.getInt(CURR_SONG_KEY,0);
            if(queue.size()>0&& queue.get(pos).exists()) {
                setSongDetails(pos);
                play_current_song(pos);
            }
            else{
                txtSongName.setText("Song Doesn't Exist!!");
                txtArtistName.setText("please update list by rescanning");
                btnPlay.setBackgroundResource(R.drawable.ic_play);
                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //mediaPlayer.pause();
    }

    void setSongDetails(int position){
        //File song = AllSongsArrayList.get(position);
        //txtSongName.setText(song.getName().toString().replace(".mp3", "").replace(".wav", ""));
        //getting other info
        txtSongName.setText(queue.get(position).getName().replace(".mp3", "").replace(".wav", ""));
        txtArtistName.setText("<unknown>");

        String CanonicalPath= queue.get(position).getAbsolutePath();
        try {
            CanonicalPath = queue.get(position).getCanonicalPath();
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
            txtSongEnd.setText(createDuration(parseInt(c.getString(6)))); //getting length
            c.getString(7);
        }
        c.close();

    }

    //Preparing the Time format for setting to textView
    public String createDuration(int duration) {

        String time = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        time = time + min + ":";

        if (sec < 10) {

            time += "0";

        }
        time += sec;
        return time;

    }
}