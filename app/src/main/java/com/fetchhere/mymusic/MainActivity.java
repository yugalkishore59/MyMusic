package com.fetchhere.mymusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

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

    ArrayList<File> allSongs;

    private static final String LIST_KEY = "all songs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tab_layout = findViewById(R.id.tab_layout);
        view_pager = findViewById(R.id.view_pager);

        tab_layout.setupWithViewPager(view_pager);

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

    public static void writeArrayListInPref(Context context, ArrayList<File> list) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(LIST_KEY, jsonString);
        editor.apply();
    }

    public static ArrayList<File> readListFromPref(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString(LIST_KEY, "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<File>>() {}.getType();
        ArrayList<File> list = gson.fromJson(jsonString, type);
        return list;
    }

    public ArrayList<File> findSong(File rootFolder) {

        //ArrayList to store all songs
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = rootFolder.listFiles();

        for (File singleFile : files) {

            //Adding the directory to arrayList if it is not hidden
            if (singleFile.isDirectory() && !singleFile.isHidden()) {

                arrayList.addAll(findSong(singleFile));

            } else {
                //Adding the single music file to ArrayList
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                    arrayList.add(singleFile);
                }
            }
        }

        return arrayList;
    }

    void scanMusic(){
        File sdfile = new File("/storage/3263-3464");
        allSongs=findSong(sdfile);
        writeArrayListInPref(this,allSongs);
    }

    void listSongs(){


    }

    void continueActivity() {

        view_page_adapter adapter = new view_page_adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragments(new all_songs_fragment(), "All songs");
        adapter.addFragments(new now_playing_fragment(), "Now Playing");
        adapter.addFragments(new favourite_fragment(), "Favourites");
        view_pager.setAdapter(adapter);

        allSongs = readListFromPref(this);

        if (allSongs == null)
            scanMusic();

        listSongs();

    }

}