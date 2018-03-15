package com.buttercell.survy.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.buttercell.survy.R;
import com.buttercell.survy.helpers.NonSwipeableViewPager;
import com.eftimoff.viewpagertransformers.StackTransformer;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class StudentHome extends AppCompatActivity {
    private static final String TAG = "StudentHome";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.homePager)
    NonSwipeableViewPager homePager;
    @BindView(R.id.bottom_navigation)
    AHBottomNavigation bottomNavigation;
    private boolean notificationVisible = false;

    StudentAdapter studentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);
        setContentView(R.layout.activity_student_home);

        setSupportActionBar(toolbar);
        ButterKnife.bind(this);


        studentAdapter = new StudentAdapter(getSupportFragmentManager());


        homePager.setAdapter(studentAdapter);

        if (Paper.book().read("currentStudent") == null) {
            startActivity(new Intent(getApplicationContext(), StudentLogin.class));
            finish();
        }



        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Surveys", R.drawable.ic_survey);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Profile", R.drawable.ic_person);


        // Add items

        bottomNavigation.addItem(item2);
//        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);



        // Set background color
        bottomNavigation.setDefaultBackgroundColor(fetchColor(R.color.colorPrimary));
        bottomNavigation.setAccentColor(fetchColor(R.color.colorAccent));
        bottomNavigation.setInactiveColor(fetchColor(R.color.colorPrimaryDark));



        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);



        //        Remove Notification
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

// remove notification badge
                int surveysItemPos = bottomNavigation.getItemsCount() - 2;

                Log.d(TAG, "onTabSelected: Yes");


                if (position == surveysItemPos) {
                    bottomNavigation.setNotification(new AHNotification(), surveysItemPos);
                    homePager.setCurrentItem(position);
                    return true;
                }
                if (!wasSelected) {
                    homePager.setCurrentItem(position);
                    return true;
                }
                return true;
            }
        });

        // Set current item programmatically
        bottomNavigation.setCurrentItem(0);



        homePager.setPageTransformer(true, new StackTransformer());



    }


    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }


}
