package com.fetchhere.mymusic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.fetchhere.mymusic.fragments.now_playing_fragment;

import java.util.ArrayList;

public class view_page_adapter extends FragmentPagerAdapter {

    public ArrayList<Fragment> fragmentList= new ArrayList<>();
    private ArrayList<String> fragmenTitletList= new ArrayList<>();
    FragmentManager fragMan;

    public view_page_adapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        fragMan=fm;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    void addFragments(Fragment fragment, String title){
        fragmentList.add(fragment);
        fragmenTitletList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmenTitletList.get(position);
    }


}
