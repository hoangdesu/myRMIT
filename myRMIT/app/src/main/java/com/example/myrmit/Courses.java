package com.example.myrmit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class Courses extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView canvas;
    OES_Fragment oes_fragment;
    TimetableFragment timetableFragment;
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
        timetableFragment = new TimetableFragment();
        viewPager = findViewById(R.id.pager1);
        tabLayout = findViewById(R.id.tab_layout1);
        canvas = findViewById(R.id.imageView2);
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        tabLayout.setupWithViewPager(viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(oes_fragment, "");
        viewPagerAdapter.addFragment(allocationFragment, "");
        viewPagerAdapter.addFragment(timetableFragment,"");
        viewPager.setAdapter(viewPagerAdapter);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.oes);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.allocation);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.timetable);
        final int[] isMove = {0};
        canvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isStart){
                    endX = (int) v.getX();
                    isStart = true;
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    isMove[0]++;
                    int x_cord = (int) event.getRawX();
                    int y_cord = (int) event.getRawY();
                    canvas.setX(x_cord - 75);
                    if (y_cord - 115 > 0 && y_cord - 155 < viewPager.getHeight()){
                        canvas.setY(y_cord - 145);
                    }
                    else if (y_cord - 115 < 0){
                        canvas.setY(0);
                    }
                    else canvas.setY(viewPager.getHeight());
                }
                else {
                    if (event.getAction() != MotionEvent.ACTION_DOWN) {
                        if ((int) v.getX() < endX / 2) {
                            canvas.setX(0);
                        }
                        else canvas.setX(endX);
                        if (isMove[0] < 4 ) {
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
                        isMove[0] = 0;
                    }
                }
                return true;
            }
        });
    }

    public void back(View view){
        finish();
    }


}