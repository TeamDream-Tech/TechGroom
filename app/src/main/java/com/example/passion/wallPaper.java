package com.example.passion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;

public class wallPaper extends AppCompatActivity {
    ImageView back1, back2, back3, back4, back5, back6, back7, back8, back9, back10, back11;
    SharedPref sharedPref;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_paper);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);


        back1 = findViewById(R.id.back1);
        back2 = findViewById(R.id.back2);
        back3 = findViewById(R.id.back3);
        back4 = findViewById(R.id.back4);
        back5 = findViewById(R.id.back5);
        back6 = findViewById(R.id.back6);
        back7 = findViewById(R.id.back7);
        back8 = findViewById(R.id.back8);
        back9 = findViewById(R.id.back9);
        back10 = findViewById(R.id.back10);
        back11 = findViewById(R.id.back11);
        relativeLayout = findViewById(R.id.wallpaperlayout);

        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snack = Snackbar.make(
                        findViewById(android.R.id.content),
                        Html.fromHtml("<font color=\"#388E3C\">Wallpaer Set</font>"),
                        Snackbar.LENGTH_SHORT
                );
                SnackbarHelper.configSnackbar(getApplicationContext(), snack);
                snack.show();
            }
        });
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        back3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        back4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        back5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        back6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        back7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        back8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        back9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        back10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        back11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
