package com.example.passion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static com.example.passion.ActivityMain.USER_PREF;

public class Createpost extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    String downloadlocation;

    static int PReqCode = 1;
    static int REQUESCODE = 1;

    String[] cameraPermissions;
    String[] storagePermissions;
    public String uri;
    private EditText textTitle;
    private EditText textDesc;
    private Button postBtn;
    private StorageReference storage;
    private FirebaseDatabase database;
    private DatabaseReference userDbRef;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrentUser;
    Uri image_rui = null;

    String editTitle, editDescription, editImage;

    String name, email, uid, dp;
    ProgressDialog pd;

    String names;
    ImageView imageIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpost);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        postBtn = (Button) findViewById(R.id.postBtn);
        textDesc = (EditText) findViewById(R.id.textDesc);
        textTitle = (EditText) findViewById(R.id.textTitle);
        imageIv = findViewById(R.id.imageFp);

        // get data through intent from previous activitie's adapter
        Intent intent = getIntent();
        final String isUpdateKey = ""+intent.getStringExtra("Key");
        final String editPostId = ""+intent.getStringExtra("editPostId");

        //validate if we came here to update post i.e came from Adapter Post
        if (isUpdateKey.equals("editPostId")){
            //update

            postBtn.setText("Update");
            loadPostData(editPostId);
        }
        else {

            postBtn.setText("Upload");
        }


        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDbRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    email = ""+ ds.child("email").getValue();
                    dp = ""+ ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                names = String.valueOf(dataSnapshot.child("name").getValue());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        imageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });


        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = textTitle.getText().toString().trim();
                String description = textDesc.getText().toString().trim();
                if (isNetworkAvailable() == true){
                    if (TextUtils.isEmpty(title)){
                        Toast.makeText(Createpost.this, "Enter Title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(description)){
                        Toast.makeText(Createpost.this, "Enter Description", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isUpdateKey.equals("editPost")){
                        beginUpdate(title, description, editPostId);
                    }
                    else {
                        uploadData(title, description);
                    }
                }
                else {
                    Snackbar.make(v, "Error, Please check Internet connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }



            }
        });

    }
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void beginUpdate(String title, String description, String editPostId) {

        pd.setMessage("Updating Post...");
        pd.show();

        if (!editImage.contains("image")){
            // with image
            updateWtihImage(title, description, editPostId);
            finish();
        }
        else if (imageIv.getDrawable() != null){
            // without image
            updateWithoutImage(title, description, editPostId);
            finish();
        }
        else {
            // without image
            updateNowWithoutImage(title, description, editPostId);
            finish();
        }
    }


    private void updateNowWithoutImage(String title, String description, String editPostId) {
        HashMap<String, Object> hashMap = new HashMap<>();

        Uri xxx = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        String pic = xxx.toString();

        hashMap.put("uid", uid);
        hashMap.put("uName", names);
        hashMap.put("uEmail", email);
        hashMap.put("uDp", pic);
        hashMap.put("pTitle", title);
        hashMap.put("PDescription", description);
        hashMap.put("PImage", "No image");
        hashMap.put("pLikes", "0");
        hashMap.put("pComments", "0");


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.child(editPostId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(Createpost.this, "Updated...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Createpost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWithoutImage(final String title, final String description, final String editPostId) {
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        String filepathandname = "Posts/"+ "post_"+ timeStamp;


        Bitmap bitmap = ((BitmapDrawable)imageIv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // image compress
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        //please check
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filepathandname);
        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image uploaded get its url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        //getting profile picture since his own dosen't work for me
                        Uri xxx = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
                        String pic = xxx.toString();

                        String downloadUri = uriTask.getResult().toString();
                        if (uriTask.isSuccessful()){
                            // uri is received, upload to firebase database
                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("uid", uid);
                            hashMap.put("uName", names);
                            hashMap.put("uEmail", email);
                            hashMap.put("uDp", pic);
                            hashMap.put("pTitle", title);
                            hashMap.put("PDescription", description);
                            hashMap.put("PImage", downloadUri);
                            hashMap.put("pLikes", "0");
                            hashMap.put("pComments", "0");


                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                            ref.child(editPostId)
                                    .updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(Createpost.this, "Updated...", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(Createpost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // image not uploaded
                pd.dismiss();
                Toast.makeText(Createpost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWtihImage(final String title, final String description, final String editPostId) {
        // post is with image, delete previous image first
        StorageReference myPictureRef = FirebaseStorage.getInstance().getReferenceFromUrl(editImage);

        myPictureRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // image deleted, upload new image
                        // for post-image, name, postid, publish time
                        final String timeStamp = String.valueOf(System.currentTimeMillis());
                        String filepathandname = "Posts/"+ "post_"+ timeStamp;


                        Bitmap bitmap = ((BitmapDrawable)imageIv.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        // image compress
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] data = baos.toByteArray();

                        //please check
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filepathandname);
                        ref.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //image uploaded get its url
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isSuccessful());

                                        //getting profile picture since his own dosen't work for me
                                        Uri xxx = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
                                        String pic = xxx.toString();

                                        String downloadUri = uriTask.getResult().toString();
                                        if (uriTask.isSuccessful()){
                                            // uri is received, upload to firebase database
                                            HashMap<String, Object> hashMap = new HashMap<>();

                                            hashMap.put("uid", uid);
                                            hashMap.put("uName", names);
                                            hashMap.put("uEmail", email);
                                            hashMap.put("uDp", pic);
                                            hashMap.put("pTitle", title);
                                            hashMap.put("PDescription", description);
                                            hashMap.put("PImage", downloadUri);
                                            hashMap.put("pLikes", "0");
                                            hashMap.put("pComments", "0");


                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                            ref.child(editPostId)
                                                    .updateChildren(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            pd.dismiss();
                                                            Toast.makeText(Createpost.this, "Updated...", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            pd.dismiss();
                                                            Toast.makeText(Createpost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // image not uploaded
                                        pd.dismiss();
                                        Toast.makeText(Createpost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(Createpost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadPostData(String editPostId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        //get detail of post using id of post
        Query fquery = reference.orderByChild("pId").equalTo(editPostId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    editTitle = ""+ds.child("pTitle").getValue();
                    editDescription = ""+ds.child("PDescription").getValue();
                    editImage = ""+ds.child("PImage").getValue();

                    // set data to views
                    textTitle.setText(editTitle);
                    textDesc.setText(editDescription);

                    // set image

                    if (!editImage.equals("No image")){
                        try {
                            Picasso.get().load(editImage).into(imageIv);
                        }
                        catch (Exception e){

                        }
                    }else {
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void uploadData(final String title, final String description) {
        pd.setMessage("Uploading Content...");
        pd.show();

        final String timeStamp = String.valueOf(System.currentTimeMillis());
        String filepathAndName = "Posts/" + "post_" + timeStamp;

        Uri xx = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        String pic = xx.toString();


        if (!(image_rui == null)){
            Bitmap bitmap = ((BitmapDrawable)imageIv.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // image compress
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            //post with image
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filepathAndName);
            final StorageReference imageFilePath = ref.child(image_rui.getLastPathSegment());
            imageFilePath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri location = uri;
                            downloadlocation = uri.toString();

                            Uri xx = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
                            String pic = xx.toString();
                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("uid", uid);
                            hashMap.put("uName", names);
                            hashMap.put("uEmail", email);
                            hashMap.put("uDp", pic);
                            hashMap.put("pId", timeStamp);
                            hashMap.put("pTitle", title);
                            hashMap.put("PDescription", description);
                            hashMap.put("PImage", downloadlocation);
                            hashMap.put("PTime", timeStamp);
                            hashMap.put("pLikes", "0");
                            hashMap.put("pComments", "0");

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

                            ref.child(timeStamp).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(Createpost.this, "Picture Post Published", Toast.LENGTH_SHORT).show();
                                            textDesc.setText("");
                                            textTitle.setText("");
                                            imageIv.setImageURI(null);
                                            image_rui = null;
                                            textDesc.clearFocus();
                                            textTitle.clearFocus();
                                            finish();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Createpost.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                            pd.dismiss();
                                        }
                                    });
                        }
                    });



                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(Createpost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            // post without image
            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("uid", uid);
            hashMap.put("uName", names);
            hashMap.put("uEmail", email);
            hashMap.put("uDp", pic);
            hashMap.put("pId", timeStamp);
            hashMap.put("pTitle", title);
            hashMap.put("PDescription", description);
            hashMap.put("PImage", "No image");
            hashMap.put("PTime", timeStamp);
            hashMap.put("pLikes", "0");
            hashMap.put("pComments", "0");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(Createpost.this, "text Post Published", Toast.LENGTH_SHORT).show();
                            textDesc.setText("");
                            textTitle.setText("");
                            imageIv.setImageURI(null);
                            image_rui = null;
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Createpost.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                            pd.dismiss();
                        }
                    });

        }

    }

    private void showImagePickDialog() {
        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image from");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){

                    if (checkcamerapermission()){
                        pickFromCamera();
                    }
                    else {
                        requestcamerapermission();
                    }

                }
                if (which == 1){
                    if (checkstoragepermission()){
                        pickFromGallery();
                    }
                    else {
                        requeststoragepermission();
                    }
                }
            }
        });
        builder.create().show();
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(galleryIntent, "select picture"),REQUESCODE);
    }

    private void pickFromCamera() {

        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);



        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    private boolean checkstoragepermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requeststoragepermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkcamerapermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean resultl = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && resultl;
    }

    private void requestcamerapermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user is signed in stay here
            email = user.getEmail();
            uid = user.getUid();
        }else{

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }
        if (id == R.id.createpost){
            Toast.makeText(Createpost.this, "Post Discarded", Toast.LENGTH_SHORT).show();
            this.finish();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(Createpost.this, "Both Camera and Storage permissions are necessary", Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(Createpost.this, "Storage permission necessary", Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                }
            }
            break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESCODE && data != null) {
            image_rui = data.getData();
            imageIv.setImageURI(image_rui);
            Toast.makeText(this, "Picked image successfully", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == IMAGE_PICK_CAMERA_CODE){
            imageIv.setImageURI(image_rui);
        }
        else {
            Toast.makeText(this, "Error Loading image", Toast.LENGTH_SHORT).show();
        }
    }
}
