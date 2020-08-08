package com.example.passion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class selectyeardialog {

    Activity activity;
    AlertDialog selectyeardialog;
    Button closedialog, cancledialog;
    RadioGroup yeargroup;
    RadioButton year1, year2, year3;
    String ansText;
    ProgressDialog pd;
    public static  int  SPLASH_SCREEN=1000;

    selectyeardialog(Activity myActivity){
        activity = myActivity;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.selectyeardialog, null));
        builder.setCancelable(false);

        pd = new ProgressDialog(activity);
        pd.setMessage("Checking Archive...");
        pd.show();

        selectyeardialog = builder.create();
        selectyeardialog.show();


        yeargroup = selectyeardialog.findViewById(R.id.yeargroup);
        year1 = selectyeardialog.findViewById(R.id.jambmathsyear2019);
        year2 = selectyeardialog.findViewById(R.id.jambmathsyear2018);
        year3 = selectyeardialog.findViewById(R.id.jambmathsyear2017);

        cancledialog = selectyeardialog.findViewById(R.id.cancledialog);
        cancledialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissdialog();
            }
        });


        closedialog = selectyeardialog.findViewById(R.id.closedialog);
        closedialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(yeargroup.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(activity, "Please select year", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton uans = selectyeardialog.findViewById(yeargroup.getCheckedRadioButtonId());
                ansText = uans.getText().toString();


                selectyeardialog.dismiss();
                Intent intent = new Intent(v.getContext(), MathsPastQuestions.class);
                intent.putExtra("typeyear", "Jamb - Mathematics "+ansText);
                activity.startActivity(intent);

                Handler handler = new Handler();
                Runnable runnable= new Runnable() {
                    @Override
                    public void run(){
                        pd.dismiss();
                    }
                };

                handler.postDelayed(runnable,SPLASH_SCREEN);
            }
        });


    }
    void dismissdialog(){
        selectyeardialog.dismiss();
        pd.dismiss();
    }
}
