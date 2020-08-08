package com.example.passion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class ProfileActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);




//        FloatingActionButton fab = findViewById(R.id.fabprofile);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ProfileActivity.this, Edit_Profile_Activity.class);
//                startActivity(intent);
//
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        collapsingToolbarLayout = findViewById(R.id.collapstool);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        toolbar = (Toolbar) findViewById(R.id.proftool);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }
        if (id == R.id.edit){
            Intent myintent = new Intent(ProfileActivity.this,
                    Edit_Profile_Activity.class);
            startActivity(myintent);
            return false;
        }

        if (id == R.id.settings){
            Intent myintent = new Intent(ProfileActivity.this,
                    SettingsActivity.class);
            startActivity(myintent);
            return false;
        }
        if (id == R.id.add){
            Intent myintent = new Intent(ProfileActivity.this,
                    userListActivity.class);
            startActivity(myintent);
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
