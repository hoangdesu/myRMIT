package com.example.myrmit.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myrmit.MapsActivity;
import com.example.myrmit.bookingActivity.BookingActivity;
import com.example.myrmit.news.NewFeed;
import com.example.myrmit.R;
import com.example.myrmit.Services;
import com.example.myrmit.SignInActivity;
import com.example.myrmit.clubs.ClubsActivity;
import com.example.myrmit.coursesActivity.Courses;
import com.example.myrmit.model.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static Intent mServiceIntent;
    private ViewPager viewPager;
    @SuppressLint("StaticFieldLeak")
    public static Activity activity;
    private TabLayout tabLayout;
    private HomeFragment homeFragment;
    private RecordFragment recordFragment;

    private SensorManager sensorManager;
    private Sensor sensor;
    private long lastMeasuredTime = System.currentTimeMillis();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialSetting();
        setFragments();
    }


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

    private void setFragments(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(homeFragment, "");
        viewPagerAdapter.addFragment(recordFragment, "");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_home_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_record_24);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Courses.activity != null){
            Courses.activity.finish();
        }
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentName = new ArrayList<>();

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

    public void onCoursesClick(View view){
        Intent courses =  new Intent(MainActivity.this, Courses.class);
        startActivity(courses);
    }

    public void onNewsClick(View view){
        Intent intent =  new Intent(MainActivity.this, NewFeed.class);
        startActivity(intent);
    }

    public void onMapsClick(View view){
        Intent intent =  new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void onServicesClick(View view){
        Intent intent = new Intent(MainActivity.this, Services.class);
        startActivity(intent);
    }

    public void onClubsClick(View view) {
        Intent intent = new Intent(MainActivity.this, ClubsActivity.class);
        startActivity(intent);
    }

    public void onBookingClick(View view) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, BookingActivity.class);
            startActivity(intent);
        }
        else Toast.makeText(this, "Access Denied!", Toast.LENGTH_SHORT).show();
    }

    // Call once to add 14 clubs to "clubs" collection
    private String clubName;
    Map<String, Object> club;
    public void addClubs() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Random r = new Random();

        club = new HashMap<>();
        clubName = "Accounting Club";
        club.put("name", clubName);
        club.put("category", "Academic and business");
        club.put("location", "RMIT Saigon South");
        club.put("email", "accountingclub.sgs@rmit.edu.vn");
        club.put("createdDate", 2010);
        club.put("description", "The Accounting Club studies and practices accounting and finance, provides opportunities to members for self-development, offers a service and association among members and practicing professionals, and encourages a sense of ethical, social and public responsibility.");
        club.put("event", new ArrayList<>());
        club.put("host", "Mr. A");
        club.put("numberOfMembers", r.nextInt(10) + 10);
        club.put("vote", r.nextInt(6));

        db.collection("clubs").document(clubName).set(club)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding club", clubName + " added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Adding club", "Failed");
                    }
                });

        club = new HashMap<>();
        clubName = "BIS Club";
        club.put("name", clubName);
        club.put("category", "Academic and business");
        club.put("location", "RMIT Saigon South");
        club.put("email", "bisclub.sgs@rmit.edu.vn");
        club.put("createdDate", 2010);
        club.put("description", "The Student BIS Club supports students studying in RMIT Vietnam’s Business Information Systems program.With support from the BIS Department, the club organises regular Business Analysis guest speakers, holds workshops for students and organises regular industry tours.Our members are some of the most passionate and dedicated on campus.  We aim to help our members to fulfil our motto – to \"Build Incredible Success\".");
        club.put("event", new ArrayList<>());
        club.put("host", "s" + (r.nextInt(1000000) + 3000000));
        club.put("numberOfMembers", r.nextInt(100) + 10);
        club.put("vote", r.nextInt(6));

        db.collection("clubs").document(clubName).set(club)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding club", clubName + " added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Adding club", "Failed");
                    }
                });

        club = new HashMap<>();
        clubName = "BIS Club";
        club.put("name", clubName);
        club.put("category", "Academic and business");
        club.put("location", "RMIT Saigon South");
        club.put("email", "businessclub.sgs@rmit.edu.vn");
        club.put("createdDate", 2010);
        club.put("description", "The Business Club aims to create a community of students who work together as a team to learn how to conduct business matters in a hands-on approach.The Business Club provides students with a variety of new opportunities to utilise the skills that are learned in the classroom.The club encourages students to think beyond the individual functions of a business, and to understand the strategies that lead to sustained success over time. It prepares members to enter the business world by involving them in projects and activities where they're an essential part of the organisation.");
        club.put("event", new ArrayList<>());
        club.put("host", "s" + (r.nextInt(1000000) + 3000000));
        club.put("numberOfMembers", r.nextInt(100) + 10);
        club.put("vote", r.nextInt(6));

        db.collection("clubs").document(clubName).set(club)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding club", clubName + " added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Adding club", "Failed");
                    }
                });

        club = new HashMap<>();
        clubName = "Developer Club";
        club.put("name", clubName);
        club.put("category", "Academic and business");
        club.put("location", "RMIT Saigon South");
        club.put("email", "developerclub.sgs@rmit.edu.vn");
        club.put("createdDate", 2010);
        club.put("description", "A community to learn technology together, even from scratch. The club believes that technology is an art more than a rigid aspect of nature. ");
        club.put("event", new ArrayList<>());
        club.put("host", "s" + (r.nextInt(1000000) + 3000000));
        club.put("numberOfMembers", r.nextInt(100) + 10);
        club.put("vote", r.nextInt(6));

        db.collection("clubs").document(clubName).set(club)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding club", clubName + " added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Adding club", "Failed");
                    }
                });

        club = new HashMap<>();
        clubName = "FinTech Club";
        club.put("name", clubName);
        club.put("category", "Social and Special Interest");
        club.put("location", "RMIT Saigon South");
        club.put("email", "fintechclub.sgs@rmit.edu.vn");
        club.put("createdDate", 2010);
        club.put("description", "RMIT Vietnam FinTech Club is the first student-led FinTech initiative in Vietnam, which was formed by RMIT Vietnam students with 1001 interests and passions in Startups, Fintech, Finance, and innovative technology.RMIT Vietnam FinTech Club was launched with the goal to inspire, educate and increase the exposure of people to fintech and digital disruption via our workshops, meetups, page contents, conferences, bootcamps, and events.We are committed to 3 core values: education, career development, and ideation. In addition, we are dedicated to nurturing the next generation of business leaders, through holding internal trainings for members in FinTech Hub; and create social values through events such as FinTech Fair and FinTech Hackathon.");
        club.put("event", new ArrayList<>());
        club.put("host", "s" + (r.nextInt(1000000) + 3000000));
        club.put("numberOfMembers", r.nextInt(100) + 10);
        club.put("vote", r.nextInt(6));

        db.collection("clubs").document(clubName).set(club)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding club", clubName + " added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Adding club", "Failed");
                    }
                });

        club = new HashMap<>();
        clubName = "Japanese Culture Club";
        club.put("name", clubName);
        club.put("category", "Social and Special Interest");
        club.put("location", "RMIT Saigon South");
        club.put("email", "japanesecultureclub.sgs@rmit.edu.vn");
        club.put("createdDate", 2010);
        club.put("description", "The Japanese Culture Club focuses exclusively on Japan and its culture, and aims to promote and spread this knowledge across RMIT University.\n" +
                "\n" +
                "The club also holds many different events related to Japanese culture including (but not limited to): Maid Cafe, Tanabata, Onigiri Workshop, and White Day Chocolate Workshop.\n" +
                "\n" +
                "Club members share a strong bond by regularly connecting through small internal events separate to the larger external events. Many students in the Japanese Culture Club study a creative major which makes the club unique; members are able to learn and share creative ideas.");
        club.put("event", new ArrayList<>());
        club.put("host", "s" + (r.nextInt(1000000) + 3000000));
        club.put("numberOfMembers", r.nextInt(100) + 10);
        club.put("vote", r.nextInt(6));

        db.collection("clubs").document(clubName).set(club)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding club", clubName + " added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Adding club", "Failed");
                    }
                });

        club = new HashMap<>();
        clubName = "Badminton Club";
        club.put("name", clubName);
        club.put("category", "Sports");
        club.put("location", "RMIT Saigon South");
        club.put("email", "badmintonclub.sgs@rmit.edu.vn");
        club.put("createdDate", 2010);
        club.put("description", "\n" +
                "The Student Badminton Club, established early 2010, provides opportunities for students to develop their badminton skills, meet new friends and improve their fitness.\n" +
                "\n" +
                "Internal tournaments are hosted each semester and everyone is welcome to join. We also sometimes join with badminton teams from other universities around Ho Chi Minh City to have friendly matches, make new friends and improve our skills.\n" +
                "\n" +
                "The Badminton Club is open to both male and female members and operates on the Saigon South Campus.");
        club.put("event", new ArrayList<>());
        club.put("host", "s" + (r.nextInt(1000000) + 3000000));
        club.put("numberOfMembers", r.nextInt(100) + 10);
        club.put("vote", r.nextInt(6));

        db.collection("clubs").document(clubName).set(club)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding club", clubName + " added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Adding club", "Failed");
                    }
                });

        club = new HashMap<>();
        clubName = "Basketball Club";
        club.put("name", clubName);
        club.put("category", "Sports");
        club.put("location", "RMIT Saigon South");
        club.put("email", "malebasketballclub.sgs@rmit.edu.vn");
        club.put("createdDate", 2010);
        club.put("description", "\n" +
                "The Male Basketball Club creates a healthy playground for male students to develop their fitness, enhance social activities and improve their basketball skills.\n" +
                "\n" +
                "This is a great team sport that is open to all ability levels. We currently have several men’s teams that compete in both internal and external tournaments.\n" +
                "\n" +
                "As well as organising and hosting one of Ho Chi Minh City’s most popular amateur basketball tournaments, we’re proud to have been declared the city’s champions in the A Team division for 2012. For our Men’s A Team, this is now the third year in a row that we have achieved this glory.\n" +
                "\n" +
                "Basketball Clubs for both female and male students operate on Saigon South campuses.");
        club.put("event", new ArrayList<>());
        club.put("host", "s" + (r.nextInt(1000000) + 3000000));
        club.put("numberOfMembers", r.nextInt(100) + 10);
        club.put("vote", r.nextInt(6));

        db.collection("clubs").document(clubName).set(club)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding club", clubName + " added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Adding club", "Failed");
                    }
                });

        club = new HashMap<>();
        clubName = "Music Club";
        club.put("name", clubName);
        club.put("category", "Creative Collectives");
        club.put("location", "RMIT Saigon South");
        club.put("email", "musicclub.sgs@rmit.edu.vn");
        club.put("createdDate", 2010);
        club.put("description", "The RMIT Music Club at the Saigon South campus consists of a community of students who love music.\n" +
                "\n" +
                "Past events include Battle of the Bands and big scale seasonal concerts which attract many people who give positive feedback.");
        club.put("event", new ArrayList<>());
        club.put("host", "s" + (r.nextInt(1000000) + 3000000));
        club.put("numberOfMembers", r.nextInt(100) + 10);
        club.put("vote", r.nextInt(6));

        db.collection("clubs").document(clubName).set(club)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding club", clubName + " added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Adding club", "Failed");
                    }
                });

        club = new HashMap<>();
        clubName = "Mass Media Club";
        club.put("name", clubName);
        club.put("category", "Creative Collectives");
        club.put("location", "RMIT Saigon South");
        club.put("email", "massmediaclub.sgs@rmit.edu.vn");
        club.put("createdDate", 2010);
        club.put("description", "The RMIT Mass Media Club was established in late 2017 to create an environment for those with an interest in mass media, and provide the RMIT community with experiences in fields such as photography, critical thinking, writing, public speaking and networking.\n" +
                "\n" +
                "The club engages multiple areas, channels and departments to support members as they learn, and publishes their own local newspaper.");
        club.put("event", new ArrayList<>());
        club.put("host", "s" + (r.nextInt(1000000) + 3000000));
        club.put("numberOfMembers", r.nextInt(100) + 10);
        club.put("vote", r.nextInt(6));

        db.collection("clubs").document(clubName).set(club)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding club", clubName + " added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Adding club", "Failed");
                    }
                });

        club = new HashMap<>();
        clubName = "Business Club";
        club.put("name", clubName);
        club.put("category", "Academic and business");
        club.put("location", "RMIT Saigon South");
        club.put("email", "businessclub.sgs@rmit.edu.vn");
        club.put("createdDate", 2010);
        club.put("description", "The Business Club aims to create a community of students who work together as a team to learn how to conduct business matters in a hands-on approach.\n" +
                "\n" +
                "The Business Club provides students with a variety of new opportunities to utilise the skills that are learned in the classroom.\n" +
                "\n" +
                "The club encourages students to think beyond the individual functions of a business, and to understand the strategies that lead to sustained success over time. It prepares members to enter the business world by involving them in projects and activities where they're an essential part of the organisation.");
        club.put("event", new ArrayList<>());
        club.put("host", "s" + (r.nextInt(1000000) + 3000000));
        club.put("numberOfMembers", r.nextInt(100) + 10);
        club.put("vote", r.nextInt(6));

        db.collection("clubs").document(clubName).set(club)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding club", clubName + " added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Adding club", "Failed");
                    }
                });

        club = new HashMap<>();
        clubName = "Cheerleading Club";
        club.put("name", clubName);
        club.put("category", "Sports");
        club.put("location", "RMIT Saigon South");
        club.put("email", "cheerleadingclub.sgs@rmit.edu.vn");
        club.put("createdDate", 2010);
        club.put("description", "Looking for a sport that's a little bit different? Want to keep up your gymnastics or dance skills?\n" +
                        "\n" +
                        "Have you watched Bring It On many times and always wanted to do what they do?\n" +
                        "\n" +
                        "If you're looking for a workout that's so much fun you won't even realise you're exercising your whole body, join us at the RMIT Cheerleading Club.");
        club.put("event", new ArrayList<>());
        club.put("host", "s" + (r.nextInt(1000000) + 3000000));
        club.put("numberOfMembers", r.nextInt(100) + 10);
        club.put("vote", r.nextInt(6));

        db.collection("clubs").document(clubName).set(club)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding club", clubName + " added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Adding club", "Failed");
                    }
                });

        club = new HashMap<>();
        clubName = "Ultimate Frisbee Club";
        club.put("name", clubName);
        club.put("category", "Sports");
        club.put("location", "RMIT Saigon South");
        club.put("email", "frisbeeclub.sgs@rmit.edu.vn");
        club.put("createdDate", 2010);
        club.put("description", "The Ultimate Frisbee Club brings an exciting new sport to RMIT Vietnam. It provides a fantastic arena for all students to develop their teamwork as it is a mixed gender sport.\n" +
                "\n" +
                "The club participates in numerous annual tournaments, such as the Singapore Open Tournament and the Vietnam HAT Tournament. In 2012, we even travelled to the Philippines to participate in the International Manila Spirits Tournament. We competed against around 70 other teams from around the world... and took out the championship in the mixed division!\n" +
                "\n" +
                "The club also hosts mini tournaments and frisbee workshops.");
        club.put("event", new ArrayList<>());
        club.put("host", "s" + (r.nextInt(1000000) + 3000000));
        club.put("numberOfMembers", r.nextInt(100) + 10);
        club.put("vote", r.nextInt(6));

        db.collection("clubs").document(clubName).set(club)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding club", clubName + " added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Adding club", "Failed");
                    }
                });

        club = new HashMap<>();
        clubName = "Neo Culture Tech Club";
        club.put("name", clubName);
        club.put("category", "Academic and business");
        club.put("location", "RMIT Saigon South");
        club.put("email", "neoculturetechclub.sgs@rmit.edu.vn");
        club.put("createdDate", 2010);
        club.put("description", "RMIT Neo Culture Tech is a community fueled by the passion for technology and innovations. Our club offers not only a playground for tech-enthusiasts but also a unique learning experience for all students. ");
        club.put("event", new ArrayList<>());
        club.put("host", "s" + (r.nextInt(1000000) + 3000000));
        club.put("numberOfMembers", r.nextInt(100) + 10);
        club.put("vote", r.nextInt(6));

        db.collection("clubs").document(clubName).set(club)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding club", clubName + " added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Adding club", "Failed");
                    }
                });
    }

}
