package com.example.passion.ui.gallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.passion.ActivityMain;
import com.example.passion.BottomActivity;
import com.example.passion.ChatActivity;
import com.example.passion.CreateQuestion;
import com.example.passion.Createpost;
import com.example.passion.Notifications;
import com.example.passion.ProfileActivity;
import com.example.passion.R;
import com.example.passion.SecondActivity;
import com.example.passion.SettingsActivity;
import com.example.passion.adapters.AdapterPosts;
import com.example.passion.models.ModelPost;
import com.example.passion.userListActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPosts adapterPosts;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    String myUid;
    RelativeLayout backup;

    CardView cardView;
    TextView textView;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    NestedScrollView nestedScrollView;
    private static int firstVisibleInListview;
    String uid;
    Button button;
    LinearLayoutManager layoutManager;
    public static int index = -1;
    public static int top = -1;

    private Animation mBounceAnimation;
    private int someStateValue;
    private final String SOME_VALUE_KEY = "someValueToSave";

    private Parcelable savedRecyclerLayoutState;
    private GridLayoutManager mGridLayoutManager;
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        if (savedInstanceState != null) {
            someStateValue = savedInstanceState.getInt(SOME_VALUE_KEY);
            // Do something with value if needed
        }
        backup = root.findViewById(R.id.backup);
        nestedScrollView = root.findViewById(R.id.NestedScrollView);

//        floatingActionButton = root.findViewById(R.id.createpostfrag);
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), Createpost.class);
//                startActivity(intent);
//            }
//        });


        final ImageView imageView = root.findViewById(R.id.showface);
        cardView = root.findViewById(R.id.fragProfile);
        textView = root.findViewById(R.id.forposts);
        button = root.findViewById(R.id.findfriends);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // user is signed in
            myUid = user.getUid();
        }

        Glide.with(getContext()).load(user.getPhotoUrl()).into(imageView);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = String.valueOf(dataSnapshot.child("name").getValue());
                String email = String.valueOf(dataSnapshot.child("email").getValue());

                textView.setText(name);
                Glide.with(getContext()).load(user.getPhotoUrl()).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), userListActivity.class);
                startActivity(intent);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Createpost.class);
                startActivity(intent);
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });
        swipeRefreshLayout = root.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadPosts();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2500);

            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = root.findViewById(R.id.waitforcontent);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = root.findViewById(R.id.postsReCyclerview);
        layoutManager = new LinearLayoutManager(getActivity());


        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        //set layout to recyclerview

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //At this point the layout is complete and the
                        //dimensions of recyclerView and any child views are known.
                        //Remove listener after changed RecyclerView's height to prevent infinite loop
                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        postList = new ArrayList<>();
        loadPosts();

        Animation animationUtils= AnimationUtils.loadAnimation(getContext(),R.anim.bounce_animation);
        mBounceAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.shakeanimation);
        backup.startAnimation(animationUtils);


        firstVisibleInListview = layoutManager.findFirstVisibleItemPosition();

        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(postList.size() - 1);
                nestedScrollView.fullScroll(View.FOCUS_UP);
//                backup.startAnimation(mBounceAnimation);
            }
        });

        checkUserStatus();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int currentFirstVisible = layoutManager.findFirstVisibleItemPosition();

                if(currentFirstVisible > firstVisibleInListview){
                    Toast.makeText(getContext(), "scrolling up", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "scrolling down", Toast.LENGTH_SHORT).show();
                }

                firstVisibleInListview = currentFirstVisible;
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT,
                layoutManager.onSaveInstanceState());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        }

    }

    private void checkUserStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // user is signed in
            myUid = user.getUid();
        }
        else {
            // user is not signed in
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//
//        layoutManager = recyclerView.getLayoutManager();
//        int scrollPosition = layoutManager.findFirstVisibleItemPosition();
//        Toast.makeText(getContext(), ""+pos, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        //set recyclerview position

        layoutManager.scrollToPositionWithOffset( index, top);

    }

    private void loadPosts() {
        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    postList.add(modelPost);

                    //adapter
                    adapterPosts = new AdapterPosts(getActivity(), postList);
                    // set adapter to recyclerview
                    recyclerView.setAdapter(adapterPosts);

                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //incase of error
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPosts(final String searchQuery){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    if (modelPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            modelPost.getPDescription().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(modelPost);
                    }

                    //adapter
                    adapterPosts = new AdapterPosts(getActivity(), postList);
                    // set adapter to recyclerview
                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //incase of error
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                    searchPosts(query);
                }
                else
                {
                    loadPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    searchPosts(newText);
                }
                else
                {
                    loadPosts();
                }
                return false;
            }
        });
    }
}