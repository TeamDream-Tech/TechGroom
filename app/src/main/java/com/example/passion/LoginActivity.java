package com.example.passion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    TextView tvSignUp;
    private long backPressedTime;
    private Toast backToast;
    EditText emailId, password;
    Button btnSignIn;
    FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    String mainid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.emailinput);
        password = findViewById(R.id.passwordinput);
        btnSignIn = findViewById(R.id.singinbtn);
        progressBar = findViewById(R.id.loginprog);
        progressBar.setVisibility(View.GONE);

        tvSignUp = findViewById(R.id.tosignup);

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if( mFirebaseUser != null ){
                    mainid = mFirebaseUser.getUid();
                    Intent i = new Intent(LoginActivity.this, BottomActivity.class);
                    startActivity(i);
//                    checkOnlineStatus("online");
                    finish();
                }
                else{

                }
            }
        };
        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String password1 = password.getText().toString();
                String email = emailId.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Please enter an email id");
                    emailId.requestFocus();
                }
                else if(password1.isEmpty()){
                    password.setError("Please enter a password");
                    password.requestFocus();
                }
                else if(email.isEmpty() && password1.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Fields are Empty",Toast.LENGTH_SHORT).show();
                }
                else if (!(email.isEmpty() && password1.isEmpty())){
                    progressBar.setVisibility(View.VISIBLE);
                    mFirebaseAuth.signInWithEmailAndPassword(email, password1).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Login Error, Please Try again",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                            }
                            else {
                                Intent intHOme = new Intent(LoginActivity.this, BottomActivity.class);
                                startActivity(intHOme);
                                finish();

                            }
                        }
                    });
                }
                else {
                    Toast.makeText(LoginActivity.this, "An Error Occured",Toast.LENGTH_SHORT).show();
                }

            }


        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            finish();
            finishAffinity();
            System.exit(0);
            return;
        }else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    private void checkOnlineStatus(String status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(mainid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        // update value of onlinestatus of current user
        dbRef.updateChildren(hashMap);

    }

}
