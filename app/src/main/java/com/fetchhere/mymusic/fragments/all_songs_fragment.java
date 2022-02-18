package com.fetchhere.mymusic.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fetchhere.mymusic.R;

public class all_songs_fragment extends Fragment {
    private  RecyclerView recyclerView;
    private TextView no_songs_text;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_songs_fragment, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=(RecyclerView) view.findViewById(R.id.recycler_View);
        no_songs_text=(TextView) view.findViewById(R.id.no_songs_textView);
    }
}