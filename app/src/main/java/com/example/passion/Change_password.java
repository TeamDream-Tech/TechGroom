package com.example.passion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Change_password extends AppCompatActivity {
    EditText password1, password2, current;
    TextView Save;
    FirebaseUser currentUser;
    String uid;
    FirebaseAuth mAuth;
    loadingDialog loadingDialog;
    public String setPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new loadingDialog(Change_password.this);

        current = findViewById(R.id.currentpassword);
        password1 = findViewById(R.id.new_password);
        password2 = findViewById(R.id.confirmnewpassword);
        Save = findViewById(R.id.save_passbtn);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            uid = mCurrentUser.getUid();
        }

        ResetLoginMessagingPin();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void ResetLoginMessagingPin(){

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String paswordResetLoginPin=current.getText().toString();
                final String password = password1.getText().toString().trim();
                final String retupepassword = password2.getText().toString().trim();
                setPin=password1.getText().toString();

                if (password.isEmpty()) {
                    password1.setError("Enter new Password");
                    password1.requestFocus();
                }
                else if (retupepassword.isEmpty()) {
                    password2.setError("Enter Confirm Password");
                    password2.requestFocus();
                }
                else if (password.isEmpty() & retupepassword.isEmpty()) {
                    Toast.makeText(Change_password.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 6) {
                    password1.setError("Too Short");
                    password1.requestFocus();
                }
                else if (retupepassword.length() < 6) {
                    password2.setError("Too Short");
                    password2.requestFocus();
                }
                else if (!password.matches(retupepassword)){
                    password2.setError("Password does not match");
                    password2.requestFocus();
                }
                else if (paswordResetLoginPin.isEmpty()) {
                        current.setError("please enter a password");
                        current.requestFocus();

                    }else {
                        AuthCredential credential= EmailAuthProvider.getCredential(currentUser.getEmail(),paswordResetLoginPin);
                        currentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        loadingDialog.startLoadingDialog();
                                        // Start Changing the password from here, call the other methoed.
                                        currentUser.updatePassword(setPin).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                    DatabaseReference databaseReference= firebaseDatabase.getReference("Users").child(uid).child("password");
                                                    databaseReference.setValue(setPin).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("passu", "onFailure:" +e.getMessage());
                                                            Toast.makeText(Change_password.this, "Failed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    loadingDialog.dismissdialog();
                                                    //Snack bar come up
                                                    final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), "Password Changed", Snackbar.LENGTH_LONG);
                                                    snackBar.setAction("OK", new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                            // Call your action method here
                                                            snackBar.dismiss();
                                                        }
                                                    });
                                                    snackBar.show();
                                                    current.setText(null);
                                                    password1.setText(null);
                                                    password2.setText(null);
                                                    current.clearFocus();
                                                    password1.clearFocus();
                                                    password2.clearFocus();

                                                }else{
                                                    loadingDialog.dismissdialog();
                                                    Toast.makeText(Change_password.this, "Error in Process", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }else{
                                        Toast.makeText(Change_password.this, "Password Incorrect", Toast.LENGTH_LONG).show();
                                        current.setError("password Incorrect");
                                        current.requestFocus();
                                    }
                                }
                            });

                    }


            }
        });

    }
}
