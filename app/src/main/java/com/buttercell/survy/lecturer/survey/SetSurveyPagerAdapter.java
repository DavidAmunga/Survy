package com.buttercell.survy.lecturer.survey;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amush on 09-Feb-18.
 */

public class SetSurveyPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragmentList=new ArrayList<Fragment>();


    public SetSurveyPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentList.add(new SurveyDetailOne());
        fragmentList.add(new SurveyDetailTwo());
        fragmentList.add(new SurveyQuestions());
        fragmentList.add(new SurveyPreview());

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
