package com.buttercell.survy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buttercell.survy.model.Question;
import com.buttercell.survy.model.Student;
import com.buttercell.survy.student.StudentHome;
import com.dualcores.swagpoints.SwagPoints;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.feeeei.circleseekbar.CircleSeekBar;
import io.paperdb.Paper;

public class DoSurvey extends AppCompatActivity {

    private static final String TAG = "DoSurvey";

    CollectionReference firestore;


    //Initialize List Questions
    List<Question> singleQuestionList = new ArrayList<>();
    List<Question> multiQuestionList = new ArrayList<>();
    List<Question> openQuestionList = new ArrayList<>();
    List<Question> likertQuestionList = new ArrayList<>();

    List<String> singleKeys = new ArrayList<>();
    List<String> multiKeys = new ArrayList<>();
    List<String> openKeys = new ArrayList<>();
    List<String> likertKeys = new ArrayList<>();


    @BindView(R.id.btnReady)
    Button btnReady;
    @BindView(R.id.questionDetails)
    LinearLayout questionDetails;

    View questionView;
    @BindView(R.id.btnNext)
    Button btnNext;

    int progress;


    int singleI = 0;
    int multiI = 0;
    int openI = 0;
    int likertI = 0;
    String key;

    String userId;

    Map<String, Object> answerNo = new HashMap<>();

    Student student;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_survey);
        ButterKnife.bind(this);

        Paper.init(this);

        student = Paper.book().read("currentStudent");


        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore = FirebaseFirestore.getInstance().collection("Surveys");

        if (getIntent().getExtras() != null) {
            key = getIntent().getExtras().getString("Key");
            final List<String> questionKeys = (List<String>) getIntent().getExtras().getSerializable("questionKeys");
            List<Question> singleQuestion = (List<Question>) getIntent().getExtras().getSerializable("singleQuestions");
            List<Question> multiQuestion = (List<Question>) getIntent().getExtras().getSerializable("multiQuestions");
            List<Question> openQuestion = (List<Question>) getIntent().getExtras().getSerializable("openQuestions");
            List<Question> likertQuestion = (List<Question>) getIntent().getExtras().getSerializable("likertQuestions");


            progress = singleQuestion.size() + multiQuestion.size() + openQuestion.size() + likertQuestion.size();

            for (int i = 0; i < questionKeys.size(); i++) {
                final int finalI = i;
                final int finalI1 = i;
                final int finalI2 = i;
                firestore.document(key).collection("SingleChoice").document(questionKeys.get(i)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "onEvent: SingleChoice");
                            Question question = documentSnapshot.toObject(Question.class);

                            String id = questionKeys.get(finalI1);
                            Log.d(TAG, "Id" + id);
                            singleQuestionList.add(question);
                            singleKeys.add(id);


                        } else {
                            firestore.document(key).collection("LikertChoice").document(questionKeys.get(finalI2)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                    if (documentSnapshot.exists()) {
                                        Log.d(TAG, "onEvent: LikertChoice");
                                        Question question = documentSnapshot.toObject(Question.class);

                                        String id = questionKeys.get(finalI1);
                                        Log.d(TAG, "Id" + id);
                                        likertQuestionList.add(question);
                                        likertKeys.add(id);


                                    } else {
                                        firestore.document(key).collection("MultiChoice").document(questionKeys.get(finalI))
                                                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                                        if (documentSnapshot.exists()) {
                                                            Log.d(TAG, "onEvent: MultiChoice");
                                                            Question question = documentSnapshot.toObject(Question.class);
                                                            multiQuestionList.add(question);

                                                            String id = questionKeys.get(finalI);
                                                            multiKeys.add(id);


                                                        } else {

                                                            firestore.document(key).collection("OpenChoice").document(questionKeys.get(finalI)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                                                    if (documentSnapshot.exists()) {
                                                                        Question question = documentSnapshot.toObject(Question.class);
                                                                        openQuestionList.add(question);
                                                                        Log.d(TAG, "onEvent: OpenChoice");

                                                                        String id = questionKeys.get(finalI);
                                                                        openKeys.add(id);
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
        }


        btnReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnReady.setVisibility(View.GONE);
                if (singleQuestionList.size() > 0) {
                    answerSingleQuestions();

                }
                else if(singleQuestionList.size()==0 && likertQuestionList.size()>0)
                {
                    answerLikertQuestions();
                }
                else if(singleQuestionList.size()==0 && likertQuestionList.size()==0)
                {
                    answerMultiQuestions();
                }
                else if(singleQuestionList.size()==0 && multiQuestionList.size()==0 && likertQuestionList.size()==0)
                {
                    answerOpenQuestions();
                }


            }
        });
    }

    private void answerSingleQuestions() {

        LayoutInflater inflater = (LayoutInflater) DoSurvey.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        questionView = inflater.inflate(R.layout.single_question_survey, null);
        // Add the new row before the add field button.


        questionDetails.removeAllViews();

        questionDetails.addView(questionView);


        if (singleI < singleQuestionList.size()) {

            final TextView question = questionView.findViewById(R.id.txtQuestion);
            final TextView choice1 = questionView.findViewById(R.id.choice1);
            final TextView choice2 = questionView.findViewById(R.id.choice2);
            final TextView choice3 = questionView.findViewById(R.id.choice3);
            final TextView choice4 = questionView.findViewById(R.id.choice4);

            question.setText(singleQuestionList.get(singleI).getQuestion());
            choice1.setText(singleQuestionList.get(singleI).getChoice1());
            choice2.setText(singleQuestionList.get(singleI).getChoice2());
            choice3.setText(singleQuestionList.get(singleI).getChoice3());
            choice4.setText(singleQuestionList.get(singleI).getChoice4());

            Log.d(TAG, "answerSingleQuestions: SingleQuestion:" + singleQuestionList.get(singleI).getQuestion());

            choice1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerSingleQuestion(choice1);
                    singleI = singleI + 1;
                    fetchSingleQuestions(question, choice1, choice2, choice3, choice4, singleI);


                    if (singleI >= singleQuestionList.size()) {
                        if (likertQuestionList.size() > 0) {
                            Log.d(TAG, "Likert:Start");
                            answerLikertQuestions();
                        }
                        else if(multiQuestionList.size()>0)
                        {
                            Log.d(TAG, "onClick: Multi:Start");
                            answerMultiQuestions();
                        }

                    }


                }
            });
            choice2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerSingleQuestion(choice2);
                    singleI = singleI + 1;
                    fetchSingleQuestions(question, choice1, choice2, choice3, choice4, singleI);
                    if (singleI >= singleQuestionList.size()) {
                        if (likertQuestionList.size() > 0) {
                            Log.d(TAG, "Likert:Start");
                            answerLikertQuestions();
                        }
                    }
                }
            });
            choice3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerSingleQuestion(choice3);
                    singleI = singleI + 1;
                    fetchSingleQuestions(question, choice1, choice2, choice3, choice4, singleI);
                    if (singleI >= singleQuestionList.size()) {
                        if (likertQuestionList.size() > 0) {
                            Log.d(TAG, "Likert:Start");
                            answerLikertQuestions();
                        }
                    }
                }
            });
            choice4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerSingleQuestion(choice4);
                    singleI = singleI + 1;
                    fetchSingleQuestions(question, choice1, choice2, choice3, choice4, singleI);
                    if (singleI >= singleQuestionList.size()) {
                        if (likertQuestionList.size() > 0) {
                            Log.d(TAG, "Likert:Start");
                            answerLikertQuestions();
                        }
                    }
                }
            });


            Log.d(TAG, "KeyId" + singleKeys.get(singleI));

        }
    }

    private void answerOpenQuestions() {
        btnNext.setVisibility(View.VISIBLE);

        LayoutInflater inflater = (LayoutInflater) DoSurvey.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        questionView = inflater.inflate(R.layout.open_question_survey, null);
        // Add the new row before the add field button.
        Log.d(TAG, "answerOpenQuestions: " + openI);

        questionDetails.removeAllViews();

        questionDetails.addView(questionView);


        if (openI < openQuestionList.size()) {

            final TextView question = questionView.findViewById(R.id.txtQuestion);

            final EditText txtAnswer = questionView.findViewById(R.id.txtAnswer);


            question.setText(openQuestionList.get(openI).getQuestion());

            if (openI == openQuestionList.size() - 1) {
                btnNext.setText("FINISH");
                btnNext.setBackgroundColor(getResources().getColor(R.color.colorAccent));

            }

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    final String answer = txtAnswer.getText().toString().trim();

                    txtAnswer.setText("");
                    Log.d(TAG, "Answer " + answer);
                    answerOpenQuestion(answer);
                    openI = openI + 1;
                    fetchOpenQuestions(question, openI);
                    if (openI == openQuestionList.size() - 1) {
                        btnNext.setText("FINISH");
                        btnNext.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                    }
                    if (openI >= openQuestionList.size()) {
                        Toast.makeText(DoSurvey.this, "Survey Finished!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DoSurvey.this, StudentHome.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        finish();

                    }
                }
            });


            Log.d(TAG, "KeyId" + openKeys.get(openI));

        }
    }

    private void answerLikertQuestions() {
        btnNext.setVisibility(View.VISIBLE);

        LayoutInflater inflater = (LayoutInflater) DoSurvey.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        questionView = inflater.inflate(R.layout.likert_question_survey, null);
        // Add the new row before the add field button.
        Log.d(TAG, "answerLikertQuestions: " + likertI);

        questionDetails.removeAllViews();

        questionDetails.addView(questionView);

        if (likertI < likertQuestionList.size()) {
            final TextView question = questionView.findViewById(R.id.txtQuestion);
            final TextView txtChoice = questionView.findViewById(R.id.txtChoice);
            final CircleSeekBar likertScale = questionView.findViewById(R.id.likert_scale);



            question.setText(likertQuestionList.get(likertI).getQuestion());

            txtChoice.setText(likertQuestionList.get(likertI).getChoice1());


            likertScale.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
                @Override
                public void onChanged(CircleSeekBar circleSeekBar, int i) {
                    switch (i) {
                        case 1:
                            txtChoice.setText(likertQuestionList.get(likertI).getChoice1());
                            break;
                        case 2:
                            txtChoice.setText(likertQuestionList.get(likertI).getChoice2());
                            break;
                        case 3:
                            txtChoice.setText(likertQuestionList.get(likertI).getChoice3());
                            break;
                        case 4:
                            txtChoice.setText(likertQuestionList.get(likertI).getChoice4());
                            break;

                    }
                }
            });


            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String answer = txtChoice.getText().toString();

                    txtChoice.setText("");
                    answerLikertQuestion(answer);
                    likertI = likertI + 1;
                    fetchLikertQuestions(question, likertI, likertScale);

                    if (likertI >= likertQuestionList.size()) {
                        if (multiQuestionList.size() > 0) {
                            Log.d(TAG, "Multi:Start");
                            answerMultiQuestions();
                        }
                        else
                        {
                            Log.d(TAG, "onClick: Open:Start");
                            answerOpenQuestions();;
                        }
                    }


                }
            });


            Log.d(TAG, "KeyId" + likertKeys.get(likertI));

        }


    }


    private void fetchOpenQuestions(TextView question, int i) {
        if (openI < openQuestionList.size()) {
            question.setText(openQuestionList.get(i).getQuestion());
        }

    }

    private void fetchLikertQuestions(TextView question, int i, CircleSeekBar likertScale) {
        if (likertI < likertQuestionList.size()) {
            question.setText(likertQuestionList.get(i).getQuestion());
            likertScale.setCurProcess(1);
        }

    }

    private void answerOpenQuestion(String answer) {
        if (openI < openQuestionList.size()) {

            final String questionKey = openKeys.get(openI);

            final Map<String, Object> answerMap = new HashMap<>();
            answerMap.put("answer", answer);
            answerMap.put("username", student.getName());
            answerMap.put("timestamp", FieldValue.serverTimestamp());

            firestore.document(key)
                    .collection("OpenChoice")
                    .document(questionKey).collection("answers")
                    .document(userId).set(answerMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(DoSurvey.this, "Open Answered!", Toast.LENGTH_SHORT).show();

                            FirebaseFirestore.getInstance().collection("Students").document(userId)
                                    .collection("Surveys")
                                    .document(key).collection("answers")
                                    .document(questionKey).set(answerMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(DoSurvey.this, "Set into Student", Toast.LENGTH_SHORT).show();

                                            answerNo.put("answerNo", progress);
                                            FirebaseFirestore.getInstance().collection("Students").document(userId)
                                                    .collection("Surveys")
                                                    .document(key).set(answerNo);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DoSurvey.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DoSurvey.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }

    private void answerLikertQuestion(String answer) {
        if (likertI < likertQuestionList.size()) {

            final String questionKey = likertKeys.get(likertI);

            final Map<String, Object> answerMap = new HashMap<>();
            answerMap.put("answer", answer);
            answerMap.put("username", student.getName());
            answerMap.put("timestamp", FieldValue.serverTimestamp());

            firestore.document(key)
                    .collection("LikertChoice")
                    .document(questionKey).collection("answers")
                    .document(userId).set(answerMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(DoSurvey.this, "Likert Answered!", Toast.LENGTH_SHORT).show();

                            FirebaseFirestore.getInstance().collection("Students").document(userId)
                                    .collection("Surveys")
                                    .document(key).collection("answers")
                                    .document(questionKey).set(answerMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(DoSurvey.this, "Set into Student", Toast.LENGTH_SHORT).show();

                                            answerNo.put("answerNo", progress);
                                            FirebaseFirestore.getInstance().collection("Students").document(userId)
                                                    .collection("Surveys")
                                                    .document(key).set(answerNo);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DoSurvey.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DoSurvey.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }


    private void answerMultiQuestions() {
        btnNext.setVisibility(View.GONE);
        LayoutInflater inflater = (LayoutInflater) DoSurvey.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        questionView = inflater.inflate(R.layout.single_question_survey, null);
        // Add the new row before the add field button.
        Log.d(TAG, "answerMultiQuestions: " + multiI);

        questionDetails.removeAllViews();

        questionDetails.addView(questionView);


        if (multiI < multiQuestionList.size()) {

            final TextView question = questionView.findViewById(R.id.txtQuestion);
            final TextView choice1 = questionView.findViewById(R.id.choice1);
            final TextView choice2 = questionView.findViewById(R.id.choice2);
            final TextView choice3 = questionView.findViewById(R.id.choice3);
            final TextView choice4 = questionView.findViewById(R.id.choice4);

            question.setText(multiQuestionList.get(multiI).getQuestion());
            choice1.setText(multiQuestionList.get(multiI).getChoice1());
            choice2.setText(multiQuestionList.get(multiI).getChoice2());
            choice3.setText(multiQuestionList.get(multiI).getChoice3());
            choice4.setText(multiQuestionList.get(multiI).getChoice4());

            Log.d(TAG, "answerMultiQuestions: MultiQuestion:" + multiQuestionList.get(multiI).getQuestion());

            choice1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerMultiQuestion(choice1);
                    multiI = multiI + 1;
                    fetchMultiQuestions(question, choice1, choice2, choice3, choice4, multiI);

                    if (multiI >= multiQuestionList.size()) {
                        if (openQuestionList.size() > 0) {
                            answerOpenQuestions();
                        }
                    }


                }
            });
            choice2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerMultiQuestion(choice2);
                    multiI = multiI + 1;
                    fetchMultiQuestions(question, choice1, choice2, choice3, choice4, multiI);

                    if (multiI >= multiQuestionList.size()) {
                        if (openQuestionList.size() > 0) {
                            answerOpenQuestions();
                        }
                    }

                }
            });
            choice3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerMultiQuestion(choice3);
                    multiI = multiI + 1;
                    fetchMultiQuestions(question, choice1, choice2, choice3, choice4, multiI);

                    if (multiI >= multiQuestionList.size()) {
                        if (openQuestionList.size() > 0) {
                            answerOpenQuestions();
                        }
                    }

                }
            });
            choice4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerMultiQuestion(choice4);
                    multiI = multiI + 1;
                    fetchMultiQuestions(question, choice1, choice2, choice3, choice4, multiI);

                    if (multiI >= multiQuestionList.size()) {
                        if (openQuestionList.size() > 0) {
                            answerOpenQuestions();
                        }
                    }

                }
            });


            Log.d(TAG, "KeyId" + multiKeys.get(multiI));

        }


    }

    private void fetchMultiQuestions(TextView question, TextView choice1, TextView choice2, TextView choice3, TextView choice4, int i) {
        if (i < multiQuestionList.size()) {
            question.setText(multiQuestionList.get(i).getQuestion());
            choice1.setText(multiQuestionList.get(i).getChoice1());
            choice2.setText(multiQuestionList.get(i).getChoice2());
            choice3.setText(multiQuestionList.get(i).getChoice3());
            choice4.setText(multiQuestionList.get(i).getChoice4());

            checkChoiceStatus(choice1, choice2, choice3, choice4);

            Log.d(TAG, "answerMultiQuestions: MultiQuestion:" + multiQuestionList.get(i).getQuestion());

        }

    }


    private void fetchSingleQuestions(TextView question, TextView choice1, TextView choice2, TextView choice3, TextView choice4, int i) {
        if (i < singleQuestionList.size()) {
            question.setText(singleQuestionList.get(i).getQuestion());
            choice1.setText(singleQuestionList.get(i).getChoice1());
            choice2.setText(singleQuestionList.get(i).getChoice2());
            choice3.setText(singleQuestionList.get(i).getChoice3());
            choice4.setText(singleQuestionList.get(i).getChoice4());

            checkChoiceStatus(choice1, choice2, choice3, choice4);

            Log.d(TAG, "answerSingleQuestions: SingleQuestion:" + singleQuestionList.get(i).getQuestion());

        }

    }

    private void checkChoiceStatus(TextView choice1, TextView choice2, TextView choice3, TextView choice4) {
        if (choice1.getText().toString().equals("none")) {
            choice1.setVisibility(View.GONE);
        }
        if (choice2.getText().toString().equals("none")) {
            choice2.setVisibility(View.GONE);
        }
        if (choice3.getText().toString().equals("none")) {
            choice3.setVisibility(View.GONE);
        }
        if (choice4.getText().toString().equals("none")) {
            choice4.setVisibility(View.GONE);
        }

    }

    private void answerSingleQuestion(TextView choice) {
        if (singleI < singleQuestionList.size()) {
            String answer = choice.getText().toString();

            final String questionKey = singleKeys.get(singleI);

            final Map<String, Object> answerMap = new HashMap<>();
            answerMap.put("answer", answer);
            answerMap.put("username", student.getName());
            answerMap.put("timestamp", FieldValue.serverTimestamp());

            firestore.document(key)
                    .collection("SingleChoice")
                    .document(questionKey).collection("answers")
                    .document(userId).set(answerMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(DoSurvey.this, "Answered!", Toast.LENGTH_SHORT).show();

                            FirebaseFirestore.getInstance().collection("Students").document(userId)
                                    .collection("Surveys")
                                    .document(key).collection("answers")
                                    .document(questionKey).set(answerMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(DoSurvey.this, "Set into Students", Toast.LENGTH_SHORT).show();
                                            answerNo.put("answerNo", progress);
                                            FirebaseFirestore.getInstance().collection("Students").document(userId)
                                                    .collection("Surveys")
                                                    .document(key).set(answerNo);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DoSurvey.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DoSurvey.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }


    private void answerMultiQuestion(TextView choice) {
        if (multiI < multiQuestionList.size()) {
            String answer = choice.getText().toString();


            final String questionKey = multiKeys.get(multiI);

            final Map<String, Object> answerMap = new HashMap<>();
            answerMap.put("answer", answer);
            answerMap.put("username", student.getName());
            answerMap.put("timestamp", FieldValue.serverTimestamp());


            firestore.document(key)
                    .collection("MultiChoice")
                    .document(questionKey).collection("answers")
                    .document(userId).set(answerMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(DoSurvey.this, "Multi Answered!", Toast.LENGTH_SHORT).show();

                            FirebaseFirestore.getInstance().collection("Students").document(userId)
                                    .collection("Surveys")
                                    .document(key).collection("answers")
                                    .document(questionKey).set(answerMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(DoSurvey.this, "Set into Students", Toast.LENGTH_SHORT).show();
                                            answerNo.put("answerNo", progress);
                                            FirebaseFirestore.getInstance().collection("Students").document(userId)
                                                    .collection("Surveys")
                                                    .document(key).set(answerNo);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DoSurvey.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DoSurvey.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }
}
