package com.buttercell.survy.lecturer.survey;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.buttercell.survy.R;
import com.buttercell.survy.common.Common;
import com.buttercell.survy.helpers.NonSwipeableViewPager;
import com.buttercell.survy.helpers.QuestionsSingleton;
import com.buttercell.survy.lecturer.LecturerHome;
import com.buttercell.survy.lecturer.SetSurvey;
import com.buttercell.survy.model.Question;
import com.buttercell.survy.model.QuestionCategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurveyPreview extends Fragment {

    private static final String TAG = "SurveyPreview";
    Unbinder unbinder;

    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnNext)
    Button btnNext;
    @BindView(R.id.expandableLayout)
    ExpandableLayout expandableLayout;

    private List<Question> singleChoiceList;
    private List<Question> multiChoiceList;
    private List<Question> openChoiceList;
    private List<Question> likertChoiceList;

    public SurveyPreview() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey_preview, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "Singlee " + QuestionsSingleton.getInstance().getSingleChoiceList());

        NonSwipeableViewPager pager = getActivity().findViewById(R.id.viewPager);


    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            singleChoiceList = QuestionsSingleton.getInstance().getSingleChoiceList();
            multiChoiceList = QuestionsSingleton.getInstance().getMultiChoiceList();
            openChoiceList = QuestionsSingleton.getInstance().getOpenChoiceList();
            likertChoiceList = QuestionsSingleton.getInstance().getLikertChoiceList();


            expandableLayout.setRenderer(new ExpandableLayout.Renderer<QuestionCategory, Question>() {
                @Override
                public void renderParent(View view, QuestionCategory questionCategory, boolean isExpanded, int i) {
                    ((TextView) view.findViewById(R.id.tv_parent_name)).setText(questionCategory.name);
                    view.findViewById(R.id.arrow).setBackgroundResource(isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
                }

                @Override
                public void renderChild(View view, Question question, int parentPosition, int childPosition) {
                    ((TextView) view.findViewById(R.id.tv_child_name)).setText(question.getQuestion());

                }
            });


            if (singleChoiceList.size() > 0) {
                expandableLayout.addSection(getSingleSection(singleChoiceList));
            }
            if (likertChoiceList.size() > 0) {
                expandableLayout.addSection(getLikertSection(likertChoiceList));

            }
            if (multiChoiceList.size() > 0) {
                expandableLayout.addSection(getMultiSection(multiChoiceList));
            }

            if (openChoiceList.size() > 0) {
                expandableLayout.addSection(getOpenSection(openChoiceList));

            }


        }
    }

    private Section getLikertSection(List<Question> likertChoiceList) {
        Section<QuestionCategory, Question> section = new Section<>();

        QuestionCategory questionCategory = new QuestionCategory("Likert Choices");

        section.parent = questionCategory;
        section.children.addAll(likertChoiceList);
        section.expanded=true;
        return section;
    }

    private Section getOpenSection(List<Question> openChoiceList) {
        Section<QuestionCategory, Question> section = new Section<>();

        QuestionCategory questionCategory = new QuestionCategory("Open Choices");

        section.parent = questionCategory;
        section.children.addAll(openChoiceList);
        section.expanded=true;
        return section;
    }

    private Section getSingleSection(List<Question> singleChoiceList) {
        Section<QuestionCategory, Question> section = new Section<>();

        QuestionCategory questionCategory = new QuestionCategory("Single Choices");

        section.parent = questionCategory;
        section.children.addAll(singleChoiceList);
        section.expanded=true;
        return section;
    }

    private Section getMultiSection(List<Question> multiChoiceList) {
        Section<QuestionCategory, Question> section = new Section<>();

        QuestionCategory questionCategory = new QuestionCategory("Multi Choices");

        section.parent = questionCategory;
        section.children.addAll(multiChoiceList);
        section.expanded=true;
        return section;
    }
    // ...

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btnStart, R.id.btnNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnStart:
                resetSurvey(Common.surveyKey);
                break;
            case R.id.btnNext:
                openChoiceList.clear();
                singleChoiceList.clear();
                multiChoiceList.clear();
                Common.surveyKey="";
                startActivity(new Intent(getActivity(), LecturerHome.class));
                getActivity().finish();
                break;
        }
    }

    private void resetSurvey(final String surveyKey) {
        Log.d(TAG, "Survey Key" + Common.surveyKey);


        FirebaseFirestore.getInstance().collection("Surveys").document(surveyKey).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Surveys").child(surveyKey);
                ref.setValue(null);
                openChoiceList.clear();
                singleChoiceList.clear();
                multiChoiceList.clear();
                Common.surveyKey="";
                startActivity(new Intent(getActivity(), SetSurvey.class));
                getActivity().finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
