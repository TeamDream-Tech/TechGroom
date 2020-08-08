package com.example.passion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.passion.ui.dashboard.DashboardFragment;
import com.example.passion.ui.gallery.GalleryFragment;
import com.example.passion.ui.home.HomeFragment;
import com.example.passion.ui.notifications.NotificationsFragment;
import com.example.passion.ui.send.SendFragment;
import com.example.passion.ui.share.ShareFragment;
import com.example.passion.ui.slideshow.SlideshowFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ncapdevi.fragnav.FragNavController;
import com.ncapdevi.fragnav.FragNavSwitchController;
import com.ncapdevi.fragnav.FragNavTransactionOptions;
import com.ncapdevi.fragnav.tabhistory.FragNavTabHistoryController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

public class BottomActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragNavController.RootFragmentListener {
    androidx.appcompat.widget.Toolbar toolbar;
    private HomeFragment fragmentSimple;
    private final String SIMPLE_FRAGMENT_TAG = "myfragmenttag";
    AppBarLayout appBarLayout;

    private AppBarConfiguration mAppBarConfiguration;
    private boolean mToolBarNavigationListenerIsRegistered = false;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String uid;

    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new SlideshowFragment();
    final Fragment fragment3 = new GalleryFragment();
    final Fragment fragment4 = new HotFragment();
    final Fragment fragment5 = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment3;


    //Fragments

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);

        fm.beginTransaction().add(R.id.containermain, fragment1, "1").hide(fragment1).commit();
        fm.beginTransaction().add(R.id.containermain, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.containermain, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.containermain, fragment5, "5").hide(fragment5).commit();
        fm.beginTransaction().add(R.id.containermain,fragment3, "3").commit();

        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbars);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        dl = findViewById(R.id.drawer_layoutf);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(t != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nv = (NavigationView)findViewById(R.id.nav_views);
        nv.setNavigationItemSelectedListener(this);

        appBarLayout = findViewById(R.id.bar);

        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.action_learn) {
                    getSupportActionBar().setTitle(getResources().getString(R.string.botmenu_home));
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;
                } else if(id == R.id.action_brain) {
                    getSupportActionBar().setTitle(getResources().getString(R.string.botmenu_slideshow));
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;
                } else if(id == R.id.action_home) {
                    getSupportActionBar().setTitle(getResources().getString(R.string.botmenu_gallery));
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
                } else if(id == R.id.action_Solve) {
                    getSupportActionBar().setTitle(getResources().getString(R.string.botmenu_share));
                    fm.beginTransaction().hide(active).show(fragment4).commit();
                    active = fragment4;
                    return true;
                }else if(id == R.id.action_discuss) {
                    getSupportActionBar().setTitle(getResources().getString(R.string.botmenu_send));
                    fm.beginTransaction().hide(active).show(fragment5).commit();
                    active = fragment5;
                    return true;
                }

                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);

    }

    public void loadFragment(Fragment fragment) {
        String tag;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containermain, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        item.setChecked(true);
        dl.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
        switch (id) {
            case R.id.newnav_home:
                Intent intent = new Intent(BottomActivity.this, SecondActivity.class);
                startActivity(intent);
                break;
            case R.id.newnav_gallery:
                getSupportActionBar().setTitle("Share");
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.newnav_admin:
                Intent adminintent = new Intent(BottomActivity.this, AdminActivity.class);
                startActivity(adminintent);
                break;
            case R.id.newnav_slideshow:
                Toast.makeText(this, "Takes you to google playstore to rate", Toast.LENGTH_SHORT).show();
                break;
            case R.id.newnav_share:
                getSupportActionBar().setTitle("Feedback");
                break;
            case R.id.newnav_send:
                Intent intent1 = new Intent(BottomActivity.this, SettingsActivity.class);
                startActivity(intent1);
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(t.onOptionsItemSelected(item))
            return true;

        int id = item.getItemId();

        switch (id) {
            case R.id.Notifications:
                Intent intent = new Intent(BottomActivity.this, Notifications.class);
                startActivity(intent);
                return true;
            case R.id.action_profile:
                Intent newintent = new Intent(BottomActivity.this, ProfileActivity.class);
                startActivity(newintent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        final MenuItem alertMenuItem = menu.findItem(R.id.action_profile);

        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        final ImageView picture = (ImageView) rootView.findViewById(R.id.showpic);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            uid = mCurrentUser.getUid();
        }
        Glide.with(BottomActivity.this).load(currentUser.getPhotoUrl()).into(picture);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = String.valueOf(dataSnapshot.child("name").getValue());
                String email = String.valueOf(dataSnapshot.child("email").getValue());

                NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_views);
                View headerView = navigationView1.getHeaderView(0);
                TextView navUsername = headerView.findViewById(R.id.nav_usernameb);
                TextView navUserMail = headerView.findViewById(R.id.nav_useremailb);
                ImageView navUserPhoto = headerView.findViewById(R.id.imageViewb);

                Glide.with(BottomActivity.this).load(currentUser.getPhotoUrl()).into(navUserPhoto);


                navUserMail.setText(email);
                navUsername.setText(name);


                Glide.with(BottomActivity.this).load(currentUser.getPhotoUrl()).into(navUserPhoto);


                Glide.with(BottomActivity.this).load(currentUser.getPhotoUrl()).into(picture);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public Fragment getRootFragment(int i) {
        return null;
    }
}
