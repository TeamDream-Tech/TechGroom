package com.example.passion.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passion.ChatActivity;
import com.example.passion.CreateQuestion;
import com.example.passion.R;
import com.example.passion.models.ModelUsers;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterOpponent extends RecyclerView.Adapter<AdapterOpponent.MyHolder> {

    Context context;
    List<ModelUsers> userList;
    ProgressDialog pd;

    String hisUid;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String uid;

    public AdapterOpponent(Context context, List<ModelUsers> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users,parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        hisUid=userList.get(position).getUid();
        String userImage=userList.get(position).getImage();
        String username=userList.get(position).getName();
        final String userEmail=userList.get(position).getEmail();

        pd = new ProgressDialog(context);

        holder.mEmailTv.setText(userEmail);
        holder.mNameTv.setText(username);

        try{
            Picasso.get().load(userImage).placeholder(R.drawable.ic_face_black_24dp).into(holder.mAvaterIv);
        }catch (Exception e){
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+userEmail, Toast.LENGTH_SHORT).show();
                uploadData();
//                Intent intent = new Intent(context, ChatActivity.class);
//                intent.putExtra("hisUid",hisUid);
//                context.startActivity(intent);
            }
        });

    }

    private void uploadData() {
        pd.setMessage("Starting Challenge");
        pd.show();

        final String timeStamp = String.valueOf(System.currentTimeMillis());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            uid = mCurrentUser.getUid();
        }


        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("sender", uid);
        hashMap.put("receiver", hisUid);
        hashMap.put("cId", timeStamp);
        hashMap.put("PTime", timeStamp);
        hashMap.put("senderscore", "0");
        hashMap.put("receiverscore", "0");
        hashMap.put("questions", "");
        hashMap.put("options", "");
        hashMap.put("answers", "");
        hashMap.put("subject", "");
        hashMap.put("status", "ongoing");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Challenge");
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(context, "Challenge created successfully", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvaterIv;
        TextView mNameTv,mEmailTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mAvaterIv = itemView.findViewById(R.id.avatariv);
            mNameTv= itemView.findViewById(R.id.nametv);
            mEmailTv=itemView.findViewById(R.id.emailtv);
        }
    }
}
