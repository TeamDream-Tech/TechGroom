package com.example.passion;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ActivityMain extends AppCompatActivity {
    private BroadcastReceiver MyReceiver = null;
    FirebaseAuth mAuth;
    private AlertDialog dialog;
    FirebaseUser currentUser;
    String uid;
    TextView textviewname, textviewemail;
    ImageView ImageUserPhoto;
    ClipData.Item item;
    private long backPressedTime;
    private Toast backToast;

    SharedPreferences sp;
    String emailstr, namestr;
    public static final String USER_PREF = "USER_PREF" ;
    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_AGE = "KEY_AGE";

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            uid = mCurrentUser.getUid();
        }
        MyReceiver = new MyReceiver();
        broadcastIntent();

        NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view);
        navigationView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(ActivityMain.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        View headerView = navigationView1.getHeaderView(0);
//        ImageView navUserPhoto = headerView.findViewById(R.id.imageView);
//        navUserPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                DrawerLayout drawer = findViewById(R.id.drawer_layout);
//                drawer.closeDrawer(GravityCompat.START);
//                Intent intent = new Intent(ActivityMain.this, ProfileActivity.class);
//                startActivity(intent);
//            }
//        });

        sp = getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);

//        Glide.with(ActivityMain.this).load(currentUser.getPhotoUrl()).into(navUserPhoto);

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = String.valueOf(dataSnapshot.child("name").getValue());
                String email = String.valueOf(dataSnapshot.child("email").getValue());


                sp = getApplicationContext().getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sp.edit();

                editor.putString(name, name);
                editor.putString(email, email);
                editor.commit();
                editor.apply();

//                NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view);
//                View headerView = navigationView1.getHeaderView(0);
//                TextView navUsername = headerView.findViewById(R.id.nav_username);
//                TextView navUserMail = headerView.findViewById(R.id.nav_useremail);
//                ImageView navUserPhoto = headerView.findViewById(R.id.imageView);

//                Glide.with(ActivityMain.this).load(currentUser.getPhotoUrl()).into(navUserPhoto);



//                StringBuilder str = new StringBuilder();
//
//                navUserMail.setText(sp.getString(email, ""));
//                navUsername.setText(sp.getString(name, ""));
//
//
//                navUserPhoto.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent profileIntent = new Intent(ActivityMain.this,ProfileActivity.class);
//                        startActivity(profileIntent);
//                        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//                        drawer.closeDrawer(GravityCompat.START);
//                    }
//                });

//                Glide.with(ActivityMain.this).load(currentUser.getPhotoUrl()).into(navUserPhoto);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void broadcastIntent() {
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void checkOnlineStatus(String status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        // update value of onlinestatus of current user
        dbRef.updateChildren(hashMap);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
