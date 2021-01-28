package com.example.myrmit.model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.myrmit.R;
import com.example.myrmit.SignInActivity;
import com.example.myrmit.coursesActivity.Courses;
import com.example.myrmit.model.objects.Course;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The listener of the firebase, get the change and send to the user
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    static int id = 0;
    FirebaseHandler firebaseHandler = new FirebaseHandler();

    /**
     * Refresh the change listener
     * @param token String
     */
    @Override
    public void onNewToken(String token) {
        Log.d("TAG", "Refreshed token: " + token);
    }

    /**
     * Trigger if there is a change
     * @param remoteMessage RemoteMessage
     */
    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // If there is not a guest
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String type = remoteMessage.getData().get("type");
            // If there is a new change
            if (type!= null) {
                // If the change is from "news"
                if (type.equals("news")) {
                    // Get data and send notification to the user
                    String title = remoteMessage.getData().get("title");
                    String description = remoteMessage.getData().get("description");
                    sendNotification(description, title);
                } else {        // If the change is from "courses"
                    String course = remoteMessage.getData().get("course");
                    // Get data from change
                    firebaseHandler.getProgressingCourse(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            ArrayList<String> list = (ArrayList<String>) task.getResult().get("list");
                            assert list != null;
                            firebaseHandler.getAccount(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    String role = task.getResult().getString("role");
                                    if (role.equals("student")){        // Check if there is the student
                                        if (list.contains(course)) {    // And he/she is taking this course
                                            // Send notification
                                            sendNotification(course + " schedule has been updated", "Course Schedule Update");
                                            if (Courses.activity != null){              // If he/she currently in the allocation page, the app will reload it
                                                Toast.makeText(Courses.activity, "New Change! Reloading Page!", Toast.LENGTH_SHORT).show();
                                                Courses.activity.recreate();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    /**
     * Send notification with the given data
     * @param messageBody String
     * @param title String
     */
    private void sendNotification(String messageBody, String title) {
        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.myrmit);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(messageBody);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "My_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Bao's Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }
        mNotificationManager.notify(id, mBuilder.build());
        idHandler();
    }

    /**
     *  Set the id for the notification
     */
    private void idHandler(){
        id++;
        if (id == 23){
            id = 0;
        }
    }

}
