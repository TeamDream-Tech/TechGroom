package com.example.passion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.passion.adapters.AdapterOpponent;
import com.example.passion.adapters.AdapterUser;
import com.example.passion.models.ModelUsers;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChooseOpponent extends AppCompatActivity {
    RecyclerView recyclerView;
    AdapterOpponent adapterOpponent;
    List<ModelUsers> usersList;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_opponent);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pd = new ProgressDialog(this);


        recyclerView = findViewById(R.id.chooseopponent_RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChooseOpponent.this));

        usersList = new ArrayList<>();

        getAllUsers();

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
                    }

                    adapterOpponent = new AdapterOpponent(ChooseOpponent.this, usersList);
                    // set adapter to recyclerview
                    recyclerView.setAdapter(adapterOpponent);
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
