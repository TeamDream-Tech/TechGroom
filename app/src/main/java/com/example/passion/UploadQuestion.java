package com.example.passion;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import android.os.Bundle;

public class UploadQuestion extends AppCompatActivity {
    private Spinner  typeSpinner,yearSpinner ,subjectSpinner,answerSpinner;
    private EditText questionEditText,optionA,optionB,optionC,optionD,solutionEditText;
    private FloatingActionButton uploadButton;
    private ImageView typeDefault,yearDeafault,subjectDefault;

    private String [] Examtype;
    private  String [] year;
    private String [] subject;
    private  String [] Answer;

    private long backPressedTime;
    private Toast backToast;

    private  boolean isExamDefault = false;
    private boolean isYearDefault = false;
    private boolean isSubjectDefault= false;

    private FirebaseFirestore mFirestore;

    public QuestionNumber questionNumber=new QuestionNumber();


    private ImageView Auto_Increase;
    private Button Addition,Minus,Clear_Question;
    private TextView number_of_Question;

    private boolean Auto_Update=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.upload_question);
        typeSpinner = findViewById(R.id.type);
        yearSpinner = findViewById(R.id.year);
        subjectSpinner = findViewById(R.id.subject);
        questionEditText = findViewById(R.id.groupTitleET);
        solutionEditText = findViewById(R.id.groupDecriptionET);
        answerSpinner = findViewById(R.id.answer);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionsB);
        optionC = findViewById(R.id.optionsC);
        optionD = findViewById(R.id.oprionsD);
        Auto_Increase = findViewById(R.id.Auto_add);
        Addition = findViewById(R.id.Add);
        Minus = findViewById(R.id.Minus);
        Clear_Question = findViewById(R.id.clear_question);
        number_of_Question = findViewById(R.id.Question_number);


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirestore= FirebaseFirestore.getInstance();

        year =  getResources().getStringArray(R.array.year);
        Examtype = getResources().getStringArray(R.array.ExamType);
        subject = getResources().getStringArray(R.array.subject);
        Answer = getResources().getStringArray(R.array.Answer);
        uploadButton = findViewById(R.id.createGroupButton);

        typeDefault = findViewById(R.id.typeDefault);
        yearDeafault = findViewById(R.id.yearDefault);
        subjectDefault = findViewById(R.id.subjectDefault);

        setSpinnerAdapter(typeSpinner,Examtype);
        setSpinnerAdapter(yearSpinner,year);
        setSpinnerAdapter(subjectSpinner,subject);
        setSpinnerAdapter(answerSpinner,Answer);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Question = questionEditText.getText().toString().trim();
                String solution= solutionEditText.getText().toString().trim();
                String Aoption= optionA.getText().toString().trim();
                String Boption = optionB.getText().toString().trim();
                String Coption= optionC.getText().toString().trim();
                String Doption = optionD.getText().toString().trim();
                String examType = typeSpinner.getSelectedItem().toString();
                String year = yearSpinner.getSelectedItem().toString();
                String subject = subjectSpinner.getSelectedItem().toString();
                String answer = answerSpinner.getSelectedItem().toString();

                if (examType.equalsIgnoreCase("Select Exam Type")){
                    showError(typeSpinner,"Please Select an exam type");
                    return;
                }
                if (year.equalsIgnoreCase("Select Year")){
                    showError(yearSpinner,"Please Select a year");
                    return;
                }
                if (subject.equalsIgnoreCase("Select Subject")){
                    showError(subjectSpinner,"Select Subject");
                    return;
                }

                if (Question.isEmpty()){
                    showError(questionEditText,"This field cant be empty....");
                    return;
                }

                if (Aoption.isEmpty()){
                    showError(optionA,"This field cant be empty....");
                    return;
                }
                if (Boption.isEmpty()){
                    showError(optionB,"This field cant be empty....");
                    return;
                }
                if (Coption.isEmpty()){
                    showError(optionC,"This field cant be empty.....");
                    return;
                }

                if (Doption.isEmpty()){
                    showError(optionD,"This field cant be empty....");
                    return;
                }
                if (answer.equalsIgnoreCase("Select Answer")){
                    showError(answerSpinner,"Select Answer");
                    return;
                }
                if(solution.isEmpty()){
                    showError(solutionEditText,"This field cant be empty....");
                    return;
                }
                Map<String ,Object> options = new HashMap<>();
                options.put("A",Aoption);
                options.put("B",Boption);
                options.put("C",Coption);
                options.put("D",Doption);

                Map<String,Object> hashMap = new HashMap<>();
                hashMap.put("ExamType",examType);
                hashMap.put("Year",year);
                hashMap.put("subject",subject);
                hashMap.put("question",Question);
                hashMap.put("Options",options);
                hashMap.put("Answer",answer);
                hashMap.put("Solution",solution);




                final ProgressDialog progressDialog = new ProgressDialog(UploadQuestion.this);
                progressDialog.setMessage("Uploading To DataBase");
                progressDialog.show();
                progressDialog.setCancelable(false);
                mFirestore.collection("PastQuestion").document(examType).collection(year).document(subject).collection("Q"+questionNumber.getQuestionNumber()).document("Question").set(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                uploadFinsished();
                                if (Auto_Update){
                                    AddQuestion();
                                }
                                progressDialog.dismiss();
                                Toast.makeText(UploadQuestion.this,"Uploaded Succesfully",Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(UploadQuestion.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        checkDefault(typeDefault,yearDeafault,subjectDefault);
        onCliclMinus();
        onClickAdd();
        onClickClear();
        onClickAutoUpdate();
    }
    public void onClickAutoUpdate(){
        Auto_Increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Auto_Update){
                    Auto_Increase.setImageResource(R.drawable.ic_auto_add);
                    Auto_Update=false;
                }else{
                    Auto_Increase.setImageResource(R.drawable.ic_auto_enabled);
                    Auto_Update=true;
                }
            }
        });
    }
    public void onClickClear(){
        Clear_Question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionNumber.setQuestionNumber(1);
                updateQuestionField();
            }
        });
    }

    public void onClickAdd(){
        Addition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddQuestion();
            }
        });
    }
    public void onCliclMinus(){
        Minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MinusQuestion();
            }
        });
    }
    public void AddQuestion(){
        int number = questionNumber.getQuestionNumber();
        int updated= ++number;
        questionNumber.setQuestionNumber(updated);
        updateQuestionField();
    }
    public void MinusQuestion(){
        int number = questionNumber.getQuestionNumber();
        if (number==1){
            Toast.makeText(UploadQuestion.this,"Question Can,t be more than one",Toast.LENGTH_SHORT).show();
        }else{
            int updated= --number;
            questionNumber.setQuestionNumber(updated);
            updateQuestionField();
        }

    }
    public void updateQuestionField(){
        int number = questionNumber.getQuestionNumber();
        number_of_Question.setText("Q"+number);

    }
    private void checkDefault(final ImageView examDefault,final ImageView yearDefault,final ImageView subjectDefault){
        examDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExamDefault){
                    examDefault.setImageResource(R.drawable.ic_default_off);
                    isExamDefault=false;
                }else{
                    examDefault.setImageResource(R.drawable.ic_default_on);
                    isExamDefault=true;
                }
            }
        });

        yearDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isYearDefault){
                    yearDefault.setImageResource(R.drawable.ic_default_off);
                    isYearDefault=false;
                }else{
                    yearDefault.setImageResource(R.drawable.ic_default_on);
                    isYearDefault=true;
                }
            }
        });
        subjectDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSubjectDefault){
                    subjectDefault.setImageResource(R.drawable.ic_default_off);
                    isSubjectDefault=false;
                }else{
                    subjectDefault.setImageResource(R.drawable.ic_default_on);
                    isSubjectDefault=true;
                }
            }
        });
    }

    public void showError(View v,String message){
        if (v instanceof Spinner){
            Toast.makeText(UploadQuestion.this,""+message,Toast.LENGTH_SHORT).show();
            v.requestFocus();
        }else if (v instanceof EditText){
            ((EditText) v).setError(message);
            v.requestFocus();
        }

    }

    public void setSpinnerAdapter(Spinner spinner,String [] items){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    public void uploadFinsished(){
        questionEditText.setText("");
        solutionEditText.setText("");
        optionA.setText("");
        optionB.setText("");
        optionC.setText("");
        optionD.setText("");

        answerSpinner.setSelection(0);
        if (!isExamDefault){
            typeSpinner.setSelection(0);
        }
        if (!isSubjectDefault){
            subjectSpinner.setSelection(0);
        }
        if (!isYearDefault){
            yearSpinner.setSelection(0);
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref",MODE_PRIVATE);
        SharedPreferences.Editor outState = sharedPreferences.edit();
        outState.putBoolean(Constant.DEFAULT_EXAM_TYPE_CONSTANT,isExamDefault);
        outState.putBoolean(Constant.DEFAUL_YEAR_CONSTANT,isYearDefault);
        outState.putBoolean(Constant.DEFAULT_SUBJECT_CONSTANT,isSubjectDefault);
        outState.putString(Constant.QUESTION_CONSTANT,questionEditText.getText().toString().trim());
        outState.putString(Constant.OPTION_A_CONSTANT,optionA.getText().toString().trim());
        outState.putString(Constant.OPTION_B_CONSTANT,optionB.getText().toString().trim());
        outState.putString(Constant.OPTION_C_CONSTANT,optionC.getText().toString().trim());
        outState.putString(Constant.OPTION_D_CONSTANT,optionD.getText().toString().trim());
        outState.putString(Constant.SOLUTION_CONSTANT,solutionEditText.getText().toString());

        outState.putInt(Constant.EXAM_TYPE_CONSTANT,typeSpinner.getSelectedItemPosition());
        outState.putInt(Constant.YEAR_CONSTANT,yearSpinner.getSelectedItemPosition());
        outState.putInt(Constant.ANSWER_CONSTANT,answerSpinner.getSelectedItemPosition());
        outState.putInt(Constant.SUBJECT_CONSTANT,subjectSpinner.getSelectedItemPosition());

        outState.putInt(Constant.QUESTION_NUMBER_CONSTANT,questionNumber.getQuestionNumber());
        outState.putBoolean(Constant.AUTO_UPDATE_NUMBER_CONSTANT,Auto_Update);

        outState.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences savedInstanceState = getSharedPreferences("shared_pref",MODE_PRIVATE);

        String qestionEt = savedInstanceState.getString(Constant.QUESTION_CONSTANT,"");
        String solutionEt = savedInstanceState.getString(Constant.SOLUTION_CONSTANT,"");
        String optionAEt = savedInstanceState.getString(Constant.OPTION_A_CONSTANT,"");
        String optionBEt = savedInstanceState.getString(Constant.OPTION_B_CONSTANT,"");
        String optionCEt = savedInstanceState.getString(Constant.OPTION_C_CONSTANT,"");
        String optionDEt = savedInstanceState.getString(Constant.OPTION_D_CONSTANT,"");
        boolean isExam =savedInstanceState.getBoolean(Constant.DEFAULT_EXAM_TYPE_CONSTANT,false);
        boolean isYear = savedInstanceState.getBoolean(Constant.DEFAUL_YEAR_CONSTANT,false);
        boolean isSubject = savedInstanceState.getBoolean(Constant.DEFAULT_SUBJECT_CONSTANT,false);

        int _examSpinner = savedInstanceState.getInt(Constant.EXAM_TYPE_CONSTANT,0);
        int _yearSpinner = savedInstanceState.getInt(Constant.YEAR_CONSTANT,0);
        int _subjectSpinner = savedInstanceState.getInt(Constant.SUBJECT_CONSTANT,0);
        int  _answerSpinner = savedInstanceState.getInt(Constant.ANSWER_CONSTANT,0);

        int presentQuestion = savedInstanceState.getInt(Constant.QUESTION_NUMBER_CONSTANT,1);
        boolean check_auto_update = savedInstanceState.getBoolean(Constant.AUTO_UPDATE_NUMBER_CONSTANT,false);

        isExamDefault=isExam;
        isYearDefault=isYear;
        isSubjectDefault=isSubject;
        Auto_Update=check_auto_update;

        Log.d("checkout", "exmaDefault"+isSubjectDefault);

        questionEditText.setText(qestionEt);
        solutionEditText.setText(solutionEt);
        optionA.setText(optionAEt);
        optionB.setText(optionBEt);
        optionC.setText(optionCEt);
        optionD.setText(optionDEt);

        questionNumber.setQuestionNumber(presentQuestion);
        updateQuestionField();

        typeSpinner.setSelection(_examSpinner);
        yearSpinner.setSelection(_yearSpinner);
        subjectSpinner.setSelection(_subjectSpinner);
        answerSpinner.setSelection(_answerSpinner);

        if (isExamDefault){
            typeDefault.setImageResource(R.drawable.ic_default_on);

        }else{
            typeDefault.setImageResource(R.drawable.ic_default_off);
        }

        if (isYearDefault){
            yearDeafault.setImageResource(R.drawable.ic_default_on);

        }else{
            yearDeafault.setImageResource(R.drawable.ic_default_off);
        }
        if (isSubjectDefault){
            subjectDefault.setImageResource(R.drawable.ic_default_on);

        }else{
            subjectDefault.setImageResource(R.drawable.ic_default_off);

        }
        if (Auto_Update){
            Auto_Increase.setImageResource(R.drawable.ic_auto_enabled);

        }else{
            Auto_Increase.setImageResource(R.drawable.ic_auto_add);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
