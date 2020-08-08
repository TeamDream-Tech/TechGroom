package com.example.passion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AdminActivity extends AppCompatActivity {
    Button button;
    EditText title, detail, imgurl, addimgurl, source, gerne;

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
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.newstitle);
        detail = findViewById(R.id.newsdetail);
        imgurl = findViewById(R.id.newsimgurl);
        addimgurl = findViewById(R.id.addnewsimgurl);
        source = findViewById(R.id.sourcelink);
        gerne = findViewById(R.id.gerne);

        pd = new ProgressDialog(this);
        button = findViewById(R.id.uploadnews);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newstitle = title.getText().toString().trim();
                String describe = detail.getText().toString().trim();
                String url = imgurl.getText().toString().trim();
                String newurl = addimgurl.getText().toString().trim();
                String sourcelink = source.getText().toString().trim();
                String gernes = gerne.getText().toString().trim();

                uploadnews(newstitle, describe, url, newurl, sourcelink, gernes);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            uid = mCurrentUser.getUid();
            useremail = mCurrentUser.getEmail();
        }

    }

    private void uploadnews(String newstitle, String describe, String url, String newurl, String sourcelink, String gernes) {

            pd.setMessage("Creating Post");
            pd.show();

            final String timeStamp = String.valueOf(System.currentTimeMillis());

            android.net.Uri xx = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
            String pic = xx.toString();


            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("uid", uid);
//            hashMap.put("uDp", pic);
            hashMap.put("pId", timeStamp);

            hashMap.put("pTitle", newstitle);
            hashMap.put("PDescription", describe);
            hashMap.put("source", sourcelink);
            hashMap.put("PImage", url);
            hashMap.put("newPImage", newurl);
            hashMap.put("gerne", gernes);

            hashMap.put("PTime", timeStamp);
            hashMap.put("pComments", "0");
            hashMap.put("views", "0");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Updates");
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(AdminActivity.this, "News Update Sent", Toast.LENGTH_SHORT).show();
                            title.setText("");
                            title.clearFocus();

                            imgurl.clearFocus();
                            imgurl.setText("");

                            detail.setText("");
                            detail.clearFocus();

                            addimgurl.clearFocus();
                            addimgurl.setText("");

                            source.setText("");
                            source.clearFocus();
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdminActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });

        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);

    }
}
