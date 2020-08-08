package com.example.passion.ui.slideshow;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.passion.ActionBottomDialogFragment;
import com.example.passion.R;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SlideshowFragment extends Fragment implements ActionBottomDialogFragment.ItemClickListener{
    FloatingActionButton floatingActionButton;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String uid;
    FloatingActionMenu floatingActionMenu;

    com.github.clans.fab.FloatingActionButton others, self;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        floatingActionMenu = root.findViewById(R.id.unfoldbtn);
        others = root.findViewById(R.id.save);
        self = root.findViewById(R.id.repost);

        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionMenu.close(true);
                showBottomSheet(v);
            }
        });

        return root;
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

    public void showBottomSheet(View view) {
        ActionBottomDialogFragment addPhotoBottomDialogFragment =
                ActionBottomDialogFragment.newInstance();
        addPhotoBottomDialogFragment.show(getFragmentManager(),
                ActionBottomDialogFragment.TAG);
    }

    @Override
    public void onItemClick(String item) {

    }
}