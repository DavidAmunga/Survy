package com.buttercell.survy.lecturer.survey;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.buttercell.survy.R;
import com.buttercell.survy.common.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurveyDetailTwo extends Fragment {

    private static final String TAG = "SurveyDetailTwo";


    @BindView(R.id.year)
    Spinner spinnerYear;
    @BindView(R.id.sem)
    Spinner spinnerSem;
    @BindView(R.id.surveyDescription)
    EditText surveyDescription;
    @BindView(R.id.btnNext)
    Button btnNext;
    Unbinder unbinder;
    @BindView(R.id.questionNo)
    EditText questionNo;

    public SurveyDetailTwo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey_detail_two, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final ViewPager pager = getActivity().findViewById(R.id.viewPager);

        ArrayAdapter semAdpter = ArrayAdapter.createFromResource(
                getContext(), R.array.semesters, android.R.layout.simple_spinner_item);
        semAdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerSem.setAdapter(semAdpter);

        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.years, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerYear.setAdapter(yearAdapter);


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String survey_desc = surveyDescription.getText().toString();
                int qstNo = Integer.parseInt(questionNo.getText().toString());
                String year = spinnerYear.getSelectedItem().toString();
                String sem = spinnerSem.getSelectedItem().toString();


                if (!TextUtils.isEmpty(survey_desc) && qstNo>0 && !TextUtils.isEmpty(questionNo.getText().toString())) {

                    btnNext.setEnabled(false);
                    btnNext.setText("Adding...");

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Surveys").child(Common.surveyKey);
                    ref.child("year").setValue(year);
                    ref.child("sem").setValue(sem);
                    ref.child("desc").setValue(survey_desc);

                    Common.questionNo=qstNo;

                    Log.d(TAG, "Question Number"+Common.questionNo);

                    pager.setCurrentItem(2);
                    btnNext.setEnabled(true);
                    btnNext.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnNext.setText("NEXT");
                }
                else
                {
                    Toast.makeText(getContext(), "Please insert all fields", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
