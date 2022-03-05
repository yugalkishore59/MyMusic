package com.fetchhere.mymusic.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

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

import com.fetchhere.mymusic.R;
import com.fetchhere.mymusic.RecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class all_songs_fragment extends Fragment {
    private  RecyclerView recyclerView;
    private TextView no_songs_text;
    private RecyclerViewAdapter recyclerViewAdapter;
    public ArrayList<File> AllSongsArrayList;
    ArrayList<File> AllSongsArrayListBackup;
    Context thisContext;
    SharedPreferences sharedPreferencesVariable;

    Spinner sorting;
    String[] sorting_methods={"Default","A-Z","Z-A"};

    Button BtnPlayAll, BtnShuffle;
    TextView totalSongs;

    public all_songs_fragment(ArrayList<File> SongsArrayList){
        AllSongsArrayList=AllSongsArrayListBackup=SongsArrayList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //thisContext=container.getContext();
        thisContext=this.getActivity();
        return inflater.inflate(R.layout.fragment_all_songs_fragment, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=(RecyclerView) view.findViewById(R.id.recycler_View);
        no_songs_text=(TextView) view.findViewById(R.id.no_songs_textView);
        BtnPlayAll=(Button) view.findViewById(R.id.play_all_button);
        BtnShuffle=(Button) view.findViewById(R.id.shuffle_all_button);
        totalSongs=(TextView) view.findViewById(R.id.txt_total_songs);

        if(AllSongsArrayList.size()==0){
            no_songs_text.setVisibility(View.VISIBLE);
        }
        else {
            no_songs_text.setVisibility(View.GONE);
            totalSongs.setText(String.valueOf(AllSongsArrayList.size())+" songs");
            recyclerView.setLayoutManager(new LinearLayoutManager(thisContext));
            recyclerViewAdapter = new RecyclerViewAdapter(thisContext, AllSongsArrayList);
            recyclerView.setAdapter(recyclerViewAdapter);

            sorting=(Spinner) view.findViewById(R.id.spinner_sort);

            //recyclerView.setHasFixedSize(true);

            //Creating the ArrayAdapter instance having the sorting method list
            ArrayAdapter spinnerArrarAdapter = new ArrayAdapter(thisContext,android.R.layout.simple_spinner_item,sorting_methods);
            spinnerArrarAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            sorting.setAdapter(spinnerArrarAdapter);
            sorting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
                {
                    switch (position){
                        case 0:
                            AllSongsArrayList=(ArrayList<File>) AllSongsArrayListBackup.clone();
                            recyclerViewAdapter = new RecyclerViewAdapter(thisContext, AllSongsArrayList);
                            recyclerView.setAdapter(recyclerViewAdapter);
                            break;
                        case 1:
                            Collections.sort(AllSongsArrayList, new Comparator<File>() {
                                @Override
                                public int compare(File file, File t1) {
                                    return file.getName().compareToIgnoreCase(t1.getName());
                                }
                            });

                            recyclerViewAdapter.notifyDataSetChanged();
                            break;
                        case 2:
                            Collections.sort(AllSongsArrayList, new Comparator<File>() {
                                @Override
                                public int compare(File file, File t1) {
                                    return -1*file.getName().compareToIgnoreCase(t1.getName());
                                }
                            });
                            recyclerViewAdapter.notifyDataSetChanged();
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
            //return rootView;
            BtnPlayAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewPager viewPager = (ViewPager) ((Activity)thisContext).findViewById(R.id.view_pager);
                    sharedPreferencesVariable=thisContext.getSharedPreferences("shared Preferences Variables", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferencesVariable.edit();
                    editor.putInt("currentSongIndex", 0);
                    editor.commit();
                    viewPager.setCurrentItem(1);
                }
            });

            BtnShuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Collections.shuffle(AllSongsArrayList);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            });

        }
    }

}