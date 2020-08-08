package com.example.passion.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.passion.ActivityMain;
import com.example.passion.Createpost;
import com.example.passion.PostDetailActivity;
import com.example.passion.R;
import com.example.passion.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder>{

    Context context;
    List<ModelPost> postList;

    private ArrayList<ArrayList<Integer>> mDataList;
    private SparseIntArray positionList = new SparseIntArray();

    String myUid;

    private DatabaseReference likesRef; // for likes database node
    private DatabaseReference postRef; // reference of posts

    boolean mProcessLike = false;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        //get data
        final String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        final String pId = postList.get(position).getpId();
        final String pTitle = postList.get(position).getpTitle();
        final String PDescription = postList.get(position).getPDescription();
        final String PImage = postList.get(position).getPImage();
        String pTimeStamp = postList.get(position).getPTime();
        String pLikes = postList.get(position).getpLikes(); // contains total number of likes for a post
        String pComments = postList.get(position).getpComments();  // contains total number of comments for a post


        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        final String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();



        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(PDescription);
        holder.pLikesTv.setText(pLikes);
        holder.pCommentsTv.setText(pComments);

        //set likes for each post
        setLikes(holder, pId);

        //set user dp
        try{
            // TODO: need to import picaso library

            Picasso.get().load(uDp).placeholder(R.drawable.profile).into(holder.uPictureiV);
            }
        catch (Exception e){

        }

        if (PImage.equals("No image")){
            //hide imageview
            holder.PImageIv.setVisibility(View.GONE);
        }
        else {
            // show imageview
            holder.PImageIv.setVisibility(View.VISIBLE); // make sure to correcte this

            try{
                // TODO: need to import picaso library
                Picasso.get().load(PImage).into(holder.PImageIv);
            }
            catch (Exception e){
            }
        }
        //set post image

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showMoreOptions(holder.moreBtn, uid, myUid, pId, PImage);
            }
        });
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pLikes = Integer.parseInt(postList.get(position).getpLikes());
                mProcessLike = true;
                //get id of the post clicked
                final String postIde = postList.get(position).getpId();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (mProcessLike){
                            if (dataSnapshot.child(postIde).hasChild(myUid)){
                                // already liked, so remove like
                                postRef.child(postIde).child("pLikes").setValue(""+(pLikes-1));
                                likesRef.child(postIde).child(myUid).removeValue();
                                mProcessLike = false;
                            }
                            else {
                                // not liked, like it
                                postRef.child(postIde).child("pLikes").setValue(""+(pLikes+1));
                                likesRef.child(postIde).child(myUid).setValue("Liked"); // set any value
                                mProcessLike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable)holder.PImageIv.getDrawable();
                if (bitmapDrawable == null){
                    // post without image
                    shareTextOnly(pTitle, PDescription);
                }else {
                    // post with image
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(pTitle, PDescription, bitmap);
                }
            }
        });holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Post DetailActivity
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId); // will get detail of post using this id, its of the post clicked
                context.startActivity(intent);

            }
        });
        holder.PImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Post DetailActivity
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId); // will get detail of post using this id, its of the post clicked
                context.startActivity(intent);
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId); // will get detail of post using this id, its of the post clicked
                context.startActivity(intent);
            }
        });
    }

    private void shareTextOnly(String pTitle, String pDescription) {
        // concatenate title and description to share
        String sharebody = pTitle + "\n" + pDescription;

        //Share intent
        Intent sintent = new Intent(Intent.ACTION_SEND);
        sintent.setType("text/plain");
        sintent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here"); // incase you share vai an email app
        sintent.putExtra(Intent.EXTRA_TEXT, sharebody); // text to share
        context.startActivity(Intent.createChooser(sintent, "Share Via")); // message to show in the share dialog
    }

    private void shareImageAndText(String pTitle, String pDescription, Bitmap bitmap) {
        // concatenate title and description to share
        String sharebody = pTitle + "\n" + pDescription;

        // first we will save this image in cache, get the saved image uri
        Uri uri = saveImageToShare(bitmap);

        // share intent
        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        sIntent.setType("image/png");
        context.startActivity(Intent.createChooser(sIntent, "Share Via"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try{
            imageFolder.mkdirs(); // create if not exists
            File file = new File (imageFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.example.passion.fileprovider", file);

        }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private void setLikes(final MyHolder holder, final String postKey) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postKey).hasChild(myUid)){
                    // user has liked this post
                    /*To indicate that the post is liked by the signed in user
                    change drawable left icon of like button
                    change text of the like button from "like" to "Liked"
                     */
                    holder.likeBtn.setImageResource(R.drawable.ic_liked);

                }
                else {
                    // user has not liked this post
                     /*To indicate that the post is not liked by the signed in user
                    change drawable left icon of like button
                    change text of the like button from "liked" to "Like"
                     */
                    holder.likeBtn.setImageResource(R.drawable.ic_thumb_up);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    @Override
//    public void onViewRecycled(@NonNull MyHolder holder) {
//        // Store position
//        final int position = holder.getAdapterPosition();
//        int firstVisiblePosition = holder..findFirstVisibleItemPosition();
//        positionList.put(position, firstVisiblePosition);
//        super.onViewRecycled(holder);
//    }

    private void showMoreOptions(ImageButton moreBtn, String uid, String myUid, final String pId, final String pImage) {
        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);

        if (uid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            //<------------------ for edit option ----------------------------->//
//            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Edit");
            //<------------------ for edit option ----------------------------->//
        }

        popupMenu.getMenu().add(Menu.NONE, 2, 0, "Report");
        popupMenu.getMenu().add(Menu.NONE, 3, 0, "View Detail");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                final int id = item.getItemId();
                if (id == 0){
                    // delete is clicked
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true);
                    builder.setTitle("Warning!");
                    builder.setMessage("Are you sure you want to delete this post? \nYou can't undo this action");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bedginDelete(pId, pImage);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true);
                    builder.setTitle("Notice!");
                    builder.setMessage("Are you sure you want to send report?");
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(context, "Report Sent Successfully", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(context, Createpost.class);
                    intent.putExtra("Key", "editPost");
                    intent.putExtra("editPostId", pId);
                    context.startActivity(intent);
                }
                else if (id == 3){
                    // Start Post DetailActivity
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postId", pId); // will get detail of post using this id, its of the post clicked
                    context.startActivity(intent);
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void bedginDelete(String pId, String pImage) {
        if (pImage.equals("No image")){
            // post is without image
            deleteWithoutImage(pId);


        }
        else {
            // post is with image
            deleteWithImage(pId, pImage);
        }
    }

    private void deleteWithImage(final String pId, String pImage) {
        // Progress bar
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // image deleted, now delete database

                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                        fquery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue(); // remove vvaluse from the firebase where pid matches
                                }
                                // deleted
                                Toast.makeText(context, "Deleted Sucessfully", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void deleteWithoutImage(String pId) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue(); // remove vvaluse from the firebase where pid matches
                }
                // deleted
                pd.dismiss();
                Toast.makeText(context, "Deleted Sucessfully", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView uPictureiV;
        ImageView PImageIv;
        TextView uNameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv, pCommentsTv;
        ImageView likeBtn, commentBtn, shareBtn;
        ImageButton moreBtn;
        CardView cardView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //Image Views
            uPictureiV = itemView.findViewById(R.id.uPictureIv);
            PImageIv = itemView.findViewById(R.id.pImageIv);
            cardView = itemView.findViewById(R.id.click);

            //Text views
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pCommentsTv = itemView.findViewById(R.id.pCommentsTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);

            //buttons
            moreBtn = itemView.findViewById(R.id.moreBtn);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);

        }
    }
}
