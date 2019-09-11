package com.rd.dmmr.tutosearch;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> listnombrefragment = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }




    @Override
    public Fragment getItem(int position) {

        return fragmentList.get(position);
    }

    @Override
    public int getCount() {

        return listnombrefragment.size();
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {


        return listnombrefragment.get(position);
    }


    public void AddFragment(Fragment fragment, String titulo){
        fragmentList.add(fragment);
        listnombrefragment.add(titulo);


    }

}
