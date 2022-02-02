package com.google.mlkit.vision.demo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlaceholderFragment extends Fragment {

    private static final String ARG_PARAM = "index";
    private TextToSpeech textToSpeech;
    private int index;

    public PlaceholderFragment() {
        // Required empty public constructor
    }

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            index = getArguments().getInt(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root;

         if(index == 1) {
            root = inflater.inflate(R.layout.fragment_placeholder_tutorial, container, false);

            TextView title = root.findViewById(R.id.title);
            TextView description = root.findViewById(R.id.description);
            RecyclerView steps = root.findViewById(R.id.procedure);
            steps.setLayoutManager(new LinearLayoutManager(getContext()));
            List<String> steps_list = new ArrayList<>();
            List<String> step_image_list = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("Pose_Data").child(getActivity().getIntent().getStringExtra("name")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    title.setText("What is " + snapshot.getKey());
                    description.setText(snapshot.child("pose_description").getValue().toString());

                    for(DataSnapshot s : snapshot.child("steps").getChildren()){
                        steps_list.add(s.getValue().toString());
                    }

                    for(DataSnapshot s : snapshot.child("step_url").getChildren()){
                        step_image_list.add(s.getValue().toString());
                    }

                    BenefitsAdapter adapter = new BenefitsAdapter(steps_list, step_image_list);
                    steps.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if(index == 2){
            root = inflater.inflate(R.layout.fragment_placeholder_benefits, container, false);
            RecyclerView benefits = root.findViewById(R.id.benefits);
            benefits.setLayoutManager(new LinearLayoutManager(getContext()));
            List<String> benefits_list = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("Pose_Data").child(getActivity().getIntent().getStringExtra("name")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot s : snapshot.child("benefits").getChildren()){
                        benefits_list.add(s.getValue().toString());
                    }

                    BenefitsAdapter adapter = new BenefitsAdapter(benefits_list);
                    benefits.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } else{
            root = inflater.inflate(R.layout.fragment_placeholder_progress, container, false);

            GraphView graph = root.findViewById(R.id.graph);
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(0, 1),
                    new DataPoint(1, 5),
                    new DataPoint(2, 3),
                    new DataPoint(3, 2),
                    new DataPoint(4, 6)
            });
            graph.addSeries(series);
        }

        return root;
    }
}