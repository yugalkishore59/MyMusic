package com.fetchhere.mymusic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class view_page_adapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentList= new ArrayList<>();
    private ArrayList<String> fragmenTitletList= new ArrayList<>();

    public view_page_adapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
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
