package com.example.passion.adapters;

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
import com.example.passion.R;
import com.example.passion.models.ModelUsers;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyHolder> {

    Context context;
    List<ModelUsers> userList;

    public AdapterUser(Context context, List<ModelUsers> userList) {
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
        final String hisUid=userList.get(position).getUid();
        String userImage=userList.get(position).getImage();
        String username=userList.get(position).getName();
        final String userEmail=userList.get(position).getEmail();

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
//                Toast.makeText(context, ""+userEmail, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid",hisUid);
                context.startActivity(intent);
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
