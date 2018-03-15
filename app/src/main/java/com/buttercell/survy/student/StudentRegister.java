package com.buttercell.survy.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.buttercell.survy.R;
import com.buttercell.survy.common.Common;
import com.buttercell.survy.model.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class StudentRegister extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "StudentRegister";

    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.txt_signIn)
    TextView txtSignIn;
    @BindView(R.id.lbl_title)
    TextView lblTitle;
    @BindView(R.id.txt_userName)
    MaterialEditText txtUserName;
    @BindView(R.id.spinnerCourse)
    MaterialSpinner spinnerCourse;
    @BindView(R.id.txtDateJoined)
    TextView txtDateJoined;
    @BindView(R.id.txt_email)
    MaterialEditText txtEmail;
    @BindView(R.id.txt_pass)
    MaterialEditText txtPass;
    @BindView(R.id.txt_confirm_pass)
    MaterialEditText txtConfirmPass;
    @BindView(R.id.textView1)
    TextView textView1;

    List<String> courseList;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);
        ButterKnife.bind(this);
        Paper.init(this);


        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        courseList = Arrays.asList(getResources().getStringArray(R.array.courses));

        spinnerCourse.setItems(courseList);

        txtDateJoined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                        StudentRegister.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.setTitle("Pick your join date");
                datePickerDialog.show(getFragmentManager(), "DatePicker");
                datePickerDialog.showYearPickerFirst(true);
                datePickerDialog.vibrate(true);
            }
        });

    }

    @OnClick({R.id.btnRegister, R.id.txt_signIn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                register();
                break;
            case R.id.txt_signIn:
                startActivity(new Intent(StudentRegister.this, StudentLogin.class));
                break;
        }
    }

    private void register() {
        final String course = courseList.get(spinnerCourse.getSelectedIndex());
        final String userName = txtUserName.getText().toString().trim();
        final String email = txtEmail.getText().toString().trim();
        final String pass = txtPass.getText().toString().trim();
        String confirmPass = txtConfirmPass.getText().toString().trim();
        final String dateJoined = txtDateJoined.getText().toString().trim();


        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(userName)) {
            if (confirmPass.equals(pass)) {
                progressDialog.setMessage("Signing in....");
                progressDialog.setCancelable(false);
                progressDialog.show();


                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    registerUser(user, course, userName, email, pass, dateJoined);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    progressDialog.dismiss();
                                    Toast.makeText(StudentRegister.this, task.getException().getLocalizedMessage(),
                                            Toast.LENGTH_SHORT).show();

                                }

                                // ...
                            }
                        });
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Please confirm your password correctly!", Toast.LENGTH_SHORT).show();
            }

        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
        }


    }

    private void registerUser(final FirebaseUser user, final String course, final String userName, final String email, final String pass, final String dateJoined) {

        final String id = user.getUid();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", userName);
        userMap.put("course", course);
        userMap.put("currentYear", dateJoined);
        userMap.put("email", email);
        userMap.put("pass", pass);
        userMap.put("role", "student");


        firestore.collection("Students").document(id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Paper.book().write("email", email);
                Paper.book().write("pass", pass);
                Paper.book().write("user_id", id);
                Paper.book().write("name", userName);

                Student student = new Student(course, dateJoined, email, "", userName);

                Paper.book().write("currentStudent", student);


                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userName).build();

                user.updateProfile(profileUpdates);

                Common.courseName = course;

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Courses");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            if (postSnapshot.child("courseName").getValue().equals(course)) {
                                String key = postSnapshot.getKey();
                                Common.courseKey = key;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                progressDialog.dismiss();
                Toast.makeText(StudentRegister.this, "Great! A Few more steps", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(StudentRegister.this, StudentAccountSetup.class);

                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(StudentRegister.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);


        int finalYear = 1;
        if (currentYear - year < 0) {
            Toast.makeText(this, "You havent yet started University!", Toast.LENGTH_SHORT).show();
        } else if (currentYear - year > 0) {
            finalYear = finalYear + (currentYear - year);
        }

        txtDateJoined.setText(String.valueOf(finalYear));
    }
}
