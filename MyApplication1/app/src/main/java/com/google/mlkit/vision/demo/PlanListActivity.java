package com.google.mlkit.vision.demo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.demo.ui.home.HomeAdapter;
import com.google.mlkit.vision.demo.ui.home.HomeGetterSetter;

import java.util.ArrayList;
import java.util.List;

public class PlanListActivity extends AppCompatActivity {

    private HomeAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);

        RecyclerView poseListRV = findViewById(R.id.pose_list);
        poseListRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        List<HomeGetterSetter> poseList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Pose_Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()){
                    HomeGetterSetter homeGetterSetter = new HomeGetterSetter();

                    homeGetterSetter.setPoseName(s.getKey());
                    homeGetterSetter.setPoseImage(s.child("pose_image").getValue().toString());

                    homeGetterSetter.setRha(Double.parseDouble(s.child("rha").getValue().toString()));
                    homeGetterSetter.setLha(Double.parseDouble(s.child("lha").getValue().toString()));
                    homeGetterSetter.setRka(Double.parseDouble(s.child("rka").getValue().toString()));
                    homeGetterSetter.setLka(Double.parseDouble(s.child("lka").getValue().toString()));
                    homeGetterSetter.setRsa(Double.parseDouble(s.child("rsa").getValue().toString()));
                    homeGetterSetter.setLsa(Double.parseDouble(s.child("lsa").getValue().toString()));
                    homeGetterSetter.setRea(Double.parseDouble(s.child("rea").getValue().toString()));
                    homeGetterSetter.setLea(Double.parseDouble(s.child("lea").getValue().toString()));

                    poseList.add(homeGetterSetter);
                }

                if(adapter == null){
                    adapter = new HomeAdapter(poseList);
                } else {
                    adapter.notifyDataSetChanged();
                }

                poseListRV.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
