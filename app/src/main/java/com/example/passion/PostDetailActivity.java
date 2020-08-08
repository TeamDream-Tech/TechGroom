package com.example.passion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.passion.adapters.AdapterComments;
import com.example.passion.models.ModelComments;
import com.example.passion.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    // to get detail of user and post
    String myUid, myEmail, myName, myDp,
    postId, pLikes, hisDp, hisName;
    String hisuid;
    String pImage, uid;
    String pDescr;

    ProgressDialog pd;
    boolean mProcessComment = false;
    boolean mProcessLike = false;


    ImageView uPictureIv, pImageIv;
    TextView unameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv, pCommentsTv;
    ImageButton moreBtn;
    ImageView likeBtn, shareBtn;
    RelativeLayout profileLayout;
    RecyclerView recyclerView;

    List<ModelComments> commentsList;
    AdapterComments adapterComments;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String pTitle;

    //add comment views

    EditText commentEt;
    ImageButton sendBtn;
    ImageView cAvatarIv;

    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //Action bar and its properties
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // get id of post using intent
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");


        uPictureIv = findViewById(R.id.uPictureIv);
        pImageIv = findViewById(R.id.pImageIv);
        unameTv = findViewById(R.id.uNameTv);
        pTimeTv = findViewById(R.id.pTimeTv);
        pTitleTv = findViewById(R.id.pTitleTv);
        pDescriptionTv = findViewById(R.id.pDescriptionTv);
        pLikesTv = findViewById(R.id.pLikesTv);
        pCommentsTv = findViewById(R.id.pCommentsTv);
        moreBtn = findViewById(R.id.moreBtn);
        likeBtn = findViewById(R.id.likeBtn);
        shareBtn = findViewById(R.id.shareBtn);
        profileLayout = findViewById(R.id.commentLayout);

        recyclerView = findViewById(R.id.recyclerView_postdetali);

        commentEt = findViewById(R.id.commentEt);
        sendBtn = findViewById(R.id.sendBtn);
        cAvatarIv = findViewById(R.id.cAvatarIv);

        loadPostInfo();

        checkUserStatus();

        loadUserInfo();

        setLikes();

        // set subtitle of actionbar
//        actionBar.setSubtitle("Signed In: " +myEmail);


        loadComments();

        //send commment button click
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        //  like button click handle
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();
            }
        });

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) pImageIv.getDrawable();
                if (bitmapDrawable == null) {
                    // post without image
                    shareTextOnly();
                } else {
                    // post with image
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            uid = mCurrentUser.getUid();
        }

//        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
//        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Glide.with(PostDetailActivity.this).load(currentUser.getPhotoUrl()).into(cAvatarIv);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void loadComments() {
        // layout (linear) for recyclerview

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        // set layout to recyclerview

        recyclerView.setLayoutManager(linearLayoutManager);

        // init comment list
        commentsList = new ArrayList<>();

        // path of the post, to get it's comments

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelComments modelComments = ds.getValue(ModelComments.class);

                    commentsList.add(modelComments);

                    // setudp adapter
                    adapterComments = new AdapterComments(getApplicationContext(), commentsList, myUid, postId);

                    //set adapter
                    recyclerView.setAdapter(adapterComments);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PostDetailActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void shareImageAndText() {
        // concatenate title and description to share
        String sharebody = pTitle + "\n" + pDescr;

        // first we will save this image in cache, get the saved image uri
        BitmapDrawable bitmapDrawable = (BitmapDrawable) pImageIv.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Uri uri = saveImageToShare(bitmap);

        // share intent
        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        sIntent.setType("image/png");
        startActivity(Intent.createChooser(sIntent, "Share Via"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try{
            imageFolder.mkdirs(); // create if not exists
            File file = new File (imageFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.example.passion.fileprovider", file);

        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private void shareTextOnly() {
        // concatenate title and description to share
        String sharebody = pTitle + "\n" + pDescr;

        //Share intent
        Intent sintent = new Intent(Intent.ACTION_SEND);
        sintent.setType("text/plain");
        sintent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here"); // incase you share vai an email app
        sintent.putExtra(Intent.EXTRA_TEXT, sharebody); // text to share
        startActivity(Intent.createChooser(sintent, "Share Via")); // message to show in the share dialog
    }

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("Warning!");
                    builder.setMessage("Are you sure you want to delete this post? \nYou can't undo this action");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bedginDelete();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("Notice!");
                    builder.setMessage("Are you sure you want to send report?");
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(PostDetailActivity.this, "Report Sent Successfully", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(PostDetailActivity.this, Createpost.class);
                    intent.putExtra("Key", "editPost");
                    intent.putExtra("editPostId", postId);
                    startActivity(intent);
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void bedginDelete() {
        if (pImage.equals("No image")){
            // post is without image
            deleteWithoutImage();
        }
        else {
            // post is with image
            deleteWithImage();
        }
    }

    private void deleteWithImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Deleting...");

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // image deleted, now delete database

                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
                        fquery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue(); // remove vvaluse from the firebase where pid matches
                                }
                                // deleted
                                Toast.makeText(PostDetailActivity.this, "Deleted Sucessfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed can't go further
                        pd.dismiss();
                        Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteWithoutImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Deleting...");

        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue(); // remove vvaluse from the firebase where pid matches
                }
                // deleted
                pd.dismiss();
                Toast.makeText(PostDetailActivity.this, "Deleted Sucessfully", Toast.LENGTH_SHORT).show();


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
        final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcessLike){
                    if (dataSnapshot.child(postId).hasChild(myUid)){
                        // already liked, so remove like
                        postRef.child(postId).child("pLikes").setValue(""+(Integer.parseInt(pLikes)-1));
                        likesRef.child(postId).child(myUid).removeValue();
                        mProcessLike = false;

                    }
                    else {
                        // not liked, like it
                        postRef.child(postId).child("pLikes").setValue(""+(Integer.parseInt(pLikes)+1));
                        likesRef.child(postId).child(myUid).setValue("Liked"); // set any value
                        mProcessLike = false;

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            Toast.makeText(this, "Comment is empty...", Toast.LENGTH_SHORT).show();
            return;
        }
        String timeStamp = String.valueOf(System.currentTimeMillis());

//        each post will have a child "comments" that will comtain comments of that post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");

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
                        Toast.makeText(PostDetailActivity.this, "Comment Added", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void updateCommentCount() {
        // whenever user adds comment, increase the comment count as we did for like count
        mProcessComment = true;
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
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
                        // if image is received then seet
                        Picasso.get().load(xxx).placeholder(R.drawable.ic_face_black_24dp).into(cAvatarIv);
                    }
                    catch (Exception e){
                        Picasso.get().load(xxx).into(cAvatarIv);
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
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
                    String commentCount = ""+ ds.child("pComments").getValue();

                    // convert timestamp to proper format

                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
                    final String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

                    // set data
                    pTitleTv.setText(pTitle);
                    pDescriptionTv.setText(pDescr);
                    pLikesTv.setText(pLikes + " Likes");
                    pTimeTv.setText(pTime);
                    pCommentsTv.setText(commentCount + " COMMENTS");

                    actionBar.setTitle("" +pTitle);

                    unameTv.setText(hisName);
                    // set the image of the user who visited the post

                    if (pImage.equals("No image")){
                        //hide imageview
                        pImageIv.setVisibility(View.GONE);
                    }
                    else {
                        // show imageview
                        pImageIv.setVisibility(View.VISIBLE);

                        try{
                            // TODO: need to import picaso library
                            Picasso.get().load(pImage).into(pImageIv);
                        }
                        catch (Exception e){
                        }
                    }

                    //set user image in comment part
                    try {
                        Picasso.get().load(hisDp).placeholder(R.drawable.profile).into(uPictureIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.profile).into(uPictureIv);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.postdetail_menu, menu);
        // hide some menu items
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings){
            Intent myintent = new Intent(this,
                    SettingsActivity.class);
            startActivity(myintent);
        }


        return super.onOptionsItemSelected(item);
    }
}
