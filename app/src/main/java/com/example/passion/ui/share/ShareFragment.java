package com.example.passion.ui.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.passion.Createpost;
import com.example.passion.Notifications;
import com.example.passion.ProfileActivity;
import com.example.passion.R;
import com.example.passion.SecondActivity;
import com.example.passion.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShareFragment extends Fragment {

    private ShareViewModel shareViewModel;
    public CardView cardView;
    public GridLayout gridLayout;
    private TextView textViews;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String uid;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_share, container, false);

        gridLayout = root.findViewById(R.id.mainGridshare);
        setEvent(gridLayout);

        textViews = (TextView) root.findViewById(R.id.sharetext);

        return root;
    }

    public void setEvent(GridLayout gridLayout) {
        for (int a = 0; a < gridLayout.getChildCount();a++){
            cardView = (CardView)gridLayout.getChildAt(a);
            final int finali = a;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = null;
                    if(finali ==0){
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Find your passion now");
                        try {
                            startActivity(whatsappIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getActivity(), "WhatsApp have not been installed", Toast.LENGTH_SHORT).show();                        }
                    }else if(finali ==1){
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto","recipient", null));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Find Your Passion now");
                        intent.putExtra(Intent.EXTRA_TEXT, "Join us and uncover your potential");
                        startActivity(Intent.createChooser(intent, "Techgroom:"));

                    }else if(finali == 2) {
                        String shareBody = "https://play.google.com/store/apps/details";
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "APP NAME (Techgroom)");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
//                    else if(finali == 3){
//                        final String appPackageName = getContext().getPackageName(); // getPackageName() from Context or Activity object
//                        try {
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                        } catch (android.content.ActivityNotFoundException anfe) {
//                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                        }
//
//                    }

                }
            });
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.action_Search);
        if(item!=null)
            item.setVisible(false);
    }

}