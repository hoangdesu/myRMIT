package com.example.myrmit.coursesActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;

import java.util.Objects;

import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myrmit.R;
import com.example.myrmit.ViewPagerAdapter;
import com.example.myrmit.model.arrayAdapter.ArrayAdapterCourses;
import com.example.myrmit.model.objects.CourseReview;
import com.example.myrmit.model.FirebaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Courses extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView canvas;
    private OES_Fragment oes_fragment;
    private ListView listview;
    private TimetableFragment timetableFragment;
    private int endX;
    private boolean isStart = false;
    private AllocationFragment allocationFragment;
    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            accountView();
        }
        else {
            guessView();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void accountView(){
        setContentView(R.layout.activity_courses);
        initialSetting();

        // Setup view adapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(oes_fragment, "");
        viewPagerAdapter.addFragment(allocationFragment, "");
        viewPagerAdapter.addFragment(timetableFragment, "");
        viewPager.setAdapter(viewPagerAdapter);

        // Set icon for all fragments
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.oes);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.allocation);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.timetable);

        // Set canvas access
        setCanvasAccess();
    }

    /**
     * Initial setting for RMIT account
     */
    private void initialSetting(){
        oes_fragment = new OES_Fragment();
        allocationFragment = new AllocationFragment();
        timetableFragment = new TimetableFragment();
        viewPager = findViewById(R.id.pager1);
        tabLayout = findViewById(R.id.tab_layout1);
        canvas = findViewById(R.id.imageView2);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Set onclick for the canvas icon to move to the app canvas
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setCanvasAccess(){
        final int[] isMove = {0};
        canvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isStart) {
                    endX = (int) v.getX();
                    isStart = true;
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    isMove[0]++;
                    int x_cord = (int) event.getRawX();
                    int y_cord = (int) event.getRawY();
                    canvas.setX(x_cord - 75);
                    if (y_cord - 115 > 0 && y_cord - 155 < viewPager.getHeight()) {
                        canvas.setY(y_cord - 145);
                    } else if (y_cord - 115 < 0) {
                        canvas.setY(0);
                    } else canvas.setY(viewPager.getHeight());
                } else {
                    if (event.getAction() != MotionEvent.ACTION_DOWN) {
                        if ((int) v.getX() < endX / 2) {
                            canvas.setX(0);
                        } else canvas.setX(endX);
                        if (isMove[0] < 4) {
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

    /**
     * Setup for guess account to view available programs
     */
    private void guessView(){
        // Initial setup
        FirebaseHandler firebaseHandler = new FirebaseHandler();
        setContentView(R.layout.activity_courses_guess);
        listview = findViewById(R.id.courseView);

        // get all the undergraduate programs
        firebaseHandler.getRMITPrograms().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {         // Set list view
                ArrayList<CourseReview> list = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot: task.getResult()){      // Set all detail with each program
                    list.add(get(documentSnapshot.getId(), (String)documentSnapshot.get("name"),getDescription(documentSnapshot), (ArrayList<String>) documentSnapshot.get("courses")));
                }
                if (list.size() == task.getResult().size()){                    // finalize the list
                    ArrayAdapterCourses arrayAdapter = new ArrayAdapterCourses(Courses.this, list);
                    listview.setAdapter(arrayAdapter);
                }
            }
        });
    }

    /**
     * Get description from firebase
     * @param documentSnapshot DocumentSnapshot
     * @return String
     */
    private String getDescription(DocumentSnapshot documentSnapshot){
        String description = (String) documentSnapshot.get("description");
        String[] temp = description.split("~");
        description = "";
        for (int i = 0; i < temp.length; i++){
            if (i != temp.length-1){
                description += temp[i] + "\n";
            }
            else description += temp[i];
        }
        return description;
    }

    /**
     * Get CourseView for guess account
     * @param code String
     * @param name String
     * @param description String
     * @param courses ArrayList<String>
     * @return CourseReview
     */
    private CourseReview get(String code, String name, String description, ArrayList<String> courses){
        return new CourseReview(name, description, courses, code);
    }

}