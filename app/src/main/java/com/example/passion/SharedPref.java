package com.example.passion;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences mySharedPref;
    public SharedPref(Context context){
        mySharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);

    }
    // this method will save the nightmood state: true or false
    public  void setChatbackground(Integer which){
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putInt("choose", which);
//        editor.putBoolean("NightMood", state);
        editor.commit();
    }

    // this method willl load the night mood state
    public Integer loadChatbackground(){
//        Boolean state = mySharedPref.getBoolean("NightMood", false);
        Integer which = mySharedPref.getInt("choose", 1);
        return which;
    }
}
