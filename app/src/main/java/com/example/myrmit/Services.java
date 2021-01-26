package com.example.myrmit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.example.myrmit.model.arrayAdapter.ArrayAdapterService;
import com.example.myrmit.model.FirebaseHandler;
import com.example.myrmit.model.objects.RMITService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Services extends AppCompatActivity {
    ListView listView;
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        listView = findViewById(R.id.serviceView);
        setList();
    }
    private void setList(){
        firebaseHandler.getRMITServices().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<RMITService> list = new ArrayList<>();
                String temp;
                String description;
                for (DocumentSnapshot documentSnapshot : task.getResult()){
                    description = "";
                    temp = (String) documentSnapshot.get("description");
                    for (int i = 0 ; i< temp.split("~").length; i++){
                        if (i != temp.split("~").length-1) {
                            description += temp.split("~")[i] + "\n";
                        }
                        else description += temp.split("~")[i];
                    }
                    list.add(get((String) documentSnapshot.get("name"), (String) documentSnapshot.get("time"), (String) documentSnapshot.get("location"), (String) documentSnapshot.get("phone"), description));
                }
                ArrayAdapterService adapter = new ArrayAdapterService(Services.this, list);
                listView.setAdapter(adapter);
            }
        });
    }

    private RMITService get(String name, String time, String location, String phone, String description) {
        return new RMITService(name, time, location, phone, description);
    }
}