package com.example.passion;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;

public class NetworkUtil {

    public static String getConnectivityStatusString(final Context context) {
        String status = null;
        AlertDialog.Builder Alert = new AlertDialog.Builder(context);
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {

                status = "Mobile data enabled";

        } else {
            status = "No internet is available";
            Alert.setCancelable(true)
                    .setTitle("You're offline")
                    .setMessage("Check your internet connection and try again")
                    .setIcon(R.drawable.ic_cloud)
                    .setPositiveButton(Html.fromHtml("<font color='#FA5521'>Ok</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                             dialogInterface.dismiss();
                        }
                    })

                    .setNegativeButton(Html.fromHtml("<font color='#FA5521'>No, Exit App</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.exit(0);
                            dialogInterface.dismiss();
                        }
                    });


            AlertDialog alertDialog = Alert.create();
            alertDialog.show();



            return status;
        }
        return status;
    }



}
