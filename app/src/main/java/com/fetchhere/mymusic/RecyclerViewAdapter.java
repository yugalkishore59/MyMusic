package com.fetchhere.mymusic;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RecyclerViewAdapter extends  RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<File> queue;

    SharedPreferences sharedPreferencesVariables;
    public static final String SHARED_PREF_KEY = "shared Preferences Variables";
    public static final String CURR_SONG_KEY = "current Song Index";

    public RecyclerViewAdapter(Context context, List<File> songtList) {
        this.context = context;
        this.queue = songtList;
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
        //getting song name
        File song = queue.get(position);
        holder.songName.setText(song.getName().toString().replace(".mp3", "").replace(".wav", ""));
        //getting other info
        String CanonicalPath= queue.get(position).getAbsolutePath();
        try {
            CanonicalPath = queue.get(position).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Cursor c = context.getContentResolver().query(
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
            holder.artistName.setText(c.getString(1)); //getting artist name
            c.getString(2);
            c.getString(3);
            c.getString(4);
            c.getString(5);
            c.getString(6);
            c.getString(7);
        }
    }

    //how many  viewholder objects
    @Override
    public int getItemCount() {
        return queue.size();
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
            sharedPreferencesVariables=context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesVariables.edit();
            editor.putInt(CURR_SONG_KEY, position);
            editor.commit();
            viewPager.setCurrentItem(1);
        }
    }
}


