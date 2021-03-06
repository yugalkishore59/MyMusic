package com.fetchhere.mymusic.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.fetchhere.mymusic.MainActivity;
import com.fetchhere.mymusic.R;
import com.fetchhere.mymusic.RecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class all_songs_fragment extends Fragment {
    private  RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    Context thisContext;

    private TextView no_songs_text;
    public ArrayList<File> allSongs,queue;

    SharedPreferences sharedPreferencesVariables;
    SharedPreferences.Editor editor;
    public static final String SHARED_PREF_KEY = "shared Preferences Variables";
    public static final String SORT_KEY = "current sort";
    public static final String CURR_SONG_KEY = "current Song Index";

    Spinner sortingSpinner;
    String[] sorting_methods={"Default","A-Z","Z-A"};
    int currentSort;

    Button BtnPlayAll, BtnShuffle;
    TextView totalSongs;

    public all_songs_fragment(ArrayList<File> allSongsList,ArrayList<File> queueList ){
        allSongs = allSongsList;
        queue=queueList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisContext=this.getActivity(); //getting context
        return inflater.inflate(R.layout.fragment_all_songs_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferencesVariables =thisContext.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferencesVariables.edit();
        currentSort= sharedPreferencesVariables.getInt(SORT_KEY,0); //getting sorting order from shared preferences

        recyclerView=(RecyclerView) view.findViewById(R.id.recycler_View);
        no_songs_text=(TextView) view.findViewById(R.id.no_songs_textView);
        BtnPlayAll=(Button) view.findViewById(R.id.play_all_button);
        BtnShuffle=(Button) view.findViewById(R.id.shuffle_all_button);
        totalSongs=(TextView) view.findViewById(R.id.txt_total_songs);

        if(allSongs.size()==0){
            no_songs_text.setVisibility(View.VISIBLE);
            /*BUG: suppose there are no songs which means no recycler view is created to list any song. Because it is created in else condition.
            After that user added some songs and rescanned directory. Then this fragment will get the list but there will be no recycler view to list songs.
            So there should always be a recycler view OR this whole fragment needs to be refreshed.*/
        }
        else {
            no_songs_text.setVisibility(View.GONE);
            totalSongs.setText(allSongs.size()+" songs");
            recyclerView.setLayoutManager(new LinearLayoutManager(thisContext));

            recyclerViewAdapter = new RecyclerViewAdapter(thisContext, queue); //giving queue to adapter
            recyclerView.setAdapter(recyclerViewAdapter); //setting adapter ie. listing songs from queue

            sortingSpinner =(Spinner) view.findViewById(R.id.spinner_sort);
            //Creating the ArrayAdapter instance having the sorting method list
            ArrayAdapter spinnerArrarAdapter = new ArrayAdapter(thisContext,android.R.layout.simple_spinner_item,sorting_methods);
            spinnerArrarAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            sortingSpinner.setAdapter(spinnerArrarAdapter);

            sortingSpinner.setSelection(currentSort); //setting sorting order (from shared preferences)
            sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
                {
                    switch (position){
                        case 0: // Default sort ie. folder(A-Z) wise
                            queue=(ArrayList<File>) allSongs.clone(); //getting queue from allSongs(ie. from backup)
                            recyclerViewAdapter = new RecyclerViewAdapter(thisContext, queue);
                            recyclerView.setAdapter(recyclerViewAdapter);
                            currentSort=0;
                            break;
                        case 1: // A-Z sort
                            Collections.sort(queue, new Comparator<File>() {
                                @Override
                                public int compare(File file, File t1) {
                                    return file.getName().compareToIgnoreCase(t1.getName());
                                }
                            });
                            recyclerViewAdapter.notifyDataSetChanged(); //notifying change in list
                            currentSort=1;
                            break;
                        case 2: // Z-A sort
                            Collections.sort(queue, new Comparator<File>() {
                                @Override
                                public int compare(File file, File t1) {
                                    return -1*file.getName().compareToIgnoreCase(t1.getName()); //-ve sort makes Z-A
                                }
                            });
                            recyclerViewAdapter.notifyDataSetChanged(); //notifying change in list
                            currentSort=2;
                            break;
                        default:
                            break;
                    }
                    //updating queue and sorting order in shared preferences
                    ((MainActivity)getActivity()).writeArrayListInPref(thisContext.getApplicationContext(),queue,((MainActivity)getActivity()).QUEUE_KEY);
                    editor.putInt(SORT_KEY, currentSort);
                    editor.commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    //do nothing
                }
            });

            //this button is to play first song
            BtnPlayAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*as now_playing_fragment automatically refresh its data on resuming so
                    we need to change the data and resume now_playing_fragment to see changes*/
                    ViewPager viewPager = (ViewPager) ((Activity)thisContext).findViewById(R.id.view_pager);
                    editor.putInt(CURR_SONG_KEY, 0); //index of 1st song
                    editor.commit();
                    viewPager.setCurrentItem(1); //resuming now_playing_fragment

                    /*BUG: if song with index "n" was/is playing last time and user changed sorting order or shuffled the list,
                    and then user tap on song with index "n" (which is a different song this time)
                    then app will keep on playing the older song. user has to tap on any other song
                    to see the effect in now_playing_fragment.*/
                }
            });

            //this button will shuffle the list
            BtnShuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Collections.shuffle(queue);
                    recyclerViewAdapter.notifyDataSetChanged();
                    ((MainActivity)getActivity()).writeArrayListInPref(thisContext.getApplicationContext(),queue,((MainActivity)getActivity()).QUEUE_KEY);
                }
            });
        }
    }
}