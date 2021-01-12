package com.example.myrmit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myrmit.model.Course;
import com.google.android.material.tabs.TabLayout;

public class Courses extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView canvas;
    OES_Fragment oes_fragment;
    Display display;
    int endX;
    boolean isStart = false;
    AllocationFragment allocationFragment;
    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        oes_fragment= new OES_Fragment();
        allocationFragment = new AllocationFragment();
        viewPager = findViewById(R.id.pager1);
        tabLayout = findViewById(R.id.tab_layout1);
        canvas = findViewById(R.id.imageView2);
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        tabLayout.setupWithViewPager(viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(oes_fragment, "");
        viewPagerAdapter.addFragment(allocationFragment, "");
        viewPager.setAdapter(viewPagerAdapter);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_baseline_home_24);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_baseline_home_24);
        final boolean[] isMove = {false};
        canvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println(event.getAction());

                if (!isStart){
                    endX = (int) v.getX();
                    isStart = true;
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    isMove[0] = true;
                    int x_cord = (int) event.getRawX();
                    int y_cord = (int) event.getRawY();
                    canvas.setX(x_cord - 75);
                    if (y_cord - 115 > 0 && y_cord - 155 < viewPager.getHeight()){
                        canvas.setY(y_cord - 115);
                    }
                    else if (y_cord - 115 < 0){
                        canvas.setY(0);
                    }
                    else canvas.setY(viewPager.getHeight());
                }
                else {
                    if (event.getAction() != MotionEvent.ACTION_DOWN) {
                        if (isMove[0]) {
                            isMove[0] = false;
                            if ((int) v.getX() < endX / 2) {
                                canvas.setX(0);
                            } else canvas.setX(endX);
                        }else {
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.instructure.candroid");
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                            } else {
                                Toast.makeText(Courses.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.instructure.candroid")));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.instructure.candroid")));
                                }
                            }
                        }
                    }
                }
                return true;
            }
        });
    }

    public void back(View view){
        finish();
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentName = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String name) {
            fragments.add(fragment);
            fragmentName.add(name);
        }

        public void clearFragment() {
            fragments.clear();
            fragmentName.clear();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentName.get(position);
        }
    }

}