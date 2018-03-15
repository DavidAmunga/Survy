package com.buttercell.survy.lecturer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.buttercell.survy.R;
import com.buttercell.survy.helpers.NonSwipeableViewPager;
import com.eftimoff.viewpagertransformers.StackTransformer;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;


public class LecturerHome extends AppCompatActivity {


    @BindView(R.id.bottom_navigation)
    AHBottomNavigation bottomNavigation;
    @BindView(R.id.homePager)
    NonSwipeableViewPager homePager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private boolean notificationVisible = false;

    LecturerAdapter lecturerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_home);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Paper.init(this);


        if (Paper.book().read("currentLecturer") == null) {
            startActivity(new Intent(getApplicationContext(), LecturerLogin.class));
            finish();

        }



        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Surveys", R.drawable.ic_survey);
//        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Notifications", R.drawable.ic_notifications);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Profile", R.drawable.ic_person);


        // Add items

        bottomNavigation.addItem(item2);
//        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);


        // Set background color
        bottomNavigation.setDefaultBackgroundColor(fetchColor(R.color.colorPrimary));
        bottomNavigation.setAccentColor(fetchColor(R.color.colorAccent));
        bottomNavigation.setInactiveColor(fetchColor(R.color.colorAccentDark));



        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);








//        Remove Notification
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

// remove notification badge
                int surveysItemPos = bottomNavigation.getItemsCount() - 2;

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


        lecturerAdapter = new LecturerAdapter(getSupportFragmentManager());


        homePager.setAdapter(lecturerAdapter);
        homePager.setPageTransformer(true, new StackTransformer());


    }



    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }




}
