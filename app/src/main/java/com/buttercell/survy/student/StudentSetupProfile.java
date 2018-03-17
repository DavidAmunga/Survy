package com.buttercell.survy.student;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.buttercell.survy.R;
import com.buttercell.survy.model.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentSetupProfile extends Fragment {
    private static final String TAG = "StudentSetupProfile";

    @BindView(R.id.profileImg)
    CircleImageView profileImg;
    @BindView(R.id.btnNext)
    Button btnNext;
    Unbinder unbinder;
    private int PICK_IMAGE = 1;


    private Uri imageUri;


    public StudentSetupProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_setup_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        Paper.init(getContext());

        final ViewPager pager = getActivity().findViewById(R.id.viewPager);


        profileImg.setOnClickListener(new View.OnClickListener() {
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
                if(imageUri!=null) {
                    btnNext.setEnabled(false);
                    btnNext.setText("Uploading.....");
                    final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    StorageReference user_profile = FirebaseStorage.getInstance().getReference("ProfileImages").child(id + ".jpg");
                    user_profile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                String downloadUri = task.getResult().getDownloadUrl().toString();
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("image", downloadUri);

                                DocumentReference docRef = FirebaseFirestore.getInstance().collection("Students").document(id);
                                docRef.update(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Toast.makeText(getContext(), "Image Added!", Toast.LENGTH_SHORT).show();
                                                pager.setCurrentItem(1);
                                            }

                                        });

                                if (Paper.book().read("currentStudent") != null) {
                                    Student student = Paper.book().read("currentStudent");

                                    student.setImage(downloadUri);

                                    Paper.book().write("currentStudent", student);
                                }


                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else
                {
                    Toast.makeText(getContext(), "Please upload an image! ", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            profileImg.setImageURI(imageUri);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
