package com.example.passion.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passion.QuestionDetail;
import com.example.passion.R;
import com.example.passion.UpdateDetailActivity;
import com.example.passion.models.ModelHot;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterHot extends RecyclerView.Adapter<AdapterHot.MyHolder>{

    Context context;
    List<ModelHot> hotList;

    String myUid;

    private DatabaseReference likesRef; // for likes database node
    private DatabaseReference postRef;

    boolean mProcessviews = false;

    public AdapterHot(Context context, List<ModelHot> hotList) {
        this.context = context;
        this.hotList = hotList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("views");
        postRef = FirebaseDatabase.getInstance().getReference().child("Updates");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // inflate layout row_hot_layout.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_hot_layout, parent, false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int i) {

        // here we get data
        String uid = hotList.get(i).getUid();
        String description = hotList.get(i).getPDescription();
        String comments = hotList.get(i).getpComments();
        String time = hotList.get(i).getPTime();
        final String url = hotList.get(i).getPImage();
        String title = hotList.get(i).getpTitle();
        final String views = hotList.get(i).getViews();
        final String pId = hotList.get(i).getpId();

        // convert timestamp to real date format
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(time));
        final String pTime = DateFormat.format("hh:mm aa", calendar).toString();
        CharSequence realtime = DateUtils.getRelativeTimeSpanString(Long.parseLong(time),System.currentTimeMillis(),DateUtils.SECOND_IN_MILLIS);

        holder.pTitle.setText(title);
        holder.time.setText(realtime);
//        holder.likes.setText("+"+likes);
//        holder.answers.setText("+"+comments);
        holder.views.setText(views);
        holder.body.setText(description);

        try {
            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
            databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = String.valueOf(dataSnapshot.child("name").getValue());
                    String email = String.valueOf(dataSnapshot.child("email").getValue());

                    holder.uNameTv.setText(name);
                    Picasso.get().load(url).placeholder(R.drawable.ic_face_black_24dp).into(holder.uPictureIv);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
        catch (Exception e){
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Snackbar.make(v, ""+pId, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(context, UpdateDetailActivity.class);
                intent.putExtra("postId", pId); // will get detail of post using this id, its of the post clicked
                context.startActivity(intent);
                mProcessviews = true;
                //get id of the post clicked

//                final int pViews = Integer.parseInt(hotList.get(i).getViews());
//
//                //get id of the post clicked
//                final String postIde = hotList.get(i).getpId();
//                likesRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (mProcessviews){
//                            if (!dataSnapshot.child(postIde).hasChild(myUid)){
//                                // not liked, like it
//                                postRef.child(postIde).child("views").setValue(""+(pViews+1));
//                                likesRef.child(postIde).child(myUid).setValue("viewed"); // set any value
//                                mProcessviews = false;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return hotList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        // views from row_hot_layout
        ImageView uPictureIv, profile;
        TextView uNameTv,
//                likes,
//                answers,
        time, pTitle, pDescription, tags, views, body;
        CardView cardView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            uPictureIv = itemView.findViewById(R.id.hotimage);
            uNameTv = itemView.findViewById(R.id.hotname);
//            likes = itemView.findViewById(R.id.likes);
//            answers = itemView.findViewById(R.id.allanswers);
            pTitle = itemView.findViewById(R.id.hottitle);
            time = itemView.findViewById(R.id.hottime);
            views = itemView.findViewById(R.id.hotviews);
            body = itemView.findViewById(R.id.hotbody);
            profile = itemView.findViewById(R.id.hotprofile);
//
            cardView = itemView.findViewById(R.id.rowhotlayout);
        }
    }
}
