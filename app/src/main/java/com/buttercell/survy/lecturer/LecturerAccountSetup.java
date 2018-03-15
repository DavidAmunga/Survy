package com.buttercell.survy.lecturer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.buttercell.survy.R;
import com.buttercell.survy.helpers.NonSwipeableViewPager;
import com.rakshakhegde.stepperindicator.StepperIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LecturerAccountSetup extends AppCompatActivity {


    LecturerSectionPagerAdapter lecturerSectionPagerAdapter;
    @BindView(R.id.viewPager)
    NonSwipeableViewPager viewPager;

    private StepperIndicator stepperIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_account_setup);
        ButterKnife.bind(this);


        stepperIndicator = findViewById(R.id.stepperIndicator);

        lecturerSectionPagerAdapter = new LecturerSectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(lecturerSectionPagerAdapter);
        stepperIndicator.setViewPager(viewPager);
// or keep last page as "end page"
        stepperIndicator.setViewPager(viewPager, viewPager.getAdapter().getCount() - 1); //
// or manual change


    }


}
