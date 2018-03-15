package com.buttercell.survy.lecturer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.buttercell.survy.helpers.SmartFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amush on 15-Feb-18.
 */

public class LecturerAdapter extends SmartFragmentStatePagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();

    public LecturerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        fragmentList.add(new LecturerSurveyFragment());
        fragmentList.add(new LecturerProfileFragment());

    }

    // Our custom method that populates this Adapter with Fragments
    public void addFragments(Fragment fragment) {
        fragmentList.add(fragment);
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
