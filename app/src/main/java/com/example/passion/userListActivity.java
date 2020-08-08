package com.example.passion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.passion.adapters.AdapterUser;
import com.example.passion.models.ModelUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class userListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    AdapterUser adapterUser;
    List<ModelUsers> usersList;
    CardView loadingshimmer;

    ProgressBar rowling;
    ShimmerLayout one;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        one = findViewById(R.id.one);
        loadingshimmer = findViewById(R.id.loadingshimmer);
        recyclerView = findViewById(R.id.userslist_RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(userListActivity.this));

        usersList = new ArrayList<>();

        getAllUsers();

        one.startShimmerAnimation();

    }

    private void getAllUsers() {
        //get current user
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        // get path of databse named "users" contiaining user info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        // get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUsers modelUsers = ds.getValue(ModelUsers.class);

                    //get all users except currently signed in user
                    if (!modelUsers.getUid().equals(fuser.getUid())){
                        usersList.add(modelUsers);
                        loadingshimmer.setVisibility(View.GONE);
                        one.stopShimmerAnimation();
                    }

                    adapterUser = new AdapterUser(userListActivity.this, usersList);
                    // set adapter to recyclerview
                    recyclerView.setAdapter(adapterUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);

    }
}
