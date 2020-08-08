package com.example.passion.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.passion.ActivityMain;
import com.example.passion.CommonEntranceActivity;
import com.example.passion.Createpost;
import com.example.passion.JambActivity;
import com.example.passion.JuniorWaecActivity;
import com.example.passion.Notifications;
import com.example.passion.ProfileActivity;
import com.example.passion.R;
import com.example.passion.SecondActivity;
import com.example.passion.SeniorWaecActivity;
import com.example.passion.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class HomeFragment extends Fragment {
    private long backPressedTime;
    private Toast backToast;

    private int someStateValue;
    private final String SOME_VALUE_KEY = "someValueToSave";
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String uid;

    private HomeViewModel homeViewModel;
    CountDownTimer countDownTimer;
    ImageView imageView, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7, imageView8, imageView9, imageView10, imageView11, imageView12, imageView13, imageView14;
    CardView cardView, cardView2, cardView3;
    TextView viewmore1, viewmore2, viewmore3, viewmore4, viewmore5;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        if (savedInstanceState != null) {
            someStateValue = savedInstanceState.getInt(SOME_VALUE_KEY);
            // Do something with value if needed
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (backPressedTime + 2000 > System.currentTimeMillis()){
                    backToast.cancel();
                    System.exit(0);
                    return;
                }
                else {
                    backToast = Toast.makeText(getActivity(), "Press back again to exit", Toast.LENGTH_LONG);
                    backToast.show();
                }
                backPressedTime = System.currentTimeMillis();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        viewmore5 = root.findViewById(R.id.viewmore5);
        viewmore3 = root.findViewById(R.id.viewmore3);
        viewmore1 = root.findViewById(R.id.viewmore1);
        viewmore4 = root.findViewById(R.id.viewmore4);

        viewmore2 = root.findViewById(R.id.viewmore2);
        viewmore2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), JambActivity.class);
                startActivity(intent);
            }
        });
        viewmore3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent juniorwaecintent = new Intent(getContext(), JuniorWaecActivity.class);
                startActivity(juniorwaecintent);
            }
        });
        viewmore4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commonintent = new Intent(getContext(), CommonEntranceActivity.class);
                startActivity(commonintent);
            }
        });
        viewmore5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent waecintent = new Intent(getContext(), SeniorWaecActivity.class);
                startActivity(waecintent);
            }
        });




        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {

                }

            };
        countDownTimer.start();


        return root;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SOME_VALUE_KEY, someStateValue);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
//        MenuItem item=menu.findItem(R.id.action_Search);
//        if(item!=null)
//            item.setVisible(false);
    }
}