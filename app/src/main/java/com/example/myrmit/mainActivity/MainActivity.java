package com.example.myrmit.mainActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.myrmit.MapsActivity;
import com.example.myrmit.bookingActivity.BookingActivity;
import com.example.myrmit.news.NewFeed;
import com.example.myrmit.R;
import com.example.myrmit.ServicesActivity;
import com.example.myrmit.SignInActivity;
import com.example.myrmit.clubs.ClubsActivity;
import com.example.myrmit.coursesActivity.Courses;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private ViewPager viewPager;
    @SuppressLint("StaticFieldLeak")
    public static Activity activity;
    private TabLayout tabLayout;
    private HomeFragment homeFragment;
    private RecordFragment recordFragment;
    private SensorManager sensorManager;
    private Sensor sensor;
    private long lastMeasuredTime = System.currentTimeMillis();

    /**
     * On create function
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialSetting();
        setFragments();
    }

    /**
     * Initial setting for all necessary components
     */
    private void initialSetting() {
        // Setting components
        SignInActivity.activity.finish();
        activity = this;
        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tab_layout);
        homeFragment = new HomeFragment();
        recordFragment = new RecordFragment();
        tabLayout.setupWithViewPager(viewPager);

        // Setup sensor
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    /**
     * Setup all fragments
     */
    private void setFragments(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(homeFragment, "");
        viewPagerAdapter.addFragment(recordFragment, "");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_home_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_record_24);
    }

    /**
     * Listen the sensor even this activity is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Listen the sensor all the time
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (Courses.activity != null){          // If there is a click from notification (user are in Courses)
            Courses.activity.finish();          // Go back to the home page and destroy the courses activity
        }
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Listen for sensor's status change
     * @param event SensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        System.out.println("sensor value: " + event.values[0]);
        if ( event.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (event.values[0] < 7) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastMeasuredTime > 10000) {
                    lastMeasuredTime = currentTime;
                    Toast.makeText(this, "Turn on the light to protect your eyes!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * View pager adapter for mini fragment
     */
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

    /**
     * If the user want to go to the courses section
     * @param view View
     */
    public void onCoursesClick(View view){
        Intent courses =  new Intent(MainActivity.this, Courses.class);
        startActivity(courses);
    }

    /**
     * If the user want to go to the News section
     * @param view View
     */
    public void onNewsClick(View view){
        Intent intent =  new Intent(MainActivity.this, NewFeed.class);
        startActivity(intent);
    }

    /**
     * If the user want to go to the Map section
     * @param view View
     */
    public void onMapsClick(View view){
        Intent intent =  new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    /**
     * If the user want to go to the Services section
     * @param view View
     */
    public void onServicesClick(View view){
        Intent intent = new Intent(MainActivity.this, ServicesActivity.class);
        startActivity(intent);
    }

    /**
     * If the user want to go to the Clubs section
     * @param view View
     */
    public void onClubsClick(View view) {
        Intent intent = new Intent(MainActivity.this, ClubsActivity.class);
        startActivity(intent);
    }

    /**
     * If the user want to go to the Booking section
     * @param view View
     */
    public void onBookingClick(View view) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, BookingActivity.class);
            startActivity(intent);
        }
        else Toast.makeText(this, "Access Denied!", Toast.LENGTH_SHORT).show();
    }

}
