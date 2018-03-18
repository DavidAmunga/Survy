package com.buttercell.survy.student;


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
import com.buttercell.survy.lecturer.SetSurvey;
import com.buttercell.survy.model.Student;
import com.buttercell.survy.model.Survey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentSurveyFragment extends Fragment {
    private static final String TAG = "StudentSurveyFragment";


    Unbinder unbinder;
    @BindView(R.id.surveyList)
    RecyclerView surveyList;

    List<Survey> listSurvey = new ArrayList<>();
    List<Survey> newListSurvey = new ArrayList<>();


    List<String> listDoneSurvey = new ArrayList<>();
    Survey survey;
    SurveyAdapter adapter;

    CollectionReference firestore;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @BindView(R.id.newSurveyList)
    RecyclerView newSurveyList;
    @BindView(R.id.lineNewSurvey)
    LinearLayout lineNewSurvey;
    @BindView(R.id.completedSurveys)
    LinearLayout completedSurveys;

    AHBottomNavigation bottomNavigation;

    Student student;

    private boolean notificationVisible = false;
    private List<Survey> mySurveys = new ArrayList<>();

    public StudentSurveyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_survey, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onViewCreated(view, savedInstanceState);

        Paper.init(getContext());

        bottomNavigation = getActivity().findViewById(R.id.bottom_navigation);

        Log.d(TAG, "onViewCreated: " + userId);

        student = Paper.book().read("currentStudent");
        getNormalSurveys();


    }

    private void getNormalSurveys() {
        listSurvey.clear();
        listDoneSurvey.clear();
        newListSurvey.clear();
        mySurveys.clear();


        surveyList.setHasFixedSize(true);
        surveyList.setLayoutManager(new LinearLayoutManager(getContext()));

        newSurveyList.setHasFixedSize(true);
        newSurveyList.setLayoutManager(new LinearLayoutManager(getContext()));


        Log.d(TAG, "getNormalSurveys: Start");
        firestore = FirebaseFirestore.getInstance().collection("Students").document(userId).collection("Surveys");


        firestore.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                Log.d(TAG, "onEvent: " + documentSnapshots.size());
                if (documentSnapshots.size() == 0) {
                    Log.d(TAG, "onEvent: Survey: New");
                    setNewSurveys();
                } else {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        Log.d(TAG, "Survey: Exist");
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String key = doc.getDocument().getId();

                            listDoneSurvey.add(key);

                            Log.d(TAG, "Done Survey: " + listDoneSurvey.size());

                            setSurveys();

                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.nav_normal_surveys:
                getNormalSurveys();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_surveys, menu);
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


    private void setNewSurveys() {

        surveyList.setVisibility(View.GONE);
        completedSurveys.setVisibility(View.GONE);

        String year = student.getCurrentYear();

        Log.d(TAG, "setSurveys: " + student);
        switch (year) {
            case "1":
                year = "One";
                break;
            case "2":
                year = "Two";
                break;
            case "3":
                year = "Three";
                break;
            case "4":
                year = "Four";
                break;
        }


        Query ref = FirebaseDatabase.getInstance().getReference("Surveys").orderByChild("year").equalTo(year);
        ;
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    survey = postSnapshot.getValue(Survey.class);

                    newListSurvey.add(survey);
                    int surveyNo = newListSurvey.size();
                    if (surveyNo > 0) {
                        createNotification(String.valueOf(surveyNo));
                    }


                    NewSurveyAdapter adapter = new NewSurveyAdapter(newListSurvey, getContext());

                    newSurveyList.setAdapter(adapter);

                    if (newListSurvey.size() == 0) {
                        newSurveyList.setVisibility(View.GONE);
                        lineNewSurvey.setVisibility(View.GONE);
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setSurveys() {
        surveyList.setVisibility(View.VISIBLE);
        completedSurveys.setVisibility(View.VISIBLE);


        String year = student.getCurrentYear();

        Log.d(TAG, "setSurveys: " + student);
        switch (year) {
            case "1":
                year = "One";
                break;
            case "2":
                year = "Two";
                break;
            case "3":
                year = "Three";
                break;
            case "4":
                year = "Four";
                break;
        }


        Query ref = FirebaseDatabase.getInstance().getReference("Surveys");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (listDoneSurvey.size() > 0) {
                        if (listDoneSurvey.contains(postSnapshot.getKey())) {
                            survey = postSnapshot.getValue(Survey.class);

                            Log.d(TAG, "onDataChange: Done Survey" + listSurvey);

                            if (listSurvey.size() < listDoneSurvey.size()) {
                                listSurvey.add(survey);

                            }

                            int surveyNo = listSurvey.size();

                            if (surveyNo > 0) {
                                createNotification(String.valueOf(surveyNo));
                            }

                            SurveyAdapter adapter = new SurveyAdapter(listSurvey, getContext());

                            surveyList.setAdapter(adapter);

                            if (listSurvey.size() == 0) {
                                surveyList.setVisibility(View.GONE);
                                completedSurveys.setVisibility(View.GONE);
                            }
                        } else {
                            Log.d(TAG, "onDataChange: New");
                            survey = postSnapshot.getValue(Survey.class);

                            int size = (int) dataSnapshot.getChildrenCount() - listDoneSurvey.size();

                            if (newListSurvey.size() < size) {
                                newListSurvey.add(survey);
                            }

                            int surveyNo = newListSurvey.size();

                            if (surveyNo > 0) {
                                createNotification(String.valueOf(surveyNo));
                            }


                            NewSurveyAdapter adapter = new NewSurveyAdapter(newListSurvey, getContext());

                            newSurveyList.setAdapter(adapter);

                            if (newListSurvey.size() == 0) {
                                newSurveyList.setVisibility(View.GONE);
                                lineNewSurvey.setVisibility(View.GONE);
                            }
                        }


                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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


}
