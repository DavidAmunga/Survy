package com.buttercell.survy.lecturer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amush on 09-Feb-18.
 */

public class LecturerSectionPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragmentList=new ArrayList<Fragment>();


    public LecturerSectionPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentList.add(new LecturerProfile());

    }




    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
