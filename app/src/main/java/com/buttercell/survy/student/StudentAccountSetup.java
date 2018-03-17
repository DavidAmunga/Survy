package com.buttercell.survy.student;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.buttercell.survy.R;
import com.buttercell.survy.helpers.NonSwipeableViewPager;
import com.rakshakhegde.stepperindicator.StepperIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentAccountSetup extends AppCompatActivity {



    StudentSectionPagerAdapter studentSectionPagerAdapter;
    @BindView(R.id.stepperIndicator)
    StepperIndicator indicator;
    @BindView(R.id.viewPager)
    NonSwipeableViewPager viewPager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);
        ButterKnife.bind(this);





        studentSectionPagerAdapter = new StudentSectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(studentSectionPagerAdapter);
        indicator.setViewPager(viewPager);
// or keep last page as "end page"
        indicator.setViewPager(viewPager, viewPager.getAdapter().getCount() - 1); //
// or manual change



    }

    @Override
    public void onBackPressed() {

        int fragmentNo = viewPager.getCurrentItem();

        setFragment(fragmentNo);
    }

    private void setFragment(int fragmentNo) {
        viewPager.setCurrentItem(fragmentNo - 1);
    }
}
