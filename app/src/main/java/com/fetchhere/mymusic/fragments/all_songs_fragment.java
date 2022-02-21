package com.fetchhere.mymusic.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fetchhere.mymusic.R;
import com.fetchhere.mymusic.RecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;

public class all_songs_fragment extends Fragment {
    private  RecyclerView recyclerView;
    private TextView no_songs_text;
    private RecyclerViewAdapter recyclerViewAdapter;
    public ArrayList<File> AllSongsArrayList;
    private ArrayAdapter<String> arrayAdapter;
    Context thisContext;

    public all_songs_fragment(ArrayList<File> SongsArrayList){
        AllSongsArrayList=SongsArrayList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisContext=container.getContext();
        return inflater.inflate(R.layout.fragment_all_songs_fragment, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=(RecyclerView) view.findViewById(R.id.recycler_View);
        no_songs_text=(TextView) view.findViewById(R.id.no_songs_textView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(thisContext));

        recyclerViewAdapter = new RecyclerViewAdapter(thisContext, AllSongsArrayList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}