package com.example.myrmit.model.arrayAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myrmit.R;
import com.example.myrmit.model.FirebaseHandler;
import com.example.myrmit.model.objects.TutorItem;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class ArrayAdapterTutor extends android.widget.ArrayAdapter<TutorItem>{
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
    ArrayList<TutorItem> list;
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    Activity context;
    boolean isBooked;
    public ArrayAdapterTutor(Activity context, ArrayList<TutorItem> list, boolean isBooked) {
        super(context, R.layout.list_lecturer_booking, list);
        this.context = context;
        this.list = list;
        this.isBooked = isBooked;
    }
    static class ViewHolder {
        protected TextView name;
        protected TextView phone;
        protected TextView id;
        protected TextView mail;
        protected TextView status;
        protected View warn;
        protected ImageView call;
        protected ImageView email;
        protected TextView major;
        protected TextView time;
        protected Button book;
    }
    @SuppressLint({"InflateParams", "SetTextI18n", "ViewHolder"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = context.getLayoutInflater();
        View view = inflator.inflate(R.layout.list_lecturer_booking, null);
        final ViewHolder viewHolder = new ViewHolder();
        // Initial setting for all components
        viewHolder.name = view.findViewById(R.id.textView57);
        viewHolder.time = view.findViewById(R.id.textView64);
        viewHolder.phone = view.findViewById(R.id.textView69);
        viewHolder.warn = view.findViewById(R.id.view13);
        viewHolder.id = view.findViewById(R.id.textView58);
        viewHolder.mail = view.findViewById(R.id.textView72);
        viewHolder.status = view.findViewById(R.id.textView73);
        viewHolder.call = view.findViewById(R.id.imageView15);
        viewHolder.email = view.findViewById(R.id.imageView16);
        viewHolder.major = view.findViewById(R.id.textView61);
        viewHolder.book = view.findViewById(R.id.button10);
        // Set their behavior based on given data
        viewHolder.id.setText(String.valueOf(position+1));
        viewHolder.phone.setText("Phone: " + list.get(position).getPhone());
        viewHolder.mail.setText("Mail: " +list.get(position).getMail());
        if (isBooked){
            viewHolder.book.setEnabled(false);
        }
        if (list.get(position).getIsBook().equals("Available")) {
            viewHolder.warn.setBackgroundColor(Color.WHITE);
            viewHolder.status.setText("Status: " + list.get(position).getIsBook());
        }
        else {
            if (list.get(position).getIsBook().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                viewHolder.warn.setBackgroundColor(Color.YELLOW);
                viewHolder.book.setEnabled(true);
                viewHolder.book.setText("Cancel");
                viewHolder.status.setText("Status: In Booking");
            }
            else {
                viewHolder.book.setEnabled(false);
                viewHolder.status.setText("Status: Unavailable");
            }
        }
        viewHolder.major.setText("Major: " + list.get(position).getMajor());
        viewHolder.name.setText(list.get(position).getName());
        String startTime = list.get(position).getTime().get(0);
        String endTime = list.get(position).getTime().get(1);
        String day = "";
        for (int i = 0; i < list.get(position).getDay().size(); i++){
            if (i < list.get(position).getDay().size() - 1 ){
                day += list.get(position).getDay().get(i) + ", ";
            }
            else day += list.get(position).getDay().get(i);
        }
        viewHolder.time.setText("Available Time: " + startTime + " -> " + endTime + " ("+ day +")");

        viewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + list.get(position).getPhone()));
                context.startActivity(dialIntent);
            }
        });

        viewHolder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.microsoft.office.outlook");
                if (launchIntent != null) {
                    context.startActivity(launchIntent);
                } else {
                    Toast.makeText(context, "There is no package available in android", Toast.LENGTH_LONG).show();
                    final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.microsoft.office.outlook")));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.microsoft.office.outlook")));
                    }
                }
            }
        });

        viewHolder.book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.book.getText().equals("Book")){
                    firebaseHandler.getTutorList().collection("data").document(list.get(position).getMail()).update("date", sdf.format(Calendar.getInstance().getTime()));
                    firebaseHandler.getTutorList().collection("data").document(list.get(position).getMail()).update("isBook", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    Toast.makeText(context, "Book successful! Reloading page!", Toast.LENGTH_LONG).show();
                    context.recreate();
                }
                else if (viewHolder.book.getText().equals("Cancel")){
                    firebaseHandler.getTutorList().collection("data").document(list.get(position).getMail()).update("date", "");
                    firebaseHandler.getTutorList().collection("data").document(list.get(position).getMail()).update("isBook", "");
                    Toast.makeText(context, "Cancel successful! Reloading page!", Toast.LENGTH_LONG).show();
                    context.recreate();
                }
            }
        });

        return view;
    }
}
