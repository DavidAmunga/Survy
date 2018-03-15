package com.buttercell.survy.lecturer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.buttercell.survy.R;
import com.buttercell.survy.helpers.NonSwipeableViewPager;
import com.buttercell.survy.lecturer.survey.SetSurveyPagerAdapter;
import com.rakshakhegde.stepperindicator.StepperIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class SetSurvey extends AppCompatActivity {


    @BindView(R.id.stepperIndicator)
    StepperIndicator stepperIndicator;
    @BindView(R.id.viewPager)
    NonSwipeableViewPager viewPager;


    SetSurveyPagerAdapter setSurveyPagerAdapter;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_survey);
        ButterKnife.bind(this);

        Paper.init(this);

        stepperIndicator = findViewById(R.id.stepperIndicator);






        setSurveyPagerAdapter = new SetSurveyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(setSurveyPagerAdapter);
        stepperIndicator.setViewPager(viewPager);
// or keep last page as "end page"
        stepperIndicator.setViewPager(viewPager, viewPager.getAdapter().getCount() - 1); //

// or manual change


    }


    @Override
    public void onBackPressed() {

        int fragmentNo = viewPager.getCurrentItem();


        if(viewPager.getCurrentItem()==0)
        {
            startActivity(new Intent(SetSurvey.this,LecturerHome.class));
            finish();
        }
        else
        {

            setFragment(fragmentNo);
        }
    }

    private void setFragment(int fragmentNo) {
        viewPager.setCurrentItem(fragmentNo - 1);
    }
}
