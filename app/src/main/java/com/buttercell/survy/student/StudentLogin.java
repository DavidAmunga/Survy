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
import com.buttercell.survy.model.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class StudentLogin extends AppCompatActivity {
    private static final String TAG = "StudentLogin";
    @BindView(R.id.txt_email)
    MaterialEditText txtEmail;
    @BindView(R.id.txt_pass)
    MaterialEditText txtPass;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.txt_signUp)
    TextView txtSignUp;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        ButterKnife.bind(this);

        Paper.init(this);
        progressDialog = new ProgressDialog(this);
        firestore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();


        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentLogin.this, StudentRegister.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalSignIn();
            }
        });

    }

    private void normalSignIn() {
        final String email = txtEmail.getText().toString().trim();
        final String pass = txtPass.getText().toString().trim();


        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
            progressDialog.setMessage("Signing in.....");
            progressDialog.setCancelable(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser user = mAuth.getCurrentUser();
                                verifyUser(user, email, pass, user.getUid());
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(StudentLogin.this, task.getException().getLocalizedMessage(),
                                        Toast.LENGTH_SHORT).show();

                            }

                            // ...
                        }
                    });


        } else {
            Toast.makeText(this, "Please enter all the fields!", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyUser(FirebaseUser user, final String email, final String pass, final String uid) {
        String userId = user.getUid();

        firestore.collection("Students").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {

                    Student student = documentSnapshot.toObject(Student.class);

                    Paper.book().write("currentStudent", student);
                    Paper.book().write("email", email);
                    Paper.book().write("pass", pass);
                    Paper.book().write("user_id", uid);

                    progressDialog.dismiss();

                    Toast.makeText(StudentLogin.this, "Welcome! " + student.getName(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(StudentLogin.this, StudentHome.class));
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(StudentLogin.this, "Sorry, you are not a student!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_enter,R.anim.animation_leave);
    }
}
