package com.buttercell.survy.lecturer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.buttercell.survy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class LecturerRegister extends AppCompatActivity {
    private static final String TAG = "LecturerRegister";
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.txt_signIn)
    TextView txtSignIn;
    @BindView(R.id.lbl_title)
    TextView lblTitle;
    @BindView(R.id.txt_userName)
    MaterialEditText txtUserName;
    @BindView(R.id.txt_email)
    MaterialEditText txtEmail;
    @BindView(R.id.txt_pass)
    MaterialEditText txtPass;
    @BindView(R.id.txt_confirm_pass)
    MaterialEditText txtConfirmPass;
    @BindView(R.id.textView1)
    TextView textView1;
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    @BindView(R.id.txtDepartment)
    MaterialEditText txtDepartment;
    @BindView(R.id.linearSignUp)
    LinearLayout linearSignUp;
    @BindView(R.id.scroll1)
    ScrollView scroll1;
    @BindView(R.id.card_container)
    RelativeLayout cardContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_register);


        Paper.init(this);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    @OnClick({R.id.btnRegister, R.id.txt_signIn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:

                break;
            case R.id.txt_signIn:
                startActivity(new Intent(LecturerRegister.this, LecturerLogin.class));
                overridePendingTransition(R.anim.animation_enter,R.anim.animation_leave);
                break;
        }
    }

    private void register() {
        final String userName = txtUserName.getText().toString().trim();
        final String email = txtEmail.getText().toString().trim();
        final String pass = txtPass.getText().toString().trim();
        String confirmPass = txtConfirmPass.getText().toString().trim();
        final String department = txtDepartment.getText().toString().trim();

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
                                    registerUser(user, userName, email, pass,department);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    progressDialog.dismiss();
                                    Toast.makeText(LecturerRegister.this, task.getException().getLocalizedMessage(),
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

    private void registerUser(final FirebaseUser user, final String userName, final String email, final String pass, String department) {

        final String id = user.getUid();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", userName);
        userMap.put("email", email);
        userMap.put("department", department);
        userMap.put("pass", pass);
        userMap.put("role", "lecturer");


        firestore.collection("Lecturers").document(id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Paper.book().write("email", email);
                Paper.book().write("name", userName);
                Paper.book().write("pass", pass);
                Paper.book().write("user_id", id);

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userName).build();

                user.updateProfile(profileUpdates);
                progressDialog.dismiss();
                Toast.makeText(LecturerRegister.this, "Great! A Few more steps", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LecturerRegister.this, LecturerAccountSetup.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LecturerRegister.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_enter,R.anim.animation_leave);
    }
}
