package com.fetchhere.mymusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.fetchhere.mymusic.fragments.all_songs_fragment;
import com.fetchhere.mymusic.fragments.favourite_fragment;
import com.fetchhere.mymusic.fragments.now_playing_fragment;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private TabLayout tab_layout;
    private ViewPager view_pager;
    public view_page_adapter adapter;

    ArrayList<File> allSongs,queue;

    SharedPreferences sharedPreferencesVariables;
    SharedPreferences.Editor editor;
    public static final String SHARED_PREF_KEY = "shared Preferences Variables";
    public static final String SORT_KEY = "current sort";
    public static final String CURR_SONG_KEY = "current Song Index";
    public static final String LIST_KEY = "all songs";
    public static final String QUEUE_KEY = "queue";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tab_layout = findViewById(R.id.tab_layout);
        view_pager = findViewById(R.id.view_pager);

        tab_layout.setupWithViewPager(view_pager);

        sharedPreferencesVariables=this.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferencesVariables.edit();

        checkForPermission();

    }

    void checkForPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_DENIED) {
            requestPermission();
        } else continueActivity();

    }

    void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{READ_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        continueActivity();
                    } else {
                        requestPermission();
                    }
                }
            }
        }
    }

    public static void writeArrayListInPref(Context context, ArrayList<File> list, String key) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, jsonString);
        editor.apply();
    }

    public static ArrayList<File> readListFromPref(Context context,String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString(key, "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<File>>() {}.getType();
        ArrayList<File> list = gson.fromJson(jsonString, type);
        return list;
    }

    public ArrayList<File> findSong(File rootFolder) {

        //ArrayList to store all songs
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = rootFolder.listFiles();
        Uri uri;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();


        for (File singleFile : files) {

            //Adding the directory to arrayList if it is not hidden
            if (singleFile.isDirectory() && !singleFile.isHidden()) {

                File noMedia=new File(singleFile.getAbsolutePath()+"/.nomedia");
                if(!noMedia.exists()) arrayList.addAll(findSong(singleFile));

            } else {
                //Adding the single music file to ArrayList
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {

                    uri = Uri.parse(singleFile.getAbsolutePath());
                    mmr.setDataSource(this,uri);
                    String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    int millSecond = Integer.parseInt(durationStr);
                    if(millSecond>60000)arrayList.add(singleFile);
                }
            }
        }

        return arrayList;
    }

    void scanMusic(){


        File[] fileDirectory = ContextCompat.getExternalFilesDirs(getApplicationContext(),null);
        for (int i = 0; i< fileDirectory.length; i++)
        {
            String path = fileDirectory[i].getParent().replace("/Android/data/","").replace(getPackageName(),"");
            fileDirectory[i]= new File(path);
        }

        allSongs=findSong(fileDirectory[0]);
        for(int i = 1; i< fileDirectory.length; i++)
        allSongs.addAll(findSong(fileDirectory[i]));

        writeArrayListInPref(this,allSongs,LIST_KEY);
        editor.putInt(CURR_SONG_KEY, 0);
        editor.putInt(SORT_KEY, 0);
        editor.commit();

    }

    void continueActivity() {

        allSongs = readListFromPref(this,LIST_KEY);
        if (allSongs == null)
            scanMusic();
        queue= readListFromPref(this,QUEUE_KEY);
        if (queue == null) {
            queue = (ArrayList<File>) allSongs.clone();
            writeArrayListInPref(this,queue,QUEUE_KEY);
        }

        adapter = new view_page_adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragments(new all_songs_fragment(allSongs,queue), "All songs");
        adapter.addFragments(new now_playing_fragment(queue), "Playing");
        adapter.addFragments(new favourite_fragment(), "Favourites");
        view_pager.setAdapter(adapter);

    }


}