package com.example.passion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.passion.adapters.AdapterChat;
import com.example.passion.models.ModelChat;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.concurrent.TimeUnit;

import static android.view.Gravity.RIGHT;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView cnameTv, userStatusTv;
    EditText messageEt;
    ImageButton sendmessageBtn;

    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<ModelChat> chatList;
    AdapterChat adapterChat;

    // firbase auth
    FirebaseAuth firebaseAuth;
    String hisUid;
    String myUid;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDpRef;
    String reply, hold;
    TextView value;
    ClipboardManager clipBoard;
    ImageView statusimg;

    RelativeLayout replymessagelayout;
    TextView replylayoutmessage;
    ImageView replylayoutclose, attach;

    public String stringreplyname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // initialise views
        Toolbar toolbar = findViewById(R.id.toolbarchat);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        statusimg = findViewById(R.id.statusimg);
        recyclerView = findViewById(R.id.chat_recyclerView);
        profileIv = findViewById(R.id.profileIv);
        cnameTv = findViewById(R.id.cnameTv);
        userStatusTv = findViewById(R.id.userStatusTv);
        messageEt = findViewById(R.id.messageEt);
        sendmessageBtn = findViewById(R.id.sendmessageBtn);
        value = findViewById(R.id.value);
        attach = findViewById(R.id.attach);

        replymessagelayout = findViewById(R.id.replylayout);
        replylayoutmessage = findViewById(R.id.replylayoutmessage);
        replylayoutclose = findViewById(R.id.replylayoutclose);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        userDpRef = firebaseDatabase.getReference("Users");

        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    Toast.makeText(ChatActivity.this, "last message", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData clipData = clipBoard.getPrimaryClip();
                ClipData.Item item = clipData.getItemAt(0);
                String text = item.getText().toString();

                replymessagelayout.setVisibility(View.VISIBLE);
                replylayoutmessage.setText(text);
                hold = text;

                if (!isKeyboardShowing) {
                    isKeyboardShowing = true;
                    onKeyboardVisibilityChanged(true);
                    InputMethodManager imm = (InputMethodManager) ChatActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    messageEt.requestFocus();
                }

                // attach view or something to the edit text to show that the message was carried
                // Access your context here using YourActivityName.this
            }
        });
        hold = "";

        replylayoutclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replymessagelayout.setVisibility(View.GONE);
                hold = "";
            }
        });

        messageEt.setText("");
        messageEt.clearFocus();
        // getting the uid we passed from the chatlist activity to this activity so we can use the uid to get user details and display in the chat



        // search user to get that user's info
        Query userQuery = userDpRef.orderByChild("uid").equalTo(hisUid);
        // get user picture and name
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // check until required info is received
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    // get data
                    String name = ""+ ds.child("name").getValue();
                    String image = ""+ ds.child("image").getValue();
                    // get value of onlinestatus
                    String onlineStatus = ""+ ds.child("onlineStatus").getValue();

                    if (onlineStatus.equals("online")){
                        userStatusTv.setVisibility(View.VISIBLE);
                        userStatusTv.setText(onlineStatus);
                    }
                    else {
                        //convert time stamp to hh:mm am/pm
                        userStatusTv.setVisibility(View.VISIBLE);
                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(Long.parseLong(onlineStatus));
                        String dateTime = DateFormat.format("MMM-dd hh:mm aa", cal).toString();
                        userStatusTv.setText("Last seen: " +dateTime);
                    }
                    // set data
                    cnameTv.setText(name);

                    try {

                        Picasso.get().load(image).placeholder(R.drawable.ic_face_white).into(profileIv);
                        Picasso.get().load(image).placeholder(R.drawable.ic_face_white).into(statusimg);

                    }catch (Exception e){

                        Picasso.get().load(R.drawable.profile).into(profileIv);
                        Picasso.get().load(R.drawable.profile).into(statusimg);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        sendmessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get text from edittext
                String message = messageEt.getText().toString().trim();
                // check if text is empty or not
                if (TextUtils.isEmpty(message)){
                    // Text empty
                    messageEt.requestFocus();
                }
                else {
                    // text is not empty, send message
                    sendMessage(message);
                }
            }
        });

        readMessages();

        seenMessages();
    }

    private void seenMessages() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("result", "seen");
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }
                    // adapter
                    adapterChat = new AdapterChat(ChatActivity.this, chatList);
                    adapterChat.notifyDataSetChanged();

                    // set adapter
                    recyclerView.setAdapter(adapterChat);

                    recyclerView.scrollToPosition(chatList.size() - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        final String FileName = "myFile";
        SharedPreferences sharedPreferences = getSharedPreferences(FileName, Context.MODE_PRIVATE);
        String defaultValue = "Default value";
        String realsender = sharedPreferences.getString("name", defaultValue);


        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("result", "delivered");
        if (hold != null){
            hashMap.put("reply", hold);
            hashMap.put("whoreply", realsender);
        }
        else if (hold.isEmpty()){
            hashMap.put("reply", "");
            hashMap.put("whoreply", "");
        }
        if (hold.equals("")){
            hashMap.put("reply", "");
            hashMap.put("whoreply", "");
        }
        databaseReference.child("Chats").push().setValue(hashMap);

        hold = "";
        // reset edittext after sending message
        messageEt.setText("");
        messageEt.clearFocus();
        replymessagelayout.setVisibility(View.GONE);

    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            myUid = user.getUid();// currently signed in user's uid
        }
        else {

        }
    }

    private void checkOnlineStatus(String status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        // update value of onlinestatus of current user
        dbRef.updateChildren(hashMap);

    }

    @Override
    protected void onStart() {
        checkUserStatus();
        //set online
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // get timestamp
        String onlineStatus = String.valueOf(System.currentTimeMillis());

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(onlineStatus));
        String dateTime = DateFormat.format("MMM-dd hh:mm aa", cal).toString();

        // set offline with last seen time stamp
        checkOnlineStatus(onlineStatus);
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        //set online
        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }

        if (id == R.id.options){
            Intent myintent = new Intent(ChatActivity.this,
                    SettingsActivity.class);
            startActivity(myintent);
            return false;
        }
        if (id == R.id.wallpaper){
            Intent myintent = new Intent(ChatActivity.this,
                    wallPaper.class);
            startActivity(myintent);
            return false;
        }
        if (id == R.id.tutor){
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Help");
            builder.setMessage("Grey check means sent/delivered \nGreen check means seen \n\nHold down or long press chat to reply or delete message");
            builder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    boolean isKeyboardShowing = false;
    void onKeyboardVisibilityChanged(boolean opened) {
    }

}
