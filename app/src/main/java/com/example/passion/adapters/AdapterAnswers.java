package com.example.passion.adapters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passion.R;
import com.example.passion.models.ModelComments;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.CLIPBOARD_SERVICE;

public class AdapterAnswers extends RecyclerView.Adapter<AdapterAnswers.MyHolder> {

    Context context;
    List<ModelComments> commentsList;
    String myUid, postId;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String mainuid;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    AlertDialog alertDialog1;
    CharSequence[] values = {" First Item "," Second Item "," Third Item "};

    public AdapterAnswers(Context context, List<ModelComments> commentsList, String myUid, String postId) {
        this.context = context;
        this.commentsList = commentsList;
        this.myUid = myUid;
        this.postId = postId;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // bind the row_comment.xml layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int i) {
        // get the data
        final String uid = commentsList.get(i).getUid();
        String name = commentsList.get(i).getuName();
        String email = commentsList.get(i).getuEmail();
        String image = commentsList.get(i).getuDp();
        final String cid = commentsList.get(i).getcId();
        final String comment = commentsList.get(i).getComment();
        String timestamp = commentsList.get(i).getTimestamp();


        // convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        final String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();


        // set the data
        holder.nameTv.setText(name);
        holder.commentTv.setText(comment);
        holder.timeTv.setText(pTime);

        // set user dp

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Uri mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        String xxx = mCurrentUser.toString();
        if (mCurrentUser != null) {

        }

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_face_black_24dp).into(holder.avatarIv);
        }catch (Exception e){

        }

        // comment click listener

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                // check if this comment is by currently signed in user or not
                PopupMenu popupMenu = new PopupMenu(context, holder.moreBtn, Gravity.END);

                if (uid.equals(myUid)){
                    popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
                }
                popupMenu.getMenu().add(Menu.NONE, 1, 0, "Copy Text");
                popupMenu.getMenu().add(Menu.NONE, 2, 0, "Report");

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                        final int id = item.getItemId();
                        if (id == 0){
                            if (myUid.equals(uid)){
                                // my comment

                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                                builder.setTitle("Delete");
                                builder.setMessage("Are you sure you want to delete this comment");
                                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // delete comment
                                        deleteComment(cid);
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // dismiss dialog
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                            }else {
                                // not my comment
                                Toast.makeText(context, "Cant't delete this comment", Toast.LENGTH_SHORT).show();
                            }

                        } else if (id == 1){
                            myClipboard = (ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE);
                            String text;
                            text = comment;
                            myClip = ClipData.newPlainText("text", text);
                            myClipboard.setPrimaryClip(myClip);
                        }
                        else if (id == 2){
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getRootView().getContext());
                            alertDialog.setTitle("Why are you reporting this?");
                            String[] items = {"Comment is inappropriate","This account may have been hacked","It's spam","Other reasons"};
                            final int checkedItem = 1;
                            alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            Toast.makeText(context, "First Option clicked", Toast.LENGTH_LONG).show();
                                            break;
                                        case 1:
                                            Toast.makeText(context, "Second Option clicked", Toast.LENGTH_LONG).show();
                                            break;
                                        case 2:
                                            Toast.makeText(context, "Third Option clicked", Toast.LENGTH_LONG).show();
                                            break;
                                        case 3:
                                            Toast.makeText(context, "Fourth Option clicked", Toast.LENGTH_LONG).show();
                                            break;
                                    }
                                }
                            });
                            alertDialog.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // delete comment
                                    Toast.makeText(context, checkedItem+"Report Sent", Toast.LENGTH_SHORT).show();
                                }
                            });
                            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // dismiss dialog
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = alertDialog.create();
                            alert.setCanceledOnTouchOutside(true);
                            alert.show();
                        }
                        return false;
                    }
                });

                popupMenu.show();

            }
        });



    }
    private void showAlertDialog() {

    }

    private void deleteComment(String cid) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Questions").child(postId);
        ref.child("Comments").child(cid).removeValue(); // it will delete the comment

        // now update the comment count
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String comments = ""+ dataSnapshot.child("pComments").getValue();
                int newCommentVal= Integer.parseInt(comments) - 1;
                ref.child("pComments").setValue("" +newCommentVal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        // declare views from row_comment.xml
        ImageView avatarIv;
        TextView nameTv, commentTv, timeTv;
        ImageView moreBtn;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            commentTv = itemView.findViewById(R.id.commentTv);
            timeTv = itemView.findViewById(R.id.timeTv);

            moreBtn = itemView.findViewById(R.id.moredeletBtn);

        }
    }
}
