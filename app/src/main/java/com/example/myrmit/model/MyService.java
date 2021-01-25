package com.example.myrmit.model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.myrmit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MyService extends Service {
    static int id = 2;
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    ArrayList<String> list;
    ArrayList<String> member;
    ArrayList<ListenerRegistration> listenerRegistration;
    @Override
    public void onCreate() {
        super.onCreate();
        listenerRegistration = new ArrayList<ListenerRegistration>();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    /**
     * Start foreground notification which is the base notification
     * To notify the user that the app is run in background
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String channelId = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1, notification);
    }

    /**
     * On Start
     * @param intent Intent
     * @param flags int
     * @param startId int
     * @return START_NOT_STICKY
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // Super user does not have these abilities.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            if (!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("vietnamsachvaxanh@gmail.com")) {
//                listenLocationChange();
//                listenNewCleaningSite();
//                listenCleaningSite();
//            }
        }
        return START_NOT_STICKY;
    }

    /**
     * Automatically generate id for notification
     * Because the limit is 24, so it have to overwrite to the oldest one
     */
    private void idHandler(){
        id++;
        if (id == 23){
            id = 2;
        }
    }

//    /**
//     * Listen and get the notification if there is a new cleaning site.
//     */
//    private void listenNewCleaningSite(){
//        listenerRegistration.add(firebaseHandler.findUser().addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    return;
//                }
//                assert value != null;
//                if (value.getDocumentChanges().size() > 0) {
//                    DocumentChange change = value.getDocumentChanges().get(value.getDocumentChanges().size() - 1);
//                    if (change.getDocument().getId().equals("vietnamsachvaxanh@gmail.com")) {
//                        NotificationManager mNotificationManager;
//                        NotificationCompat.Builder mBuilder =
//                                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
//                        Intent ii = new Intent(getApplicationContext(), WelcomeScreen.class);
//                        PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, ii, 0);
//                        if (WelcomeScreen.activity == null) {
//                            mBuilder.setContentIntent(pendingIntent);
//                        }
//                        mBuilder.setSmallIcon(R.drawable.icon);
//                        mBuilder.setContentTitle("New Cleaning site!");
//                        mBuilder.setContentText("Go to the map and check it out!");
//                        mBuilder.setPriority(Notification.PRIORITY_MAX);
//                        mNotificationManager =
//                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            String channelId = "My_channel_id";
//                            NotificationChannel channel = new NotificationChannel(
//                                    channelId,
//                                    "Bao's Channel",
//                                    NotificationManager.IMPORTANCE_HIGH);
//                            mNotificationManager.createNotificationChannel(channel);
//                            mBuilder.setChannelId(channelId);
//                        }
//                        mNotificationManager.notify(id, mBuilder.build());
//                        idHandler();
//                    }
//                }
//            }
//        }));
//    }
//
//    /**
//     * Listen to cleaning site that the user is in
//     */
//    private void listenCleaningSite(){
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            firebaseHandler.findUser().document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    list  = (ArrayList<String>) task.getResult().get("host");
//                    member = (ArrayList<String>) task.getResult().get("member");
//                    assert list != null;
//                    for (int i = 0; i< Objects.requireNonNull(member).size(); i++){
//                        list.add(member.get(i));
//                    }
//                    for (String path : list){
//                        listenCleaningSiteChange(path);
//                    }
//                }
//            });
//        }
//    }
//
//    /**
//     * Start listening
//     * @param path String
//     */
//    private void listenCleaningSiteChange(String path){
//        listenDataChange(path);
//    }
//
//    /**
//     * Listen data even
//     * @param path String
//     */
//    private void listenDataChange(String path){
//        listenerRegistration.add(firebaseHandler.findArea(path.split(",")[0], path.split(",")[1]).document(path.split(",")[2])
//                .collection("data").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                        if (error != null) {
//                            return;
//                        }
//                        assert value != null;
//                        if (value.getDocumentChanges().size() > 0) {
//                            DocumentChange change = value.getDocumentChanges().get(value.getDocumentChanges().size() - 1);
//                            if (change.getType() == DocumentChange.Type.MODIFIED) {
//                                if (change.getDocument().getId().equals("member")) {
//                                    listenToMemberJoin(change);
//                                }
//                                else if (change.getDocument().getId().equals("data")){
//                                    listenToIncomeChange(change);
//                                }
//                                else {
//                                    listenToLevelChange(change);
//                                }
//                            }
//                        }
//                    }
//                })
//        );
//    }
//
//    /**
//     * Listen and notify if there is a user join
//     * @param change DocumentChange
//     */
//    private void listenToMemberJoin(DocumentChange change){
//        Objects.requireNonNull(change.getDocument().getReference().getParent().getParent()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                NotificationManager mNotificationManager;
//                NotificationCompat.Builder mBuilder =
//                        new NotificationCompat.Builder(getApplicationContext(), "notify_001");
//                Intent ii = new Intent(getApplicationContext(), WelcomeScreen.class);
//                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, ii, 0);
//                if (WelcomeScreen.activity == null) {
//                    mBuilder.setContentIntent(pendingIntent);
//                }
//                mBuilder.setSmallIcon(R.drawable.icon);
//                ArrayList<String> list = (ArrayList<String>) change.getDocument().get("member");
//                assert list != null;
//                if (list.size() != 0) {
//                    String user = list.get(list.size() - 1);
//                    if (!user.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())) {
//                        mBuilder.setContentTitle("A new member has just joined the \"" + task.getResult().get("locationName") + "\"!");
//                        mBuilder.setContentText("Username: " + list.get(list.size() - 1));
//                    } else {
//                        mBuilder.setContentTitle("Join Successful!");
//                        mBuilder.setContentText("You have just joined the \"" + task.getResult().get("locationName") + "\"!");
//                    }
//                    mBuilder.setPriority(Notification.PRIORITY_MAX);
//                    mNotificationManager =
//                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                    mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        String channelId = "My_channel_id";
//                        NotificationChannel channel = new NotificationChannel(
//                                channelId,
//                                "Bao's Channel",
//                                NotificationManager.IMPORTANCE_HIGH);
//                        mNotificationManager.createNotificationChannel(channel);
//                        mBuilder.setChannelId(channelId);
//                    }
//                    mNotificationManager.notify(id, mBuilder.build());
//                }
//            }
//        });
//        idHandler();
//    }
//    /**
//     * Listen and notify if there is an income input
//     * @param change DocumentChange
//     */
//    private void listenToIncomeChange(DocumentChange change){
//        Objects.requireNonNull(change.getDocument().getReference().getParent().getParent()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                NotificationManager mNotificationManager;
//                NotificationCompat.Builder mBuilder =
//                        new NotificationCompat.Builder(getApplicationContext(), "notify_001");
//                Intent ii = new Intent(getApplicationContext(), WelcomeScreen.class);
//                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, ii, 0);
//                if (WelcomeScreen.activity == null) {
//                    mBuilder.setContentIntent(pendingIntent);
//                }
//                System.out.println("----------------");
//                mBuilder.setSmallIcon(R.drawable.icon);
//                ArrayList<String> list = (ArrayList<String>) change.getDocument().get("history");
//                assert list != null;
//                if (list.size() != 0) {
//                    String user = list.get((list.size() - 1)).split(",")[1];
//                    if (!user.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())) {
//                        mBuilder.setContentTitle("New Income Trash to \"" + task.getResult().get("locationName") + "\"!");
//                        mBuilder.setContentText("User: " + list.get(list.size() - 1).split(",")[1] + " --- Amount: " + list.get(list.size() - 1).split(",")[2]);
//                    } else {
//                        mBuilder.setContentTitle("Add Successful!");
//                        mBuilder.setContentText("Location: " + task.getResult().get("locationName") + " --- Amount: " + list.get(list.size() - 1).split(",")[2]);
//                    }
//                    mBuilder.setPriority(Notification.PRIORITY_MAX);
//                    mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//                    mNotificationManager =
//                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        String channelId = "My_channel_id";
//                        NotificationChannel channel = new NotificationChannel(
//                                channelId,
//                                "Bao's Channel",
//                                NotificationManager.IMPORTANCE_HIGH);
//                        mNotificationManager.createNotificationChannel(channel);
//                        mBuilder.setChannelId(channelId);
//                    }
//                    mNotificationManager.notify(id, mBuilder.build());
//                }
//            }
//        });
//        idHandler();
//    }
//
//    /**
//     * Listen and notify when there is a level change
//     * @param change DocumentChange
//     */
//    private void listenToLevelChange(DocumentChange change){
//        Objects.requireNonNull(change.getDocument().getReference().getParent().getParent()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                NotificationManager mNotificationManager;
//                NotificationCompat.Builder mBuilder =
//                        new NotificationCompat.Builder(getApplicationContext(), "notify_001");
//                Intent ii = new Intent(getApplicationContext(), WelcomeScreen.class);
//                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, ii, 0);
//                if (WelcomeScreen.activity == null) {
//                    mBuilder.setContentIntent(pendingIntent);
//                }
//                mBuilder.setSmallIcon(R.drawable.icon);
//                String level = (String) change.getDocument().get("level");
//                assert level != null;
//                if (!level.equals("done")) {
//                    String user = (String) task.getResult().get("host");
//                    assert user != null;
//                    if (!user.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())) {
//                        mBuilder.setContentTitle("Your host has just changed the level of site");
//                    } else {
//                        mBuilder.setContentTitle("Change level Successful!");
//                    }
//                    mBuilder.setContentText("Location: " + task.getResult().get("locationName") + " --- Level: " + task.getResult().get("level"));
//                }
//                else {
//                    notifySiteClosed(task, mBuilder);
//                }
//                mBuilder.setPriority(Notification.PRIORITY_MAX);
//                mNotificationManager =
//                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    String channelId = "My_channel_id";
//                    NotificationChannel channel = new NotificationChannel(
//                            channelId,
//                            "Bao's Channel",
//                            NotificationManager.IMPORTANCE_HIGH);
//                    mNotificationManager.createNotificationChannel(channel);
//                    mBuilder.setChannelId(channelId);
//                }
//                mNotificationManager.notify(id, mBuilder.build());
//
//            }
//        });
//        idHandler();
//    }
//
//    /**
//     * listen and notify when a site is closed (fresh)
//     * @param task Task<DocumentSnapshot>
//     * @param mBuilder NotificationCompat.Builder
//     */
//    private void notifySiteClosed(Task<DocumentSnapshot> task, NotificationCompat.Builder mBuilder){
//        if (task.getResult().get("host").equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())){
//            mBuilder.setContentTitle("Close the site Successful!");
//        }
//        else {
//            //Close the view Statistic page of all member are viewing to reload.
//            if (GroupView.activity != null){
//                GroupView.activity.finish();
//                GroupView.activity = null;
//            }
//            if (StatisticView.activity != null){
//                StatisticView.activity.finish();
//                StatisticView.activity = null;
//            }
//
//            mBuilder.setContentTitle("Your host has just closed the site");
//            String path = Objects.requireNonNull(task.getResult().getReference().getParent().getParent()).getId() + "," + task.getResult().getReference().getParent().getId() + "," +  task.getResult().getId();
//            firebaseHandler.findUser().document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    ArrayList<String> member = (ArrayList<String>) task.getResult().get("member");
//                    for (int i = 0; i < member.size() ; i++){
//                        String element = member.get(i);
//                        if (element.split(",")[0].equals(path.split(",")[0]) && element.split(",")[1].equals(path.split(",")[1]) && element.split(",")[2].equals(path.split(",")[2])){
//                            member.remove(i);
//                            break;
//                        }
//                    }
//                    task.getResult().getReference().update("member", member);
//                }
//            });
//        }
//        mBuilder.setContentText("Location: " + task.getResult().get("locationName") + " is clean!");
//    }
//
//    /**
//     * Listen and notify whenever there is a new district/new city has been add
//     */
//    private void listenLocationChange(){
//        listenerRegistration.add(firebaseHandler.getDb().collection("cleaningsites")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
//                                        @Nullable FirebaseFirestoreException e) {
//                        if (e != null) {
//                            return;
//                        }
//                        assert querySnapshot != null;
//                        DocumentChange change = querySnapshot.getDocumentChanges().get(querySnapshot.getDocumentChanges().size()-1);
//                        if (change.getType() == DocumentChange.Type.MODIFIED) {
//                            NotificationManager mNotificationManager;
//                            NotificationCompat.Builder mBuilder =
//                                    new NotificationCompat.Builder(getApplicationContext(), "notify_001");
//                            Intent ii = new Intent(getApplicationContext(), WelcomeScreen.class);
//                            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, ii, 0);
//                            if (WelcomeScreen.activity == null) {
//                                mBuilder.setContentIntent(pendingIntent);
//                            }
//                            mBuilder.setSmallIcon(R.drawable.icon);
//                            mBuilder.setContentTitle("New Location has been added!");
//                            ArrayList<String> district = (ArrayList<String>) change.getDocument().get("district");
//                            mBuilder.setContentText("Admin Area: " + change.getDocument().getId() + " --- Sub Admin Area: " + district.get(district.size()-1));
//                            mBuilder.setPriority(Notification.PRIORITY_MAX);
//                            mNotificationManager =
//                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                            {
//                                String channelId = "My_channel_id";
//                                NotificationChannel channel = new NotificationChannel(
//                                        channelId,
//                                        "Bao's Channel",
//                                        NotificationManager.IMPORTANCE_HIGH);
//                                mNotificationManager.createNotificationChannel(channel);
//                                mBuilder.setChannelId(channelId);
//                            }
//
//                            mNotificationManager.notify(id, mBuilder.build());
//                            idHandler();
//                        }
//                    }
//                })
//        );
//    }

    /**
     * auto-restart service
     */
    private void restartService(){
        for (ListenerRegistration registration : listenerRegistration) {
            registration.remove();
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartService");
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        broadcastIntent.setClass(MyService.this, RestartService.class);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        restartService();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
