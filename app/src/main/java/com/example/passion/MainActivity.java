package com.example.passion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2500;
    Context mContext;
    public ImageView ReminderButton, next, next1, next2, next3, next4, next5, next6, next7, next8, next9, next10, next11, next12, next14, next15, next16;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        },SPLASH_TIME_OUT);



        Runnable runnable= new Runnable() {
            @Override
            public void run() {
                ReminderButton=findViewById(R.id.shake);
                next=findViewById(R.id.shake2);
                next1=findViewById(R.id.shake3);
                next2=findViewById(R.id.shake4);
                next3=findViewById(R.id.shake5);
                next4=findViewById(R.id.shake6);
                next6=findViewById(R.id.shake8);
                next8=findViewById(R.id.shake10);
                next9=findViewById(R.id.shake11);
                next10=findViewById(R.id.shake12);
                next11=findViewById(R.id.shake14);
                next12=findViewById(R.id.shake15);
                next14=findViewById(R.id.shake16);
                Animation animationUtils= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shakeanimation);
                ReminderButton.startAnimation(animationUtils);
                next.startAnimation(animationUtils);
                next1.startAnimation(animationUtils);
                next2.startAnimation(animationUtils);
                next3.startAnimation(animationUtils);
                next4.startAnimation(animationUtils);
                next6.startAnimation(animationUtils);
                next8.startAnimation(animationUtils);
                next9.startAnimation(animationUtils);
                next10.startAnimation(animationUtils);
                next11.startAnimation(animationUtils);
                next12.startAnimation(animationUtils);
                next14.startAnimation(animationUtils);

            }
        };
        runnable.run();

    }


}
