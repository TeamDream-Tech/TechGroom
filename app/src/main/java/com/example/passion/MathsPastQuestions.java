package com.example.passion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;
import java.util.zip.Inflater;

import io.github.kexanie.library.MathView;

public class MathsPastQuestions extends AppCompatActivity {
    TextView tv;
    TextView time;
    Button submitbutton, quitbutton;
    RadioGroup radio_g;
    MathView opt2, opt3, opt4;
    CountDownTimer countDownTimer;
    Button button;
    MathView formula_two, opt1, answer;
    MathView mathView;
    String questions[] = {

            "(1) Make q the subject of the formula in the equation. $${mn \\over a^2} - {pq \\over b^2} = 1$$",
            "(2) The angle of elevation of the top of a tree from a point on the ground 60m away from the foot of the tree is 78°. Find the height of the tree correct to the nearest whole number.",
            "(3) A binary operation ⊗ is defined by m⊗n=mn+m−n on the set of real numbers, for all m, n ∈ R. Find the value of 3 ⊗ (2 ⊗ 4)",
            "<table>" +
                    "<tr>" +
                    "<td>Age in years </td>\n" +
                    "<td>| 7  </td>\n" +
                    "<td>| 8  </td>\n" +
                    "<td>| 9  </td>\n" +
                    "<td>| 10 </td>\n" +
                    "<td>| 11 |</td>\n" +
                    "</tr>\n" +
                    "<tr>" +
                    "<td>No of pupils </td>\n" +
                    "<td>| 4  </td>\n" +
                    "<td>| 13 </td>\n" +
                    "<td>| 30 </td>\n" +
                    "<td>| 44 </td>\n" +
                    "<td>| 9  |</td>\n" +
                    "</tr>" +
                    "</table>" + "<p> </P>" + "(4) The table above shows the number of pupils in a class with respect to their ages. If a pie chart is constructed to represent the age, the angle corresponding to 8 years old is",
            "(5) In a class of 50 students, 40 students offered Physics and 30 offered Biology. How many offered both Physics and Biology?",
            "(6) Rationalize $${\\frac{\\sqrt{2} + \\sqrt{3}}{\\sqrt{2} - \\sqrt{3}}}$$",
            "(7) Find the length of the chord |AB| in the diagram shown above.",
            "(8) $${Given \\sin 58° = \\cos p°, find p}$$",
            "(9) $${\\frac{\\frac{2}{3} \\div \\frac{4}{5}}{\\frac{1}{4} + \\frac{3}{5} - \\frac{1}{3}}}$$",
            "(10) $${if 6x^3 + 2x^2 - 5x + 1 divides x^2 - x - 1, find the remainder.}$$"
    };
    String answers[] = {"(a) $$q = {{b^2(mn - a^2)} \\over a^2 p}$$","(c) ≊  282m to the nearest whole number","(c) 15","(c) 46.8°","(b) 20","(b) import pkg.*","(d) None of the mentioned","(b) java","(a) equals()","(a) int"};

    String explain[] = {
            "$${mn \\over a^2} - {pq \\over b^2} = 1$$    $${mn \\over a^2} - 1 = {pq \\over b^2}$$    $${mn - a^2 \\over a^2} = {pq \\over b^2}$$    $$pq = {b^2(mn - a^2) \\over a^2}$$   $$q = {b^2(mn - a^2) \\over a^2p}$$",
            "$${\\tan 78 = \\frac{h}{60}}$$    $${h = 60 \\tan 78}$$   $${h = 60 \\times 4.705 = 282.27m}$$  ≊ 282m to the nearest whole number.",
            "$${m⊗n=mn+m−n}$$    $${3 ⊗ (2 ⊗ 4)}$$  $${2 ⊗ 4 = 2(4) + 2 - 4 = 6}$$  $${3 otimes 6 = 3(6) + 3 - 6  = 15}$$",
            "<p>Total number of pupils : 4 + 13 + 30 + 44 + 9 = 100</p>" + "<p>The number of 8 - year olds = 13</p>" + "<p>The angle represented by the 8-year olds</p>" + "<p>on the pie chart = 13100×360° = 46.8°</p>",
            "$${n(Total) = 50}$$   $${n(Physics) = 40}$$  $${n(Biology) = 30}$$   $${Let n(Physics and Biology) = x}$$   $${n(Physics only) = 40 -x}$$   $${n(Biology only) = 30 - x}$$    $${40 - x + 30 - x + x = 50}$$   $${70 - x = 50}$$   $${x = 20}$$",
            "No explanation available for this question",
            "No explanation available for this question",
            "No explanation available for this question",
            "No explanation available for this question",
            "No explanation available for this question"
    };

    String opt[] = {
            "(a) $$q = {{b^2(mn - a^2)} \\over a^2 p}$$","(b) $$q = {{m^2n - a^2} \\over p^2}$$","(c) $$q = {{mn - 2b^2} \\over a^2}$$","(d) $$q = {{b^2(n^2 - ma^2)} \\over n}$$",
            "(a) 148m","(b)  382m","(c) 282m","(d) 248m",
            "(a) 6","(b) 25","(c) 15","(d) 18",
            "(a) 48.6°","(b) 56.3°","(c) 46.8°","(d) 13°",
            "(a) 42","(b) 20","(c) 70","(d) 54",
            "(a) $${- 5 - 2\\sqrt{6}}$$","(b)$${ -5 + 3\\sqrt{2}}$$","(c)$${ 5 - 2\\sqrt{3}}$$","(d)$${ 5 + 2\\sqrt{6}}$$",
            "(a) int","(b) float","(c) void","(d) None of the mentioned",
            "(a) lang","(b) java","(c) util","(d) java.packages",
            "(a) $${\\frac{31}{50}}$$","(b)$${ \\frac{20}{31}}$$","(c)$${ \\frac{31}{20}}$$","(d)$${ \\frac{50}{31}}$$",
            "(a)  9x + 9","(b)  2x + 6","(c)  6x + 8","(d)  5x - 3"
    };
    int quenumber = 1;

    int flag=0;
    public static int marks=0,correct=0,wrong=0;
    Button submitbtn, prevbtn;
    ImageView imageView;
    TextView number;
    final int min = 1;
    final int max = 100;
    final int random = new Random().nextInt((max - min) + 1) + min;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maths_past_questions);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String categorytype= intent.getStringExtra("typeyear");

        dialog = new Dialog(this);
        setTitle(categorytype);

        imageView  = findViewById(R.id.explanation);
        formula_two = (MathView) findViewById(R.id.question);
//        tv=findViewById(R.id.question);
        answer = findViewById(R.id.answers);
        submitbtn = findViewById(R.id.submitbtn);
        prevbtn = findViewById(R.id.prevbtn);
        opt1=(MathView) findViewById(R.id.option1);
        opt2 =(MathView)findViewById(R.id.option2);
        opt3 =(MathView)findViewById(R.id.option3);
        opt4 =(MathView)findViewById(R.id.option4);
        number = findViewById(R.id.number);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LayoutInflater inflater = getLayoutInflater();
//                AlertDialog dialog = new AlertDialog.Builder(MathsPastQuestions.this)
//                        .setTitle("Explanation")
//                        .setMessage(answer.getText())
//                        .setView(inflater.inflate(R.layout.descriptionlayout, null))
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .setIcon(R.drawable.ic_description)
//                        .show();
//                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
//                textView.setScroller(new Scroller(MathsPastQuestions.this));
//                textView.setVerticalScrollBarEnabled(true);
//                textView.setMovementMethod(new ScrollingMovementMethod());
                Showmenubar();
            }
        });

        formula_two.setText(questions[flag]);
        opt1.setText(opt[0]);
        opt2.setText(opt[1]);
        opt3.setText(opt[2]);
        opt4.setText(opt[3]);
        answer.setText(answers[0]);

        number.setText("Question " + quenumber + " out of 10");
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag<questions.length-1)
                {
                    flag++;

                    quenumber++;

                    number.setText("Question " + quenumber + " out of 10");
                    if (flag <= 9){
                        answer.setText(answers[flag]);
                        formula_two.setText(questions[flag]);
                        opt1.setText(opt[flag*4]);
                        opt2.setText(opt[flag*4 +1]);
                        opt3.setText(opt[flag*4 +2]);
                        opt4.setText(opt[flag*4 +3]);

                    }

                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MathsPastQuestions.this);
                    builder.setCancelable(true);
                    builder.setMessage("This is the last question\ndo you want to end study?");
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                    builder.setNegativeButton("No, continue",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0){
                    Toast.makeText(MathsPastQuestions.this, "This is the first question", Toast.LENGTH_SHORT).show();
                }

                if(flag> 0){

                    flag--;

                    quenumber--;

                    number.setText("Question " + quenumber + " out of 10");
                    if (flag >= 0){
                        answer.setText(answers[flag]);
                        formula_two.setText(questions[flag]);
                        opt1.setText(opt[flag*4]);
                        opt2.setText(opt[flag*4 +1]);
                        opt3.setText(opt[flag*4 +2]);
                        opt4.setText(opt[flag*4 +3]);
                    }
                    else {
                        Toast.makeText(MathsPastQuestions.this, "This is the first question", Toast.LENGTH_SHORT).show();
                    }
                }
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

    private void Showmenubar() {
        dialog.setContentView(R.layout.descriptionlayout);
        mathView = (MathView) dialog.findViewById(R.id.descripout);
        mathView.setText(explain[flag]);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

}

