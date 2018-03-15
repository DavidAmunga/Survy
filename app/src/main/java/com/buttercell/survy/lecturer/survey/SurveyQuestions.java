package com.buttercell.survy.lecturer.survey;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.buttercell.survy.R;
import com.buttercell.survy.common.Common;
import com.buttercell.survy.helpers.NonSwipeableViewPager;
import com.buttercell.survy.helpers.QuestionsSingleton;
import com.buttercell.survy.model.Question;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurveyQuestions extends Fragment {

    private static final String TAG = "SurveyQuestions";
    @BindView(R.id.progressBar)
    MaterialProgressBar progressBar;
    @BindView(R.id.spinnerQuestion)
    MaterialSpinner spinnerQuestion;
    @BindView(R.id.parentQuestionLayout)
    LinearLayout parentQuestionLayout;
    @BindView(R.id.cardContainer)
    CardView cardContainer;
    @BindView(R.id.btnNext)
    Button btnNext;
    Unbinder unbinder;


    List<String> questionList;

    View questionView;

    int progress = 1;

    FirebaseFirestore firestore;

    List<String> questionOrder = new ArrayList<>();

    String id;

    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

    NonSwipeableViewPager pager;

    public SurveyQuestions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey_questions, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        pager = getActivity().findViewById(R.id.viewPager);

        firestore = FirebaseFirestore.getInstance();

        progressBar.setMax(Common.questionNo);


        progressBar.setProgress(progress);


        id = Common.surveyKey;


        questionList = Arrays.asList(getResources().getStringArray(R.array.questions));
        spinnerQuestion.setItems(questionList);

        spinnerQuestion.setSelectedIndex(0);
        spinnerQuestion.setSelected(true);

        spinnerQuestion.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (item.equals("Single choice")) {
                    parentQuestionLayout.removeAllViews();
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    questionView = inflater.inflate(R.layout.single_choice, null);
                    // Add the new row before the add field button.

                    parentQuestionLayout.addView(questionView);


                } else if (item.equals("Multi choice")) {

                    parentQuestionLayout.removeAllViews();
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    questionView = inflater.inflate(R.layout.single_choice, null);
                    // Add the new row before the add field button.

                    parentQuestionLayout.addView(questionView);


                } else if (item.equals("Open")) {
                    parentQuestionLayout.removeAllViews();
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    questionView = inflater.inflate(R.layout.open_choice, null);
                    // Add the new row before the add field button.

                    parentQuestionLayout.addView(questionView);

                }


            }
        });


    }


    private Question getSingleChoiceQuestionDetails(View questionView) {
        final EditText questionDetails = questionView.findViewById(R.id.questionDetails);

        String qstion = questionDetails.getText().toString().trim();

        EditText txtChoiceNo1 = questionView.findViewById(R.id.choice1);
        EditText txtChoiceNo2 = questionView.findViewById(R.id.choice2);
        EditText txtChoiceNo3 = questionView.findViewById(R.id.choice3);
        EditText txtChoiceNo4 = questionView.findViewById(R.id.choice4);

        String choiceNo1 = txtChoiceNo1.getText().toString().trim();
        String choiceNo2 = txtChoiceNo2.getText().toString().trim();
        String choiceNo3 = txtChoiceNo3.getText().toString().trim();
        String choiceNo4 = txtChoiceNo4.getText().toString().trim();


        if (TextUtils.isEmpty(choiceNo1)) {
            choiceNo1 = "none";
        }
        if (TextUtils.isEmpty(choiceNo2)) {
            choiceNo2 = "none";
        }
        if (TextUtils.isEmpty(choiceNo3)) {
            choiceNo3 = "none";
        }
        if (TextUtils.isEmpty(choiceNo4)) {
            choiceNo4 = "none";
        }

        Question question = new Question(qstion, choiceNo1, choiceNo2, choiceNo3, choiceNo4);


        Log.d(TAG, "setSingleChoiceView: " + question.toString());
        return question;


    }


    private Question getOpenChoiceQuestionDetails(View questionView) {
        final EditText questionDetails = questionView.findViewById(R.id.questionDetails);

        String qstion = questionDetails.getText().toString().trim();

        Question question = new Question(qstion, "none", "none", "none", "none");


        Log.d(TAG, "setOpenChoices: " + question.toString());
        return question;


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick(R.id.btnNext)
    public void onViewClicked() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Surveys").child(id);
        ref.child("userName").setValue(userName);
        ref.child("userId").setValue(userId);
        ref.child("timestamp").setValue(ServerValue.TIMESTAMP);


        progressBar.setMax(Common.questionNo);


        if (progress <= Common.questionNo) {
            progress = progress + 1;
            progressBar.setProgress(progress);


            Log.d(TAG, "Progress " + progress);

            parentQuestionLayout.removeAllViews();

            if (questionView != null) {
                switch (questionList.get(spinnerQuestion.getSelectedIndex())) {
                    case "Single choice":
                        Question singleQuestion = getSingleChoiceQuestionDetails(questionView);
                        addSingleChoice(singleQuestion, id);
                        Common.singleChoiceList.add(singleQuestion);
                        Log.d(TAG, "Single Choice " + Common.singleChoiceList);
                        QuestionsSingleton.getInstance().setSingleChoiceList(Common.singleChoiceList);
                        Log.d(TAG, "Single Question " + QuestionsSingleton.getInstance().getSingleChoiceList());
                        break;
                    case "Multi choice":
                        Question multiQuestion = getSingleChoiceQuestionDetails(questionView);
                        addMultiChoice(multiQuestion, id);
                        Common.multiChoiceList.add(multiQuestion);
                        QuestionsSingleton.getInstance().setMultiChoiceList(Common.multiChoiceList);
                        break;
                    case "Open":
                        Question openQuestion = getOpenChoiceQuestionDetails(questionView);
                        addOpenChoice(openQuestion, id);
                        Common.openChoiceList.add(openQuestion);
                        QuestionsSingleton.getInstance().setOpenChoiceList(Common.openChoiceList);
                        break;
                }

                ref.child("questionOrder").setValue(questionOrder);

                Log.d(TAG, "onViewClicked: " + questionOrder.toString());


            }


        }
        if (progress == Common.questionNo) {
            btnNext.setText("FINISH");
        }

        if (progress > Common.questionNo) {
            Toast.makeText(getContext(), "Finished!", Toast.LENGTH_SHORT).show();


            pager.setCurrentItem(3);
        }


    }

    private void addOpenChoice(Question question, String id) {
        String choiceId = UUID.randomUUID().toString();

        questionOrder.add(progress - 2, choiceId);


        firestore.collection("Surveys").document(id).collection("OpenChoice").document(choiceId).set(question).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(getContext(), "Added!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void addSingleChoice(Question question, String id) {


        String choiceId = UUID.randomUUID().toString();

        questionOrder.add(progress - 2, choiceId);


        Map<String, Object> questionDetails = new HashMap<>();


        firestore.collection("Surveys").document(id).collection("SingleChoice").document(choiceId).set(question).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(getContext(), "Added!", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void addMultiChoice(Question question, String id) {


        String choiceId = UUID.randomUUID().toString();

        questionOrder.add(progress - 2, choiceId);


        firestore.collection("Surveys").document(id).collection("MultiChoice").document(choiceId).set(question).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(getContext(), "Added!", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
