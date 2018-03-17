package com.buttercell.survy;

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

import com.buttercell.survy.lecturer.LecturerHome;
import com.buttercell.survy.lecturer.LecturerLogin;
import com.buttercell.survy.student.StudentHome;
import com.buttercell.survy.student.StudentLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class Welcome extends AppCompatActivity {
    private static final String TAG = "Welcome";


    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.lblTeacher)
    Button lblTeacher;
    @BindView(R.id.lblStudent)
    Button lblStudent;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        Paper.init(this);


        progressDialog = new ProgressDialog(this);

        checkLogin();
    }


    private void checkLogin() {
        String em = Paper.book().read("email");
        String pwd = Paper.book().read("pass");

        if (em != null && pwd != null) {
            if (!TextUtils.isEmpty(em) && !TextUtils.isEmpty(pwd)) {
                progressDialog.setMessage("One moment please....");
                progressDialog.setCancelable(false);
                progressDialog.show();
                FirebaseAuth.getInstance().signInWithEmailAndPassword(em, pwd)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                                    Toast.makeText(Welcome.this, task.getException().getLocalizedMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else {
                                    Log.d(TAG, "onComplete: Sign In Success!");
                                    Paper.book().write("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    verify_user(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                }

                            }
                        });


            }
        }
    }

    private void verify_user(final String uid) {
        FirebaseFirestore.getInstance().collection("Students").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    progressDialog.dismiss();
                    Toast.makeText(Welcome.this, "Welcome!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Welcome.this, StudentHome.class));
                    finish();
                }
                else
                {
                    FirebaseFirestore.getInstance().collection("Lecturers").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                            if (documentSnapshot.exists()) {
                                progressDialog.dismiss();
                                Toast.makeText(Welcome.this, "Welcome! Lec!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Welcome.this, LecturerHome.class));
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });


    }

    @OnClick({R.id.lblTeacher, R.id.lblStudent})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lblTeacher:
                startActivity(new Intent(Welcome.this, LecturerLogin.class).putExtra("role", "teacher"));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                break;
            case R.id.lblStudent:
                startActivity(new Intent(Welcome.this, StudentLogin.class).putExtra("role", "student"));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                break;
        }
    }


}
