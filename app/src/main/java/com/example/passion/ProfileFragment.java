package com.example.passion;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.passion.adapters.AdapterQuestions;
import com.example.passion.models.ModelQuestions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    List<ModelQuestions> questionsList;
    AdapterQuestions adapterQuestions;

    SwipeRefreshLayout swipeRefreshLayout;
    NestedScrollView nestedScrollView;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        nestedScrollView = root.findViewById(R.id.quesNestedScrollView);
        swipeRefreshLayout = root.findViewById(R.id.quesswiperefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (isNetworkAvailable() == true) {
                    swipeRefreshLayout.setRefreshing(true);
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadQuestions();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }, 2500);

                }else {
                    swipeRefreshLayout.setRefreshing(true);
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }, 500);
                    Snackbar.make(getView(), "Please check Internet connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });


        floatingActionButton = root.findViewById(R.id.postquestion);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateQuestion.class);
                startActivity(intent);
            }
        });

        recyclerView = root.findViewById(R.id.quesRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // show newest question first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);


        recyclerView.setLayoutManager(layoutManager);

        questionsList = new ArrayList<>();

        loadQuestions();

        return root;
    }

    private void loadQuestions() {
        // path of all questions
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Questions");
        // get all questions
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questionsList.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelQuestions modelQuestions = ds.getValue(ModelQuestions.class);

                    questionsList.add(modelQuestions);

                    // adater
                    adapterQuestions = new AdapterQuestions(getActivity(), questionsList);
                    // set adapter to recyclerview
                    recyclerView.setAdapter(adapterQuestions);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // in case of error
                Toast.makeText(getContext(), "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchQuestions(final String searchQuery){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Questions");
        // get all questions
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questionsList.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelQuestions modelQuestions = ds.getValue(ModelQuestions.class);

                    if (modelQuestions.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            modelQuestions.getPDescription().toLowerCase().contains(searchQuery.toLowerCase())){
                        questionsList.add(modelQuestions);
                    }

                    // adater
                    adapterQuestions = new AdapterQuestions(getActivity(), questionsList);
                    // set adapter to recyclerview
                    recyclerView.setAdapter(adapterQuestions);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // in case of error
                Toast.makeText(getContext(), "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_Search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    searchQuestions(query);
                }
                else
                {
                    loadQuestions();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    searchQuestions(newText);
                }
                else
                {
                    loadQuestions();
                }
                return false;
            }
        });
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
