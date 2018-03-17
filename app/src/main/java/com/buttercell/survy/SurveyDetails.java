package com.buttercell.survy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.buttercell.survy.model.Question;
import com.buttercell.survy.model.QuestionCategory;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;
import io.paperdb.Paper;

public class SurveyDetails extends AppCompatActivity {

    private static final String TAG = "SurveyDetails";

    CollectionReference firestore;

    List<String> questionKeys = new ArrayList<>();

    @BindView(R.id.expandableLayout)
    ExpandableLayout expandableLayout;


    String surveyKey = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_details);
        ButterKnife.bind(this);

        Paper.init(this);

        firestore = FirebaseFirestore.getInstance().collection("Surveys");

        if (getIntent().getExtras() != null) {
            surveyKey = getIntent().getStringExtra("key");
            questionKeys = (List<String>) getIntent().getSerializableExtra("questionKeys");
        }


        expandableLayout.setRenderer(new ExpandableLayout.Renderer<QuestionCategory, Question>() {
            @Override
            public void renderParent(View view, QuestionCategory questionCategory, boolean isExpanded, int i) {
                ((TextView) view.findViewById(R.id.tv_parent_name)).setText(questionCategory.name);
                view.findViewById(R.id.arrow).setBackgroundResource(isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
            }

            @Override
            public void renderChild(View view, final Question thisQuestion, int parentPosition, int childPosition) {
                ((TextView) view.findViewById(R.id.tv_child_name)).setText(thisQuestion.getQuestion());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < questionKeys.size(); i++) {
                            final int finalI = i;
                            final int finalI1 = i;
                            firestore.document(surveyKey).collection("SingleChoice").document(questionKeys.get(i)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                    if (documentSnapshot.exists()) {
                                        Log.d(TAG, "onEvent: SingleChoice");
                                        Question question = documentSnapshot.toObject(Question.class);
                                        goToQuestionDetails(thisQuestion, question, finalI, "SingleChoice");


                                    } else {
                                        firestore.document(surveyKey).collection("MultiChoice").document(questionKeys.get(finalI1)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                                if (documentSnapshot.exists()) {
                                                    Log.d(TAG, "onEvent: MultiChoice");
                                                    Question question = documentSnapshot.toObject(Question.class);
                                                    goToQuestionDetails(thisQuestion, question, finalI, "MultiChoice");


                                                } else {
                                                    firestore.document(surveyKey).collection("OpenChoice").document(questionKeys.get(finalI1)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                                            if (documentSnapshot.exists()) {
                                                                Log.d(TAG, "onEvent: OpenChoice");
                                                                Question question = documentSnapshot.toObject(Question.class);
                                                                goToQuestionDetails(thisQuestion, question, finalI, "OpenChoice");


                                                            }

                                                        }
                                                    });
                                                }

                                            }
                                        });

                                    }

                                }
                            });


                        }


                    }
                });

            }
        });


        if (getIntent().getExtras() != null) {

            List<Question> singleQuestion = (List<Question>) getIntent().getExtras().getSerializable("singleQuestions");
            List<Question> multiQuestion = (List<Question>) getIntent().getExtras().getSerializable("multiQuestions");
            List<Question> openQuestion = (List<Question>) getIntent().getExtras().getSerializable("openQuestions");

            if (singleQuestion.size() > 0) {
                expandableLayout.addSection(getSingleSection(singleQuestion));
            }
            if (multiQuestion.size() > 0) {
                expandableLayout.addSection(getMultiSection(multiQuestion));
            }

            if (openQuestion.size() > 0) {
                expandableLayout.addSection(getOpenSection(openQuestion));
            }

        }


    }

    private void goToQuestionDetails(Question thisQuestion, Question question, int finalI, String choice) {
        if (question.getQuestion().equals(thisQuestion.getQuestion())) {
            Log.d(TAG, "goToQuestionDetails: " + thisQuestion.getQuestion());
            String questionKey = questionKeys.get(finalI);

            Intent intent = new Intent(SurveyDetails.this, QuestionDetails.class);
            intent.putExtra("question", thisQuestion);
            intent.putExtra("questionKey", questionKey);
            intent.putExtra("surveyKey", surveyKey);
            intent.putExtra("choice", choice);
            startActivity(intent);

        }

    }

    private Section getOpenSection(List<Question> openChoiceList) {
        Section<QuestionCategory, Question> section = new Section<>();

        QuestionCategory questionCategory = new QuestionCategory("Open Questions");

        section.parent = questionCategory;
        section.children.addAll(openChoiceList);
        section.expanded = true;
        return section;
    }

    private Section getSingleSection(List<Question> singleChoiceList) {
        Section<QuestionCategory, Question> section = new Section<>();

        QuestionCategory questionCategory = new QuestionCategory("Single Questions");

        section.parent = questionCategory;
        section.children.addAll(singleChoiceList);
        section.expanded = true;
        return section;
    }

    private Section getMultiSection(List<Question> multiChoiceList) {
        Section<QuestionCategory, Question> section = new Section<>();

        QuestionCategory questionCategory = new QuestionCategory("Multi Questions");

        section.parent = questionCategory;
        section.children.addAll(multiChoiceList);
        section.expanded = true;
        return section;
    }
}
