package com.example.myrmit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myrmit.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class SignInActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static Activity activity;
    EditText username;
    EditText password;
    TextView status;
    FirebaseAuth myAuth = FirebaseAuth.getInstance();
    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        WelcomeActivity.activity.finish();
        activity = this;
        status = findViewById(R.id.textView13);
        username = findViewById(R.id.email);
        password = findViewById(R.id.password);
        FirebaseMessaging.getInstance().subscribeToTopic("news_notification");
        System.out.println("Firebase messaging subscribed");
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        }
        if (MainActivity.activity!= null){
            MainActivity.activity.finish();
        }
        ImageView signIn = (ImageView)findViewById(R.id.signin);
        signIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        //overlay is black with transparency of 0x77 (119)
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }

                return false;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void signIn(View view){
        if (!username.getText().toString().matches("([s]|[S])(\\d{7})+@rmit.edu.vn$")){
            status.setText("Invalid Email! Please enter an RMIT Email!");
        }
        else {
            if (!username.getText().toString().equals("") && !password.getText().toString().equals("")) {
                myAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            status.setText("Wrong username or password!");
                        }
                    }
                });
            }
        }
    }

    public void guestLogin(View view){
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
    }
}