package com.buttercell.survy.student;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amush on 09-Feb-18.
 */

public class StudentSectionPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragmentList=new ArrayList<Fragment>();


    public StudentSectionPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentList.add(new StudentSetupProfile());
        fragmentList.add(new StudentUnits());

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
