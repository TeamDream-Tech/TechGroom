package com.example.passion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.passion.adapters.AdapterAnswers;
import com.example.passion.models.ModelComments;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class QuestionDetail extends AppCompatActivity {

    String myUid, myEmail, myName, myDp,
            postId, pLikes, hisDp, hisName, views;
    String hisuid;
    String pImage, uid;
    String pDescr;

    ProgressDialog pd;
    boolean mProcessComment = false;
    boolean mProcessLike = false;



    TextView unameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv, pCommentsTv, pViews;
    ImageView quesprofpic;
    ImageView likeBtn, dislikebtn;
    ImageView detailprof;
    RelativeLayout profileLayout;
    RecyclerView recyclerView;

    List<ModelComments> commentsList;
    AdapterAnswers adapterAnswers;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String pTitle;

    //add comment views

    EditText commentEt;
    ImageButton sendBtn;
    ImageButton moreBtn;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");


        // Textviews
        unameTv = findViewById(R.id.quesdetailname);
        pTimeTv = findViewById(R.id.quesdetailtime);
        pTitleTv = findViewById(R.id.quesdetailtitle);
        pDescriptionTv = findViewById(R.id.quesdetaildescrip);
        pLikesTv = findViewById(R.id.quesdetaillikes);
        pCommentsTv = findViewById(R.id.quesdetailcomment);
        pViews = findViewById(R.id.quesdetailviews);

        // Images
        quesprofpic = findViewById(R.id.quesdetailprofile);
        detailprof = findViewById(R.id.quesdetailpic);
        likeBtn = findViewById(R.id.quesdetaillike);
        dislikebtn = findViewById(R.id.quesdetaildislike);

        // recyclerview
        recyclerView = findViewById(R.id.quesdetailRecycler);

        // Edittext
        commentEt = findViewById(R.id.quesanswer);

        // Image buttons
        sendBtn = findViewById(R.id.quesdetailsendBtn);
        moreBtn = findViewById(R.id.quesdetailmorebtn);

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions();
            }
        });
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();
            }
        });
        dislikebtn.setVisibility(View.GONE);
        dislikebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dislikePost();
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        loadPostInfo();
        checkUserStatus();
        loadUserInfo();
        setLikes();
        loadComments();

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

    private void loadPostInfo() {
        //get post using the post id
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Questions");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // keep checking the posts until get the required post
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data
                    pTitle = ""+ds.child("pTitle").getValue();
                    pDescr = ""+ds.child("PDescription").getValue();
                    pLikes = ""+ds.child("pLikes").getValue();
                    String pTimeStamp = ""+ds.child("PTime").getValue();
                    pImage = ""+ds.child("PImage").getValue();
                    hisDp = ""+ds.child("uDp").getValue();
                    hisuid = ""+ds.child("uid").getValue();
                    String uEmail = ""+ds.child("uEmail").getValue();
                    hisName = ""+ds.child("uName").getValue();
                    views = ""+ds.child("views").getValue();
                    String commentCount = ""+ ds.child("pComments").getValue();

                    // convert timestamp to proper format

                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
                    final String pTime = DateFormat.format("hh:mm aa", calendar).toString();

                    // set data
                    pTitleTv.setText(pTitle);
                    pDescriptionTv.setText(pDescr);
                    pLikesTv.setText("+" + pLikes);
                    pTimeTv.setText(pTime);
                    pCommentsTv.setText(commentCount + " ANSWERS");

                    unameTv.setText(hisName);
                    pViews.setText(views);



                    // set the image of the user who has the post
                    try{
                        // TODO: need to import picaso library
                        Picasso.get().load(hisDp).into(detailprof);
                    }
                    catch (Exception e){
                    }

                    //set current user image in comment part
                    try {
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
                        databaseReference.child(hisuid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String name = String.valueOf(dataSnapshot.child("name").getValue());
                                String email = String.valueOf(dataSnapshot.child("email").getValue());

                                unameTv.setText(name);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });
                    }
                    catch (Exception e){

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void checkUserStatus(){
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
                    myName = ""+ds.child("name").getValue();
                    myDp = pic;

//                    set data
                    try {
                        // if image is received then set
                        Picasso.get().load(xxx).placeholder(R.drawable.ic_face_black_24dp).into(quesprofpic);
                    }
                    catch (Exception e){
                        Picasso.get().load(xxx).into(quesprofpic);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setLikes() {
        // when details of post is loading, also check if the user has liked the post or not
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).hasChild(myUid)){
                    // user has liked this post
                    /*To indicate that the post is liked by the signed in user
                    change drawable left icon of like button
                    change text of the like button from "like" to "Liked"
                     */
                    likeBtn.setImageResource(R.drawable.ic_liked);

                }
                else {
                    // user has not liked this post
                     /*To indicate that the post is not liked by the signed in user
                    change drawable left icon of like button
                    change text of the like button from "liked" to "Like"
                     */
                    likeBtn.setImageResource(R.drawable.ic_thumb_up);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void likePost() {
        mProcessLike = true;
        //get id of the post clicked

        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Questions");

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcessLike){
                    if (!dataSnapshot.child(postId).hasChild(myUid)){
                        // not liked, like it
                        postRef.child(postId).child("pLikes").setValue(""+(Integer.parseInt(pLikes)+1));
                        likesRef.child(postId).child(myUid).setValue("Liked"); // set any value
                        mProcessLike = false;
                    }
                    else {
                        // already liked, dislike it
                        postRef.child(postId).child("pLikes").setValue("" + (Integer.parseInt(pLikes) - 1));
                        likesRef.child(postId).child(myUid).removeValue();
                        mProcessLike = false;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void dislikePost() {
//        mProcessLike = true;
//        //get id of the post clicked
//
//        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
//        final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Questions");
//
//        likesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (mProcessLike){
//                    if (!dataSnapshot.child(postId).hasChild(myUid)) {
//                        // like it
//                        postRef.child(postId).child("pLikes").setValue("" + (Integer.parseInt(pLikes) - 1));
//                        likesRef.child(postId).child(myUid).setValue("Disliked"); // set any value
//                        mProcessLike = false;
//                    }
//                    else if (dataSnapshot.child(postId).hasChild(myUid) && likesRef.child(postId).child(myUid).equals("Disliked")){
//                        // already disliked, remove it
//                        postRef.child(postId).child("pLikes").setValue(""+(Integer.parseInt(pLikes)+1));
//                        likesRef.child(postId).child(myUid).removeValue(); // set any value
//                        mProcessLike = false;
//                    }
//                    else if (dataSnapshot.child(postId).hasChild(myUid) && likesRef.child(postId).child(myUid).equals("Liked")){
//                        // already liked dislike it
//                        postRef.child(postId).child("pLikes").setValue("" + (Integer.parseInt(pLikes) - 2));
//                        likesRef.child(postId).child(myUid).setValue("Disliked"); // set any value
//                        mProcessLike = false;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void showMoreOptions() {
        PopupMenu popupMenu = new PopupMenu(this, moreBtn, Gravity.END);

        if (hisuid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            //<------------------ for edit option ----------------------------->//
//            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Edit");
            //<------------------ for edit option ----------------------------->//
        }

        popupMenu.getMenu().add(Menu.NONE, 2, 0, "Report");


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                final int id = item.getItemId();
                if (id == 0){
                    // delete is clicked
                    AlertDialog.Builder builder = new AlertDialog.Builder(QuestionDetail.this);
                    builder.setCancelable(true);
                    builder.setTitle("Warning!");
                    builder.setMessage("Are you sure you want to delete this post? \nYou can't undo this action");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    bedginDelete();
                                }
                            });
                    builder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else if (id == 2){
                    AlertDialog.Builder builder = new AlertDialog.Builder(QuestionDetail.this);
                    builder.setCancelable(true);
                    builder.setTitle("Notice!");
                    builder.setMessage("Are you sure you want to send report?");
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(QuestionDetail.this, "Report Sent Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                    builder.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else if (id == 1){
                    // Edit is clicked
                    Intent intent = new Intent(QuestionDetail.this, Createpost.class);
                    intent.putExtra("Key", "editPost");
                    intent.putExtra("editPostId", postId);
                    startActivity(intent);
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void loadComments() {
        // layout (linear) for recyclerview

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        // set layout to recyclerview

        recyclerView.setLayoutManager(linearLayoutManager);

        // init comment list
        commentsList = new ArrayList<>();

        // path of the post, to get it's comments

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Questions").child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelComments modelComments = ds.getValue(ModelComments.class);

                    commentsList.add(modelComments);

                    // setudp adapter
                    adapterAnswers = new AdapterAnswers(getApplicationContext(), commentsList, myUid, postId);

                    //set adapter
                    recyclerView.setAdapter(adapterAnswers);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(QuestionDetail.this, "Failed to load data", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void postComment() {
        pd = new ProgressDialog(this);
        pd.setMessage("Adding comment...");

        // get data from comment edittext
        String comment = commentEt.getText().toString().trim();

        //validate
        if (TextUtils.isEmpty(comment)){
            // no value is entered
            Toast.makeText(this, "answer field is empty...", Toast.LENGTH_SHORT).show();
            return;
        }
        String timeStamp = String.valueOf(System.currentTimeMillis());

//        each post will have a child "comments" that will comtain comments of that post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Questions").child(postId).child("Comments");

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
                        Toast.makeText(QuestionDetail.this, "Comment Added", Toast.LENGTH_SHORT).show();
                        commentEt.setText("");
                        commentEt.clearFocus();
                        updateCommentCount();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed
                        pd.dismiss();
                        Toast.makeText(QuestionDetail.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void updateCommentCount() {
        // whenever user adds comment, increase the comment count as we did for like count
        mProcessComment = true;
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Questions").child(postId);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
