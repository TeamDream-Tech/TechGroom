package com.example.passion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextName, editTextEmail, editTextPassword, editTextPhone;
    private ProgressBar progressBar;


    TextView tvSignIn;
    private FirebaseAuth mAuth;
    ImageView ImageUserPhoto;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ImageUserPhoto = findViewById(R.id.imageholder);
        editTextName = findViewById(R.id.fullname);
        editTextEmail = findViewById(R.id.fullemail);
        editTextPassword = findViewById(R.id.fullpassword);
        editTextPhone = findViewById(R.id.fullphone);
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        tvSignIn = findViewById(R.id.tosignin);

        findViewById(R.id.singinbtn).setOnClickListener(this);

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });


        ImageUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 22){
                    checkAndRequestForPermission();
                }
                else {
                    openGallery();
                }
            }
        });
    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(galleryIntent, "select picture"),REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(SignUpActivity.this, "Please grant access for required permission",Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(SignUpActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }
        else
            openGallery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESCODE && data != null){

            //The user has successfully picked an image
            pickedImgUri = data.getData();
            ImageUserPhoto.setImageURI(pickedImgUri);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }

    private void registerUser() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String image = "0";
        final String onlineStatus = "online";


        if (name.isEmpty()) {
            editTextName.setError("Invalid input");
            editTextName.requestFocus();
            return;
        }


        if (email.isEmpty()) {
            editTextEmail.setError("Invalid input");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Invalid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Invalid input");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Too Short");
            editTextPassword.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError("Invalid input");
            editTextPhone.requestFocus();
            return;
        }

        if (phone.length() != 11) {
            editTextPhone.setError("Invalid number");
            editTextPhone.requestFocus();
            return;
        }
        if(pickedImgUri == null){
            Toast toast;
            toast=Toast.makeText(getApplicationContext(),"PLEASE CHOOSE A PICTURE",Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = firebaseUser.getUid();


                        if (task.isSuccessful()) {

                            User user = new User(
                                    name,
                                    email,
                                    phone,
                                    password,
                                    uid,
                                    image,
                                    onlineStatus
                            );

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        updateUserInfo(name,pickedImgUri, mAuth.getCurrentUser());
                                        Toast.makeText(SignUpActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(SignUpActivity.this, BottomActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        //display a failure message
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        StorageReference mStroage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStroage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String image = uri.toString();

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("image")
                                .setValue(image);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("image", image);


                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("onlineStatus")
                                .setValue("online");
                        hashMap.put("onlineStatus", "offline");



                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            updateUI();
                                        }
                                    }
                                });

                    }
                });

            }
        });
    }

    private void updateUI() {

    }
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.singinbtn:
                if (isNetworkAvailable() == true){
                    registerUser();
                    break;
                }
                else {
                    Snackbar.make(v, "Error, Please check Internet connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();                }
        }
    }
}
