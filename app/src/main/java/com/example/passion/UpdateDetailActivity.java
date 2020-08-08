package com.example.passion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.passion.adapters.AdapterIntake;
import com.example.passion.models.ModelComments;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UpdateDetailActivity extends AppCompatActivity {
    TextView gerne, title, date, views, comments, body;
    ImageView image, newimage;

    String myUid, myEmail, myName, myDp,
            postId, pLikes, hisDp, hisName, numviews;
    String hisuid;
    String pImage, uid;
    String pDescr, pTitle, pGerne;

    ImageButton imageButton;
    ImageView imageView;
    RelativeLayout relativeLayout;
    FloatingActionButton floatingActionButton;
    EditText editText;
    ProgressDialog pd;

    boolean mProcessComment = false;
    RecyclerView recyclerView;

    List<ModelComments> commentsList;
    AdapterIntake adapterIntake;

    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_detail);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        gerne = findViewById(R.id.hotgerne);
        title = findViewById(R.id.hotheader);
        date = findViewById(R.id.hottime);
        views = findViewById(R.id.hotviews);
        comments = findViewById(R.id.hotcomments);
        body = findViewById(R.id.hotcontent);

        imageButton = findViewById(R.id.hotdetailsendBtn);
        imageView = findViewById(R.id.hotdetailprofile);
        relativeLayout = findViewById(R.id.hotcommentLayout);
        editText = findViewById(R.id.hotintake);
        floatingActionButton = findViewById(R.id.hotshowlayout);

        recyclerView = findViewById(R.id.hotdetailRecycler);

        image = findViewById(R.id.hotpicture);
        loadPostInfo();
        loadUserInfo();
        loadComments();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.VISIBLE);
                floatingActionButton.setVisibility(View.GONE);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
                relativeLayout.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.VISIBLE);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // user is signed in
            myEmail = user.getEmail();
            myUid = user.getUid();
        }
        else {
            // user is not signed in
        }


    }
    private void loadUserInfo() {
        final Uri xxx = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        final String pic = xxx.toString();

        final Query myRef = FirebaseDatabase.getInstance().getReference("Users");
        // get current user info
        myRef.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    myDp = pic;

//                    set data
                    try {
                        // if image is received then set
                        Picasso.get().load(xxx).placeholder(R.drawable.ic_face_black_24dp).into(imageView);
                    }
                    catch (Exception e){
                        Picasso.get().load(xxx).into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadPostInfo() {
        //get post using the post id
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Updates");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // keep checking the posts until get the required post
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data
                    pGerne = ""+ds.child("gerne").getValue();
                    pTitle = ""+ds.child("pTitle").getValue();
                    String pTimeStamp = ""+ds.child("PTime").getValue();
                    numviews = ""+ds.child("views").getValue();
                    String commentCount = ""+ ds.child("pComments").getValue();
                    pDescr = ""+ds.child("PDescription").getValue();

                    pImage = ""+ds.child("PImage").getValue();

                    // convert timestamp to proper format

//                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
//                    calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
//                    final String pTime = DateFormat.format("hh:mm aa", calendar).toString();


                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String dateTime = DateFormat.format("MMM-dd hh:mm aa", cal).toString();

                    // set data
                    gerne.setText(pGerne);
                    title.setText(pTitle);
                    date.setText(dateTime);
                    views.setText(numviews);
                    comments.setText(commentCount);
                    body.setText(pDescr);

                    // set the image of the user who has the post
                    try{
                        // TODO: need to import picaso library
                        Picasso.get().load(pImage).into(image);
                    }
                    catch (Exception e){
                    }

                    //set current user image in comment part

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void loadComments() {
        // layout (linear) for recyclerview

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        // set layout to recyclerview

        recyclerView.setLayoutManager(linearLayoutManager);

        // init comment list
        commentsList = new ArrayList<>();

        // path of the post, to get it's comments

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Updates").child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelComments modelComments = ds.getValue(ModelComments.class);

                    commentsList.add(modelComments);

                    // setudp adapter
                    adapterIntake = new AdapterIntake(getApplicationContext(), commentsList, myUid, postId);

                    //set adapter
                    recyclerView.setAdapter(adapterIntake);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UpdateDetailActivity.this, "Please check connection", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void postComment() {
        pd = new ProgressDialog(this);
        pd.setMessage("Publishing...");

        // get data from comment edittext
        String comment = editText.getText().toString().trim();

        //validate
        if (TextUtils.isEmpty(comment)){
            // no value is entered
            Toast.makeText(this, "answer field is empty...", Toast.LENGTH_SHORT).show();
            return;
        }
        String timeStamp = String.valueOf(System.currentTimeMillis());

//        each post will have a child "comments" that will comtain comments of that post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Updates").child(postId).child("Comments");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cId", timeStamp);
        hashMap.put("comment", comment);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("uid", myUid);
        hashMap.put("uEmail", myEmail);
        hashMap.put("uDp", myDp);
        hashMap.put("uName", myName);


        // put this data in db
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // added
                        pd.dismiss();
                        Toast.makeText(UpdateDetailActivity.this, "Comment Added", Toast.LENGTH_SHORT).show();
                        editText.setText("");
                        editText.clearFocus();
                        updateCommentCount();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed
                        pd.dismiss();
                        Toast.makeText(UpdateDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void updateCommentCount() {
        // whenever user adds comment, increase the comment count as we did for like count
        mProcessComment = true;
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Updates").child(postId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcessComment){
                    String comments = ""+ dataSnapshot.child("pComments").getValue();
                    int newCommentVal= Integer.parseInt(comments) + 1;
                    ref.child("pComments").setValue("" +newCommentVal);
                    mProcessComment = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
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
