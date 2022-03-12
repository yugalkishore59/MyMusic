package com.fetchhere.mymusic;

import androidx.annotation.NonNull;
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

    /*allSongs is kind of backup of scanned songs list and queue is the main song list.
    most of the operations will be done on queue*/
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

        //setting tab layout with view pager
        tab_layout.setupWithViewPager(view_pager);

        sharedPreferencesVariables=this.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferencesVariables.edit();

        //checking permission for reading storage
        checkForPermission();

    }

    //function to check storage permission
    void checkForPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_DENIED) {
            requestPermission(); //request for permission if permission is not given
        } else
            continueActivity(); //continue if permission is already given

    }

    //function to request for read storage permission
    void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{READ_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        continueActivity(); //continue if permission granted
                    } else {
                        requestPermission(); //request again if denied
                    }
                }
            }
        }
    }

    //this function is called after the storage read permission is granted
    void continueActivity() {
        allSongs = readListFromPref(this,LIST_KEY); //reading allSongs from shared preferences (if scanned already)
        if (allSongs == null)
            scanMusic(); //scanning music if not scanned already
        queue= readListFromPref(this,QUEUE_KEY); //reading queue from shared preferences (if exist)
        if (queue == null) {
            //creating and storing queue to shared preferences if it doesn't exist
            queue = (ArrayList<File>) allSongs.clone();
            writeArrayListInPref(this,queue,QUEUE_KEY);
        }

        //creating fragments
        adapter = new view_page_adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragments(new all_songs_fragment(allSongs,queue), "All songs");
        adapter.addFragments(new now_playing_fragment(queue), "Playing");
        adapter.addFragments(new favourite_fragment(), "Favourites");
        view_pager.setAdapter(adapter);

    }

    //function to get all storage directories of device and list songs from them
    void scanMusic(){
        File[] fileDirectory = ContextCompat.getExternalFilesDirs(getApplicationContext(),null);
        //above line will return directories "/Android/data/<package name>" from all the external/internal storages of device

        for (int i = 0; i< fileDirectory.length; i++) {
            //getting root path of each directory
            String path = fileDirectory[i].getParent().replace("/Android/data/","").replace(getPackageName(),"");
            fileDirectory[i]= new File(path);
        }

        allSongs=findSong(fileDirectory[0]); //calling function to scan music from fileDirectory[0] ie. internal storage
        for(int i = 1; i< fileDirectory.length; i++)
            allSongs.addAll(findSong(fileDirectory[i])); //adding music from other external storages

        //updating shared preferences
        writeArrayListInPref(this,allSongs,LIST_KEY);
        editor.putInt(CURR_SONG_KEY, 0);
        editor.putInt(SORT_KEY, 0);
        editor.commit();

    }

    //function to search music(mp3 and wav only) from provided directory
    public ArrayList<File> findSong(File rootFolder) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = rootFolder.listFiles();
        Uri uri;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever(); //to filter songs by duration

        assert files != null;
        for (File singleFile : files) {

            //Adding the directory to arrayList if it is not hidden by Recursion
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                File noMedia=new File(singleFile.getAbsolutePath()+"/.nomedia");
                if(!noMedia.exists()) arrayList.addAll(findSong(singleFile)); //Recursion

            } else {
                //Adding the single music file to ArrayList
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                    uri = Uri.parse(singleFile.getAbsolutePath());
                    mmr.setDataSource(this,uri);
                    String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    int millSecond = Integer.parseInt(durationStr);
                    if(millSecond>60000)arrayList.add(singleFile); //1 minute duration filter
                }
            }
        }
        return arrayList;
    }

    //function to write ArrayList<File> object in shared preferences using gson
    public static void writeArrayListInPref(Context context, ArrayList<File> list, String key) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, jsonString);
        editor.apply();
    }

    //function to read ArrayList<File> object from shared preferences using gson
    public static ArrayList<File> readListFromPref(Context context,String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString(key, "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<File>>() {}.getType();
        return gson.fromJson(jsonString, type);
    }

}