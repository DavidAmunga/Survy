package com.buttercell.survy.student;


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

import com.bumptech.glide.Glide;
import com.buttercell.survy.R;
import com.buttercell.survy.lecturer.LecturerLogin;
import com.buttercell.survy.model.Student;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentProfileFragment extends Fragment {
    private static final String TAG = "StudentProfileFragment";


    @BindView(R.id.logOut)
    Button logOut;
    Unbinder unbinder;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.profileUser)
    TextView profileUser;
    @BindView(R.id.profileYear)
    TextView profileYear;
    @BindView(R.id.profileCourse)
    TextView profileCourse;

    public StudentProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Paper.init(getContext());


        Student student = Paper.book().read("currentStudent");


        profileUser.setText(student.getName());
        profileCourse.setText(student.getCourse());
        profileYear.setText("Year "+student.getCurrentYear());

        Glide.with(this).load(student.getImage()).into(profileImage);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Logging out!");
                FirebaseAuth.getInstance().signOut();
                Paper.book().destroy();
                startActivity(new Intent(getContext(), StudentLogin.class));
                getActivity().finish();

            }
        });
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            if (getActivity() != null) {
                getActivity().setTitle("Profile ");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
