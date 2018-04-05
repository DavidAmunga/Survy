package com.buttercell.survy.lecturer.survey;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.buttercell.survy.R;
import com.buttercell.survy.common.Common;
import com.buttercell.survy.model.Lecturer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurveyDetailOne extends Fragment {

    private static final String TAG = "SurveyDetailOne";
    @BindView(R.id.surveyImage)
    CircleImageView surveyImage;
    @BindView(R.id.userImage)
    CircleImageView userImage;
    @BindView(R.id.surveyName)
    EditText txtSurveyName;
    Unbinder unbinder;
    @BindView(R.id.btnNext)
    Button btnNext;


    private int PICK_IMAGE = 1;


    private Uri imageUri;


    public SurveyDetailOne() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey_detail_one, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ViewPager pager = getActivity().findViewById(R.id.viewPager);

        Lecturer lecturer = Paper.book().read("currentLecturer");
        String profileImage = lecturer.getImage();
        imageUri = null;

        Glide.with(getContext()).load(profileImage).into(userImage);

        surveyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE);

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String surveyName = txtSurveyName.getText().toString();

                if (imageUri != null && !TextUtils.isEmpty(surveyName)) {

                    btnNext.setEnabled(false);
                    btnNext.setText("Adding...");


                    Common.surveyKey = UUID.randomUUID().toString();
                    Common.surveyName = surveyName;
                    final StorageReference survey_image = FirebaseStorage.getInstance().getReference("SurveyImages").child(Common.surveyKey + ".jpg");
                    survey_image.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                String imageUrl = task.getResult().getDownloadUrl().toString();

                                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                Date date = new Date();
                                String surveyDate=dateFormat.format(date);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Surveys").child(Common.surveyKey);
                                ref.child("image").setValue(imageUrl);
                                ref.child("name").setValue(surveyName);
                                ref.child("dateAdded").setValue(surveyDate);


                                pager.setCurrentItem(1);
                                btnNext.setEnabled(true);
                                btnNext.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                btnNext.setText("NEXT");

                            } else {
                                Toast.makeText(getContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }


            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if(data!=null)
            {
                imageUri = data.getData();
                surveyImage.setImageURI(imageUri);
            }

        }
    }

}
