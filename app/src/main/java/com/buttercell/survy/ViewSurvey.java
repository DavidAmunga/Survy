package com.buttercell.survy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.buttercell.survy.model.Lecturer;
import com.buttercell.survy.model.Question;
import com.buttercell.survy.model.Survey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class ViewSurvey extends AppCompatActivity {

    private static final String TAG = "ViewSurvey";
    Survey survey;
    @BindView(R.id.txtSurveyName)
    TextView txtSurveyName;
    @BindView(R.id.txtUser)
    TextView txtUser;
    @BindView(R.id.txtDescription)
    TextView txtDescription;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnSurveyDetails)
    Button btnSurveyDetails;
    @BindView(R.id.survey_image)
    CircleImageView surveyImage;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;

    private List<String> questionKeys = new ArrayList<>();
    private List<Question> singleQuestion = new ArrayList<>();
    private List<Question> multiQuestion = new ArrayList<>();
    private List<Question> openQuestion = new ArrayList<>();
    private List<Question> likertQuestion = new ArrayList<>();

    private CollectionReference firestore;

    Lecturer lecturer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_survey);
        ButterKnife.bind(this);

        Paper.init(this);

        firestore = FirebaseFirestore.getInstance().collection("Surveys");


        if (getIntent().getExtras() != null) {

            if (getIntent().getStringExtra("new") == null) {
                btnStart.setVisibility(View.GONE);

            }


            final String key = getIntent().getExtras().getString("key");


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Surveys").child(key);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    survey = dataSnapshot.getValue(Survey.class);

                    String lecturerId = survey.getUserId();
                    FirebaseFirestore.getInstance().collection("Lecturers").document(lecturerId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                            String lecImage = documentSnapshot.get("image").toString();

                            Glide.with(getApplicationContext()).load(survey.getImage()).into(surveyImage);
                            Glide.with(getApplicationContext()).load(lecImage).into(profileImage);

                            txtDescription.setText(survey.getDesc());
                            txtSurveyName.setText(survey.getName());
                            txtUser.setText(survey.getUserName());


                        }
                    });


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference refQuestions = FirebaseDatabase.getInstance().getReference("Surveys").child(key).child("questionOrder");
            refQuestions.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String question = postSnapshot.getValue().toString();

                        questionKeys.add(question);


                    }
                    getSingleQuestions(questionKeys, key);
                    getMultiQuestions(questionKeys, key);
                    getOpenQuestions(questionKeys, key);
                    getLikertQuestions(questionKeys, key);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });


            btnSurveyDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ViewSurvey.this, SurveyDetails.class);
                    intent.putExtra("key", key);
                    intent.putExtra("questionKeys", (Serializable) questionKeys);
                    intent.putExtra("singleQuestions", (Serializable) singleQuestion);
                    intent.putExtra("multiQuestions", (Serializable) multiQuestion);
                    intent.putExtra("openQuestions", (Serializable) openQuestion);
                    intent.putExtra("likertQuestions", (Serializable) likertQuestion);

                    startActivity(intent);
                }
            });


            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (questionKeys.size() > 0) {

                        Intent intent = new Intent(ViewSurvey.this, DoSurvey.class);
                        intent.putExtra("Key", key);
                        intent.putExtra("singleQuestions", (Serializable) singleQuestion);
                        intent.putExtra("multiQuestions", (Serializable) multiQuestion);
                        intent.putExtra("openQuestions", (Serializable) openQuestion);
                        intent.putExtra("likertQuestions", (Serializable) likertQuestion);
                        intent.putExtra("questionKeys", (Serializable) questionKeys);

                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(ViewSurvey.this, "Please turn on your internet!", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }


    }

    private void getLikertQuestions(List<String> questionKeys, String key) {
        for (int i = 0; i < questionKeys.size(); i++) {
            firestore.document(key).collection("LikertChoice").document(questionKeys.get(i)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {

                        Question question = documentSnapshot.toObject(Question.class);

                        likertQuestion.add(question);
                        Log.d(TAG, "onEvent: LikertChoice " + likertQuestion.size());

                    }


                }
            });


        }
    }

    private void getOpenQuestions(List<String> questionKeys, String key) {
        for (int i = 0; i < questionKeys.size(); i++) {
            firestore.document(key).collection("OpenChoice").document(questionKeys.get(i)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {

                        Question question = documentSnapshot.toObject(Question.class);

                        openQuestion.add(question);
                        Log.d(TAG, "onEvent: OpenChoice " + openQuestion.size());

                    }


                }
            });


        }
    }

    private void getMultiQuestions(List<String> questionKeys, String key) {
        for (int i = 0; i < questionKeys.size(); i++) {
            firestore.document(key).collection("MultiChoice").document(questionKeys.get(i)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {

                        Question question = documentSnapshot.toObject(Question.class);

                        multiQuestion.add(question);
                        Log.d(TAG, "onEvent: MultiChoice " + multiQuestion.size());

                    }


                }
            });


        }

    }

    private void getSingleQuestions(final List<String> questionKeys, final String key) {
        for (int i = 0; i < questionKeys.size(); i++) {
            firestore.document(key).collection("SingleChoice").document(questionKeys.get(i)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {

                        Question question = documentSnapshot.toObject(Question.class);

                        singleQuestion.add(question);
                        Log.d(TAG, "onEvent: SingleChoice " + singleQuestion.size());

                    }


                }
            });


        }

    }
}
