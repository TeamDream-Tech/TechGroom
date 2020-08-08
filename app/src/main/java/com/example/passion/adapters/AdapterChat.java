package com.example.passion.adapters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;
import androidx.transition.TransitionManager;

import com.example.passion.ChatActivity;
import com.example.passion.Createpost;
import com.example.passion.OnSwipeTouchListener;
import com.example.passion.PostDetailActivity;
import com.example.passion.R;
import com.example.passion.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.CLIPBOARD_SERVICE;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.Myholder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    FirebaseUser fuser;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    String uid;
    String hisuid;

    public AdapterChat(Context context, List<ModelChat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflate layout row_chat_left.xml for receiver, row_chat_right.xml for sender
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new Myholder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new Myholder(view);
        }
        //inside oncreate

    }

    @Override
    public void onBindViewHolder(@NonNull final Myholder holder, final int position) {
        //get data
        final String message = chatList.get(position).getMessage();
        final String timeStamp = chatList.get(position).getTimestamp();
        String result = chatList.get(position).getResult();
        String reply = chatList.get(position).getReply();
        final String sender = chatList.get(position).getSender();
        final String receiver = chatList.get(position).getReceiver();
        String whoreply = chatList.get(position).getWhoreply();

        //convert time stamp to hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("hh:mm aa", cal).toString();

        // set date
        holder.messageTv.setText(message);
        holder.timeTv.setText(dateTime);
        final String FileName = "myFile";

//        holder.show.setOnTouchListener(new OnSwipeTouchListener(context) {
//            public void onSwipeRight() {
//                Toast.makeText(context, "right", Toast.LENGTH_SHORT).show();
//            }
//            public void onSwipeLeft() {
//                Toast.makeText(context, "left", Toast.LENGTH_SHORT).show();
//            }
//        });
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            uid = mCurrentUser.getUid();
        }

        holder.show.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.messageTv, Gravity.END);

                popupMenu.getMenu().add(Menu.NONE, 2, 0, "Reply");
                if (sender.equals(uid)){
                    popupMenu.getMenu().add(Menu.NONE, 3, 0, "Delete");
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                        final int id = item.getItemId();
                        if (id == 2){
                            myClipboard = (ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE);
                            String text;
                            text = message;
                            myClip = ClipData.newPlainText("text", text);
                            myClipboard.setPrimaryClip(myClip);


                            SharedPreferences sharedPreferences = context.getSharedPreferences(FileName, Context.MODE_PRIVATE);
                            final SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("name", sender);

                            editor.commit();

                            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
                            databaseReference.child(sender).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String name = String.valueOf(dataSnapshot.child("name").getValue());


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else if (id == 3){
                            // Start deleting message
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setCancelable(true);
                            builder.setTitle("Delete!");
                            builder.setMessage("Are you sure you want to Delete this message?");
                            builder.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteMessage(position);
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
                        return false;
                    }
                });

                popupMenu.show();
                return false;
            }
        });

        if (result.equals("seen")) {
            holder.check1.setImageResource(R.drawable.ic_done_all_green);
        }
        else if (result.equals("delivered")){
            holder.check1.setImageResource(R.drawable.ic_done_all_black);
        }
        else {
            holder.check1.setImageResource(R.drawable.ic_error_outline);
        }

        if (!reply.isEmpty()){
            holder.replyview.setVisibility(View.VISIBLE);
            holder.replymessageTv.setText(reply);
        }
        else {
            holder.replyview.setVisibility(View.GONE);
            holder.replymessageTv.setText("");
        }
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(whoreply).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = String.valueOf(dataSnapshot.child("name").getValue());
                holder.who.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void deleteMessage(int position) {
        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String msgTimeStamp = chatList.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    if (ds.child("sender").getValue().equals(myUID)){

                        // use this if you want to remove everything
                        ds.getRef().removeValue();

//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("message", "This message was deleted...");
//                        ds.getRef().updateChildren(hashMap);

                        Toast.makeText(context, "message deleted", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ds.getRef().removeValue();

//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("message", "This message was deleted...");
//                        ds.getRef().updateChildren(hashMap);

                        Toast.makeText(context, "message deleted", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // get currently signed in user
            fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    // view holder class
    class Myholder extends RecyclerView.ViewHolder{
        // views
        TextView messageTv, timeTv;
        ImageView check1;
        LinearLayout show, status;
        RelativeLayout replyview;
        LinearLayout messageLayout;
        TextView replymessageTv, who;

        public Myholder(@NonNull View itemView) {
            super(itemView);

            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timesentTv);
            check1 = itemView.findViewById(R.id.check1);
            show = itemView.findViewById(R.id.show);

            status = itemView.findViewById(R.id.status);
            replymessageTv = itemView.findViewById(R.id.replymessageTv);
            replyview = itemView.findViewById(R.id.replyview);
            who = itemView.findViewById(R.id.who);
            messageLayout = itemView.findViewById(R.id.messageLayout);

        }
    }
}
