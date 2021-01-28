package com.example.myrmit.bookingActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.myrmit.R;
import com.example.myrmit.model.arrayAdapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import android.os.Bundle;

import java.util.Objects;

public class BookingActivity extends AppCompatActivity {
    /**
     * On create function
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        // Configure all the components
        ViewPager viewPager = findViewById(R.id.pager2);
        TabLayout tabLayout = findViewById(R.id.tab_layout2);
        // Get the instance of the fragments
        BookingRoomFragment room_fragment = new BookingRoomFragment();
        BookingLecturerFragment lecturer_fragment = new BookingLecturerFragment();
        // Set the fragment
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