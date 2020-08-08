package com.example.passion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CreateQuestion extends AppCompatActivity {
    TextView newbutton;
    EditText question, description, links;
    ImageView imageView;
    TextView textView;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String uid;
    ProgressDialog pd;

    DatabaseReference userDbRef;
    // user info
    String username, useremail, dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newbutton = findViewById(R.id.newpostques);
        question = findViewById(R.id.newquestion);
        description = findViewById(R.id.newdescription);
        links = findViewById(R.id.newlinks);
        imageView = findViewById(R.id.ques_profpic);
        textView = findViewById(R.id.ques_profname);
        pd = new ProgressDialog(this);

        userDbRef =  FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDbRef.orderByChild("email").equalTo(useremail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    username = ""+ ds.child("name").getValue();
                    useremail = ""+ ds.child("email").getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        newbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Snackbar.make(v, "Error, Please check Internet connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    String title = question.getText().toString().trim();
                    String describe = description.getText().toString().trim();
                    String tags = links.getText().toString().trim();

                    if (TextUtils.isEmpty(title)){
                        Toast.makeText(CreateQuestion.this, "Enter title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(describe)){
                        Toast.makeText(CreateQuestion.this, "Enter description", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(tags)){
                        Toast.makeText(CreateQuestion.this, "Enter tags", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        uploadData(title, describe, "noImage");
                    }

            }
        });


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            uid = mCurrentUser.getUid();

            useremail = mCurrentUser.getEmail();

        }
        Glide.with(CreateQuestion.this).load(currentUser.getPhotoUrl()).into(imageView);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = String.valueOf(dataSnapshot.child("name").getValue());
                String email = String.valueOf(dataSnapshot.child("email").getValue());


                textView.setText(name);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    private void uploadData(String title, final String describe, String Uri) {
        pd.setMessage("Creating Post");
        pd.show();

        final String timeStamp = String.valueOf(System.currentTimeMillis());
        String filepathAndName = "Posts/" + "post_" + timeStamp;

        android.net.Uri xx = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        String pic = xx.toString();


        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("uName", username);
        hashMap.put("uEmail", useremail);
        hashMap.put("uDp", pic);
        hashMap.put("pId", timeStamp);
        hashMap.put("pTitle", title);
        hashMap.put("PDescription", describe);
        hashMap.put("PImage", "No image");
        hashMap.put("PTime", timeStamp);
        hashMap.put("pLikes", "0");
        hashMap.put("pComments", "0");
        hashMap.put("views", "0");
        hashMap.put("bestanswer", "none");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Questions");
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(CreateQuestion.this, "Post Published", Toast.LENGTH_SHORT).show();
                        question.setText("");
                        question.clearFocus();
                        description.setText("");
                        description.clearFocus();
                        links.setText("");
                        links.clearFocus();
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateQuestion.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            this.finish();
        }


        return super.onOptionsItemSelected(item);
    }
}
