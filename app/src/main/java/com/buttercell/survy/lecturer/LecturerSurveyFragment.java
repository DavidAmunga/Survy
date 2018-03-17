package com.buttercell.survy.lecturer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.buttercell.survy.R;
import com.buttercell.survy.adapter.NewSurveyAdapter;
import com.buttercell.survy.adapter.SurveyAdapter;
import com.buttercell.survy.model.Survey;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class LecturerSurveyFragment extends Fragment {
    private static final String TAG = "LecturerSurveyFragment";

    @BindView(R.id.fab)
    FloatingActionButton fab;
    Unbinder unbinder;


    List<Survey> listSurvey = new ArrayList<>();
    List<Survey> newListSurvey = new ArrayList<>();


    List<String> listDoneSurvey = new ArrayList<>();
    Survey survey;
    SurveyAdapter adapter;

    CollectionReference firestore;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    AHBottomNavigation bottomNavigation;
    @BindView(R.id.mySurveys)
    LinearLayout lineMySurveys;
    @BindView(R.id.mySurveyList)
    RecyclerView mySurveyList;


    private boolean notificationVisible = false;
    private List<Survey> mySurveys = new ArrayList<>();

    public LecturerSurveyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lecturer_survey, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        bottomNavigation = getActivity().findViewById(R.id.bottom_navigation);

        Log.d(TAG, "onViewCreated: " + userId);


        mySurveyList.setLayoutManager(new LinearLayoutManager(getContext()));
        mySurveyList.setHasFixedSize(true);


        getMySurveys();


    }


    private void getMySurveys() {
        Log.d(TAG, "getMySurveys: Start");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Surveys");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    if (postSnapshot.child("userName").getValue().equals(name)) {
                        Log.d(TAG, "onDataChange: Mine");
                        survey = postSnapshot.getValue(Survey.class);

                        mySurveys.add(survey);

                        SurveyAdapter adapter = new SurveyAdapter(mySurveys, getContext());


                        mySurveyList.setAdapter(adapter);


                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            if (getActivity() != null) {
                getActivity().setTitle("Surveys");
            }
        }
    }


    private void createNotification(final String surveyNo) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AHNotification notification = new AHNotification.Builder()
                        .setText(surveyNo)
                        .setBackgroundColor(Color.parseColor("#FF3E4D"))
                        .setTextColor(Color.WHITE)
                        .build();
                // Adding notification to last item.
                bottomNavigation.setNotification(notification, bottomNavigation.getItemsCount() - 2);
                notificationVisible = true;
            }
        }, 100);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {

        startActivity(new Intent(getActivity(), SetSurvey.class));
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
