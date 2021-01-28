package com.example.myrmit.bookingActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.myrmit.R;
import com.example.myrmit.model.arrayAdapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import android.os.Bundle;
import android.widget.ImageView;

import java.util.Objects;

public class BookingActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        viewPager = findViewById(R.id.pager2);
        tabLayout = findViewById(R.id.tab_layout2);
        BookingRoomFragment room_fragment = new BookingRoomFragment();
        BookingLecturerFragment lecturer_fragment = new BookingLecturerFragment();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(room_fragment, "");
        viewPagerAdapter.addFragment(lecturer_fragment, "");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        // Set icon for all fragments
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.bookroom);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.booklecturer);

    }
}