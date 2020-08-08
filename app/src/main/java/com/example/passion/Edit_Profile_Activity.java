package com.example.passion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static com.example.passion.ActivityMain.USER_PREF;

public class Edit_Profile_Activity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String uid;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;
    ImageView UserPhoto;
    TextView change;
    loadingDialog loadingDialog;
    EditText email, name, number;
    String setemail;
    String setname;
    String setnumber;
    String password;
    SharedPreferences sp;

    String names, emails, numbers;
    String status = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__profile_);

        change = findViewById(R.id.saveprofile);
        loadingDialog = new loadingDialog(Edit_Profile_Activity.this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = findViewById(R.id.edit_profemail);
        name = findViewById(R.id.edit_profname);
        number = findViewById(R.id.edit_profnumber);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            uid = mCurrentUser.getUid();
        }

        loadImage();


        UserPhoto = findViewById(R.id.edit_profpic);
        Glide.with(Edit_Profile_Activity.this).load(currentUser.getPhotoUrl()).into(UserPhoto);
        TextView Username = findViewById(R.id.edit_profname);
        TextView UserMail = findViewById(R.id.edit_profemail);
        TextView UserNumber = findViewById(R.id.edit_profnumber);

        UserMail.setText(emails);
        Username.setText(names);
        UserNumber.setText(numbers);


        Checkconnection();
        UserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Check Internet Connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void Checkconnection(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            connected = true;

            change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetworkAvailable() == true){
                        Toast.makeText(Edit_Profile_Activity.this, "Connected", Toast.LENGTH_SHORT).show();
                        loadingDialog.startLoadingDialog();
                        updateProfilenumber();
                        SaveProfileChanges();
                        SavenameChanges();
                    }
                    else {
                        Toast.makeText(Edit_Profile_Activity.this, "Not Connected", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else
        {
            connected = false;
            change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetworkAvailable() == true){
                        Toast.makeText(Edit_Profile_Activity.this, "Connected", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(Edit_Profile_Activity.this, "Not Connected", Toast.LENGTH_SHORT).show();
                    }
                        Snackbar.make(v, "No Internet connection", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                }
            });

        }


    }


    public void loadImage(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                names = String.valueOf(dataSnapshot.child("name").getValue());
                emails = String.valueOf(dataSnapshot.child("email").getValue());
                numbers = String.valueOf(dataSnapshot.child("phone").getValue());
                password = String.valueOf(dataSnapshot.child("password").getValue());

                sp = getApplicationContext().getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sp.edit();

                editor.putString(names, names);
                editor.putString(emails, emails);
                editor.commit();

                TextView Username = findViewById(R.id.edit_profname);
                TextView UserMail = findViewById(R.id.edit_profemail);
                TextView UserNumber = findViewById(R.id.edit_profnumber);

                UserMail.setText(sp.getString(emails, ""));
                Username.setText(sp.getString(names, ""));
                UserNumber.setText(numbers);
                StringBuilder str = new StringBuilder();



                UserPhoto.setOnClickListener(new View.OnClickListener() {
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        if (ContextCompat.checkSelfPermission(Edit_Profile_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Edit_Profile_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(Edit_Profile_Activity.this, "Please grant access for required permission",Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(Edit_Profile_Activity.this,
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
            change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingDialog.startLoadingDialog();
                    updateUserInfo(pickedImgUri, mAuth.getCurrentUser());
                    UserPhoto.setImageURI(pickedImgUri);
                    updateProfilenumber();
                    SaveProfileChanges();
                    SavenameChanges();
                }
            });

            //The user has successfully picked an image
            pickedImgUri = data.getData();
            UserPhoto.setImageURI(pickedImgUri);
        }
    }


    private void updateUserInfo(Uri pickedImgUri, final FirebaseUser currentUser) {
        StorageReference mStroage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStroage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            updateUI();

//                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
//                                            Uri xxx = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
//                                            String pic = xxx.toString();
//
//                                            HashMap<String, Object> hashMap = new HashMap<>();
//                                            hashMap.put("uDp", pic);
                                        }
                                    }
                                });
                            }
                        });

                    }
                });
            }

    private void updateUI() {
        Toast.makeText(Edit_Profile_Activity.this, "Avatar Changed Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void SaveProfileChanges(){
        String Useremail= email.getText().toString();
        setemail=email.getText().toString();

        AuthCredential credential= EmailAuthProvider.getCredential(currentUser.getEmail(), password);
        currentUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                                if (!task.isSuccessful()) {
                                    Toast.makeText(Edit_Profile_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference= firebaseDatabase.getReference("Users").child(uid).child("email");
                                    databaseReference.setValue(setemail).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("passu", "onFailure:" +e.getMessage());
                                        }
                                    });
                                    Toast.makeText(Edit_Profile_Activity.this, "Email updated", Toast.LENGTH_SHORT).show();
                                }

                        }else{
                            Toast.makeText(Edit_Profile_Activity.this, "Password Incorrect", Toast.LENGTH_LONG).show();
                        }
                                    //----------------------------------------------------------\\
                    }
            });
    }

    public void SavenameChanges(){
        String Username= name.getText().toString();
        setname=name.getText().toString();


        AuthCredential credential= EmailAuthProvider.getCredential(currentUser.getEmail(), password);
        currentUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            if (!task.isSuccessful()) {
                                loadingDialog.dismissdialog();
                                Toast.makeText(Edit_Profile_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference= firebaseDatabase.getReference("Users").child(uid).child("name");
                                databaseReference.setValue(setname).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("passu", "onFailure:" +e.getMessage());
                                    }
                                });
                                loadingDialog.dismissdialog();
                                Toast.makeText(Edit_Profile_Activity.this, "Name updated", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(Edit_Profile_Activity.this, "Password Incorrect", Toast.LENGTH_LONG).show();
                        }
                        //----------------------------------------------------------\\
                    }
                });
    }

    public void updateProfilenumber(){

        String Usernumber= number.getText().toString();
        setnumber=number.getText().toString();

        if (Usernumber.length() >= 11) {

            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);
            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                if (!task.isSuccessful()) {
                                    Toast.makeText(Edit_Profile_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(uid).child("phone");
                                    databaseReference.setValue(setnumber).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("passu", "onFailure:" + e.getMessage());
                                        }
                                    });
                                    Toast.makeText(Edit_Profile_Activity.this, "Number updated", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Edit_Profile_Activity.this, "Password Incorrect", Toast.LENGTH_LONG).show();
                            }
                            //----------------------------------------------------------\\
                        }
                    });
        }else {
            number.setError("too short");
            number.requestFocus();
        }
    }
}