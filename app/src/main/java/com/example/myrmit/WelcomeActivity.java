package com.example.myrmit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.InputStream;

public class WelcomeActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    /**
     * On create function
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Initial setting
        activity = this;
        Handler handler = new Handler();
        View view = findViewById(R.id.view2);

        // Allow user to skip the gif
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        // Set time, will move to next activity after the gif run
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        }, 4000);
    }

    /**
     * Setup gif image for displaying
     */
    public static class MYGIFView extends View {
        private Movie movie;
        long startTime;
        @SuppressLint("ResourceType")
        public MYGIFView(Context context) {
            super(context);
            init(context);
        }
        public MYGIFView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public MYGIFView(Context context, AttributeSet attrs,
                         int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }
        @SuppressLint("ResourceType")
        private void init(Context context){
            setFocusable(true);
            InputStream is = context.getResources().openRawResource(R.drawable.giphy);
            movie = Movie.decodeStream(is);
        }

        /**
         * Draw the gif image
         * @param canvas Canvas
         */
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.scale(((float)super.getWidth() /(float)movie.width()) ,(float)super.getWidth() /(float)movie.width());
            super.onDraw(canvas);
            long now = android.os.SystemClock.uptimeMillis();
            if (startTime == 0) { // first time
                startTime = now;
            }
            int relTime = (int)((now - startTime));
            movie.setTime(relTime);
            movie.draw(canvas,0,0);
            this.invalidate();
        }

    }
}