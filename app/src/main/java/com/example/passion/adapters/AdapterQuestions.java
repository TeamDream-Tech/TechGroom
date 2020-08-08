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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passion.QuestionDetail;
import com.example.passion.R;
import com.example.passion.models.ModelQuestions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

public class AdapterQuestions extends RecyclerView.Adapter<AdapterQuestions.MyHolder>{

    Context context;
    List<ModelQuestions> questionList;

    String myUid;

    private DatabaseReference likesRef; // for likes database node
    private DatabaseReference postRef;

    boolean mProcessviews = false;

    public AdapterQuestions(Context context, List<ModelQuestions> questionList) {
        this.context = context;
        this.questionList = questionList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("views");
        postRef = FirebaseDatabase.getInstance().getReference().child("Questions");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // inflate layout row_questions.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_questions, parent, false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int i) {

        // here we get data
        String uid = questionList.get(i).getUid();
        String likes = questionList.get(i).getpLikes();
        String comments = questionList.get(i).getpComments();
        String time = questionList.get(i).getPTime();
        String title = questionList.get(i).getpTitle();
        String email = questionList.get(i).getuEmail();
        final String udp = questionList.get(i).getuDp();
        final String pId = questionList.get(i).getpId();
        final String views = questionList.get(i).getViews();
//        String a = questionList.get(i).getUid();
//        String a = questionList.get(i).getUid();

        // convert timestamp to real date format
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(time));
        final String pTime = DateFormat.format("hh:mm aa", calendar).toString();

        holder.pTitle.setText(title);
        holder.time.setText(pTime);
        holder.likes.setText("+"+likes);
        holder.answers.setText("+"+comments);
        holder.views.setText(views);

        try {
            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
            databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = String.valueOf(dataSnapshot.child("name").getValue());
                    String email = String.valueOf(dataSnapshot.child("email").getValue());

                    holder.uNameTv.setText(name);
                    Picasso.get().load(udp).placeholder(R.drawable.ic_face_black_24dp).into(holder.uPictureIv);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
        catch (Exception e){
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuestionDetail.class);
                intent.putExtra("postId", pId); // will get detail of post using this id, its of the post clicked
                context.startActivity(intent);


                mProcessviews = true;
                //get id of the post clicked

                final int pViews = Integer.parseInt(questionList.get(i).getViews());

                //get id of the post clicked
                final String postIde = questionList.get(i).getpId();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (mProcessviews){
                            if (!dataSnapshot.child(postIde).hasChild(myUid)){
                                // not liked, like it
                                postRef.child(postIde).child("views").setValue(""+(pViews+1));
                                likesRef.child(postIde).child(myUid).setValue("viewed"); // set any value
                                mProcessviews = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        // views from row_questions
        ImageView uPictureIv;
        TextView uNameTv, likes, answers, time, pTitle, pDescription, tags, views;
        RelativeLayout relativeLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            uPictureIv = itemView.findViewById(R.id.quesdp);
            uNameTv = itemView.findViewById(R.id.profname);
            likes = itemView.findViewById(R.id.likes);
            answers = itemView.findViewById(R.id.allanswers);
            pTitle = itemView.findViewById(R.id.questitle);
            time = itemView.findViewById(R.id.quesdateasked);
            views = itemView.findViewById(R.id.quesviews);

            relativeLayout = itemView.findViewById(R.id.todetail);
        }
    }
}
