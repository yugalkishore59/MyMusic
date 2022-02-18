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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.fetchhere.mymusic.fragments.all_songs_fragment;
import com.fetchhere.mymusic.fragments.favourite_fragment;
import com.fetchhere.mymusic.fragments.now_playing_fragment;
import com.google.android.material.tabs.TabLayout;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private TabLayout tab_layout;
    private ViewPager view_pager;

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

    void continueActivity() {

        view_page_adapter adapter = new view_page_adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragments(new all_songs_fragment(), "All songs");
        adapter.addFragments(new now_playing_fragment(), "Now Playing");
        adapter.addFragments(new favourite_fragment(), "Favourites");
        view_pager.setAdapter(adapter);

        scanMusic();
    }
    void scanMusic(){

    }
}