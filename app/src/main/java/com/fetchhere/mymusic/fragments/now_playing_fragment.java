package com.fetchhere.mymusic.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
    ImageView imgAlbumImage;

    SeekBar seekMusicBar;
    boolean isSeeking=false;
    Thread updateSeekBar;

    public ArrayList<File> queue;
    Context thisContext;
    static MediaPlayer mediaPlayer;
    int pos = -1; //current song position (-1 => no songs)

    SharedPreferences sharedPreferencesVariables;
    SharedPreferences.Editor editor;
    public static final String SHARED_PREF_KEY = "shared Preferences Variables";
    public static final String CURR_SONG_KEY = "current Song Index";

    public now_playing_fragment(ArrayList<File> queueList){
        queue =queueList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisContext=this.getActivity(); //getting context

        assert thisContext != null;
        sharedPreferencesVariables=thisContext.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferencesVariables.edit();

        return inflater.inflate(R.layout.fragment_now_playing_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
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
        btnPrevious.setOnClickListener(view1 -> {
            btnPlay.setBackgroundResource(R.drawable.ic_pause);

            //Checking for list start
            if (pos>0) {
                pos-=1; //previous index
                editor.putInt(CURR_SONG_KEY, pos);
                editor.commit();
                if(queue.size()>0&& queue.get(pos).exists()) {
                    setSongDetails(pos);
                    play_current_song(pos); //playing previous song if exist
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
            //playing last song if it was 1st song
            else {
                pos= queue.size()-1;
                editor.putInt(CURR_SONG_KEY, pos);
                editor.commit();
                if(queue.size()>0&& queue.get(pos).exists()) {
                    setSongDetails(pos);
                    play_current_song(pos); //playing last song
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
        });

        //Implementing OnClickListener for next Button
        btnNext.setOnClickListener(view12 -> {
            btnPlay.setBackgroundResource(R.drawable.ic_pause);
            //Checking for list end
            if (pos< queue.size()-1) {
                pos+=1; //next index
                editor.putInt(CURR_SONG_KEY, pos);
                editor.commit();
                if(queue.size()>0&& queue.get(pos).exists()) {
                    setSongDetails(pos);
                    play_current_song(pos); //playing next song if it exist
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
            //playing 1st song if it was last song
            else {
                pos=0;
                editor.putInt(CURR_SONG_KEY, pos);
                editor.commit();
                if(queue.size()>0&& queue.get(pos).exists()) {
                    setSongDetails(pos);
                    play_current_song(pos); //playing 1st song
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
        });

        //Implementing OnClickListener for Play and Pause Button
        btnPlay.setOnClickListener(view13 -> {
            //Checking if playing any songs or not
            if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
                btnPlay.setBackgroundResource(R.drawable.ic_play);
                //Pausing the current media
                mediaPlayer.pause();
            }
            else {
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                //Starting the media player
                if(pos==sharedPreferencesVariables.getInt(CURR_SONG_KEY,0)){ //if we are on correct song index
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
                    pos=sharedPreferencesVariables.getInt(CURR_SONG_KEY,0); //getting current song index from shared preferences
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
    }

    @Override
    public void onResume() {
        /*In this method, app will refresh all its variables*/
        super.onResume();

        //updating queue
        if(queue!=((MainActivity)getActivity()).readListFromPref(thisContext.getApplicationContext(),((MainActivity)getActivity()).QUEUE_KEY)){
            queue=((MainActivity)getActivity()).readListFromPref(thisContext.getApplicationContext(),((MainActivity)getActivity()).QUEUE_KEY);
        }

        //ERROR : if shared preference is greater than song list. FIXED I guess
        if(pos==sharedPreferencesVariables.getInt(CURR_SONG_KEY,0)){ //update only buttons if we are on correct song index
            if(mediaPlayer==null) btnPlay.setBackgroundResource(R.drawable.ic_play);
            else if(mediaPlayer.isPlaying()&& queue.size()>0&& queue.get(pos).exists())  btnPlay.setBackgroundResource(R.drawable.ic_pause);
            else btnPlay.setBackgroundResource(R.drawable.ic_play);
            /*BUG: if song with index "n" was/is playing last time and user changed sorting order or shuffled the list,
                    and then user tap on song with index "n" (which is a different song this time)
                    then app will keep on playing the older song. user has to tap on any other song
                    to see the effect in now_playing_fragment.*/
        }
        else{ //update variables and play song if we are not at correct song index
            btnPlay.setBackgroundResource(R.drawable.ic_pause);
            pos=sharedPreferencesVariables.getInt(CURR_SONG_KEY,0); //getting current song index from shared preferences
            if(queue.size()>0&& queue.get(pos).exists()) {
                setSongDetails(pos); //setting song details
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
    }

    //function to play song at given index
    public void play_current_song(final int position){
        if (mediaPlayer!=null){
            mediaPlayer.stop();
        }

        mediaPlayer = MediaPlayer.create(thisContext, Uri.parse(queue.get(position).toString()));
        mediaPlayer.setOnCompletionListener(mp -> {
            //play next song on completing the current song
            if(pos< queue.size()-1){
                pos+=1;
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
        });

        mediaPlayer.start();
        updateSeekBar = new Thread() { //thread to update seekbar after every half second
            @Override
            public void run() {
                int TotalDuration = mediaPlayer.getDuration();
                int CurrentPosition = 0;
                while (CurrentPosition < TotalDuration) {
                    try {
                        if(!isSeeking){ //update only if user is not seeking
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

        //when user seeks seekbar
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
        final Handler handler = new Handler(); //handler to update current duration text after every second
        final int delay = 1000; //1 second delay

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

    void setSongDetails(int position){
        //setting default song and artist name
        txtSongName.setText(queue.get(position).getName().replace(".mp3", "").replace(".wav", ""));
        txtArtistName.setText("<unknown>");

        //fetching song details
        String CanonicalPath= queue.get(position).getAbsolutePath();
        try {
            CanonicalPath = queue.get(position).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Cursor c = thisContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        //song details
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

        if (null != c) {
            while (c.moveToNext()) {
                c.getString(0);
                txtArtistName.setText(c.getString(1)); //getting artist name
                txtSongName.setText(c.getString(3)); //getting song name
                c.getString(4);
                c.getString(5);
                txtSongEnd.setText(createDuration(parseInt(c.getString(6)))); //getting length
                c.getString(7);
            }
        }
        assert c != null;
        c.close();
    }

    //Preparing the Time format in standard form
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