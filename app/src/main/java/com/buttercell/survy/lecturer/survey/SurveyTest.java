package com.buttercell.survy.lecturer.survey;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.buttercell.survy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SurveyTest extends AppCompatActivity {
    private static final String TAG = "SurveyTest";
    @BindView(R.id.txt)
    TextView txt;

    List<String> questionKeys = new ArrayList<>();
    DocumentReference firestore;

    List<String> questionsList = new ArrayList<>();

    List<Map> singleChoicesList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_test);
        ButterKnife.bind(this);


        firestore = FirebaseFirestore.getInstance().collection("Surveys")
                .document("82325d0d-a3dc-4a4d-aaaa-fb68da5dc6cd");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Surveys").child("82325d0d-a3dc-4a4d-aaaa-fb68da5dc6cd").child("questionOrder");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int childNo = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    questionKeys.add(postSnapshot.getValue().toString());


                }


                for (int i = 0; i < questionKeys.size(); i++)
                {
                    getSingleChoiceQuestions(i);
                    Log.d(TAG, "Single: "+singleChoicesList.toString());


//                    Log.d(TAG, "onDataChange: " + questionKeys.get(i));
//                    final int finalI = i;
//                    firestore.collection("SingleChoice").document(questionKeys.get(i)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                        @Override
//                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
//
//                            if (documentSnapshot.exists()) {
//                                String question = documentSnapshot.getString("question");
//                                questionsList.add(question);
//                                Log.d(TAG, "Single Choice: " + question);
//                            }
//                            else
//                            {
//                                Log.d(TAG, "onEvent: Failed");
//
//                                firestore.collection("MultiChoice").document(questionKeys.get(finalI)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
//
//                                        if (documentSnapshot.exists()) {
//                                            String question = documentSnapshot.getString("question");
//                                            questionsList.add(question);
//                                            Log.d(TAG, "Multi Choice: " + question);
//                                        } else
//                                            {
//                                            Log.d(TAG, "onEvent: Failed");
//                                            firestore.collection("OpenChoice").document(questionKeys.get(finalI)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                                                @Override
//                                                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
//
//                                                    if (documentSnapshot.exists()) {
//                                                        String question = documentSnapshot.getString("question");
//                                                        questionsList.add(question);
//                                                        Log.d(TAG, "Open Choice: " + question);
//                                                    } else {
//                                                        Log.d(TAG, "onEvent: Failed");
//
//
//                                                    }
//
//
//                                                }
//                                            });
//
//
//                                        }
//
//
//                                    }
//                                });
//
//                            }
//
//
//                        }
//                    });


                }
                txt.setText(questionsList.toString());

                // Log.d(TAG, "onDataChange: " + questionKeys.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public Map getSingleChoiceQuestions(int i)
    {
        final Map<String,Object> questionMap=new HashMap<>();

        Log.d(TAG, "onDataChange: " + questionKeys.get(i));
        final int finalI = i;
        firestore.collection("SingleChoice").document(questionKeys.get(i)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (documentSnapshot.exists()) {

                    String question = documentSnapshot.getString("question");
                    String choice1 = documentSnapshot.getString("choice1");
                    String choice2 = documentSnapshot.getString("choice2");
                    String choice3 = documentSnapshot.getString("choice3");
                    String choice4 = documentSnapshot.getString("choice4");


                    questionsList.add(question);
                 //   Log.d(TAG, "Single Choice: " + question);



                    questionMap.put("question",question);
                    questionMap.put("choice1",choice1);
                    questionMap.put("choice2",choice2);
                    questionMap.put("choice3",choice3);
                    questionMap.put("choice4",choice4);


                    singleChoicesList.add(questionMap);

                    Log.d(TAG, "ChoiceList: "+questionMap.toString());

                } else {
                    Log.d(TAG, "onEvent: Failed");

                }
            }
        });

        return questionMap;

    }

}
