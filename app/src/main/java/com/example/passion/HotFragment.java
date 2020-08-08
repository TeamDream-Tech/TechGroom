package com.example.passion;


import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.request.RequestOptions;
import com.example.passion.adapters.AdapterHot;
import com.example.passion.models.ModelHot;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.glide.slider.library.tricks.ViewPagerEx;
import com.google.android.material.snackbar.Snackbar;
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
public class HotFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    ViewFlipper viewFlipper;


    public HotFragment() {
        // Required empty public constructor
    }

    int images[] = {R.drawable.chemistry,R.drawable.maths,R.drawable.biology};
    private SliderLayout mDemoSlider;

    List<ModelHot> hotList;
    AdapterHot adapterHot;

    SwipeRefreshLayout swipeRefreshLayout;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_hot, container, false);


        mDemoSlider = root.findViewById(R.id.slider);

        ArrayList<String> listUrl = new ArrayList<>();
        ArrayList<String> listName = new ArrayList<>();

        listUrl.add("https://www.revive-adserver.com/media/GitHub.jpg");
        listName.add("JPG - Github");

        listUrl.add("https://tctechcrunch2011.files.wordpress.com/2017/02/android-studio-logo.png");
        listName.add("PNG - Android Studio");

        listUrl.add("https://static.toiimg.com/photo/72975551.cms");
        listName.add("GIF - Disney");

        listUrl.add("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQ2Tte0mvD9fj--zkJGrAwtpGg80FR5h46RHQ&usqp=CAU");
        listName.add("WEBP - Mountain");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        //.diskCacheStrategy(DiskCacheStrategy.NONE)
        //.placeholder(R.drawable.placeholder)
        //.error(R.drawable.placeholder);

        for (int i = 0; i < listUrl.size(); i++) {
            TextSliderView sliderView = new TextSliderView(getContext());
            // if you want show image only / without description text use DefaultSliderView instead

            // initialize SliderLayout
            sliderView
                    .image(listUrl.get(i))
                    .description(listName.get(i))
                    .setRequestOption(requestOptions)
                    .setProgressBarVisible(true)
                    .setOnSliderClickListener(this);

            //add your extra information
            sliderView.bundle(new Bundle());
            sliderView.getBundle().putString("extra", listName.get(i));
            mDemoSlider.addSlider(sliderView);
        }

        // set Slider Transition Animation
        // mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);

        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
        mDemoSlider.stopCyclingWhenTouch(false);




        nestedScrollView = root.findViewById(R.id.hotNestedScrollView);
        swipeRefreshLayout = root.findViewById(R.id.hotswiperefresh);
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

        recyclerView = root.findViewById(R.id.hotReCyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // show newest question first, for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);


        recyclerView.setLayoutManager(layoutManager);

        hotList = new ArrayList<>();

        loadQuestions();

        return root;
    }

    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getContext(), slider.getBundle().getString("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void loadQuestions() {
        // path of all questions
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Updates");
        // get all questions
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hotList.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelHot modelHot = ds.getValue(ModelHot.class);

                    hotList.add(modelHot);

                    // adater
                    adapterHot = new AdapterHot(getActivity(), hotList);
                    // set adapter to recyclerview
                    recyclerView.setAdapter(adapterHot);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // in case of error
                Toast.makeText(getContext(), "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
