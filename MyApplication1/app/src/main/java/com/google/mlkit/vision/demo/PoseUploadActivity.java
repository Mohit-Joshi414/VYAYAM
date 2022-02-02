package com.google.mlkit.vision.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PoseUploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView pose_image;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose_upload);

        EditText name, time, rha, lha, rka, lka, rsa, lsa, rea, lea, desc, procedure;
        Spinner facing, level;
        Button imageSelector, uploadBtn;

        name = findViewById(R.id.pose_name);
        time = findViewById(R.id.pose_time);
        rha = findViewById(R.id.pose_right_hip_angle);
        lha = findViewById(R.id.pose_left_hip_angle);
        rka = findViewById(R.id.pose_right_knee_angle);
        lka = findViewById(R.id.pose_left_knee_angle);
        rsa = findViewById(R.id.pose_right_shoulder_angle);
        lsa = findViewById(R.id.pose_left_shoulder_angle);
        rea = findViewById(R.id.pose_right_elbow_angle);
        lea = findViewById(R.id.pose_left_elbow_angle);
        desc = findViewById(R.id.pose_description);
        procedure = findViewById(R.id.pose_step_by_step_procedure);
        facing = findViewById(R.id.pose_facing);
        level = findViewById(R.id.pose_difficulty_level);
        pose_image = findViewById(R.id.pose_image);
        imageSelector = findViewById(R.id.pose_image_selector);
        uploadBtn = findViewById(R.id.pose_data_upload_btn);

        String[] facingList = {"Sideways", "Forward", "Backward"};
        String[] levelList = {"Beginner", "Intermediate", "Hard"};

        Map<String, String> map = new HashMap<>();

        ArrayAdapter<String> facingAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, facingList);
        facing.setAdapter(facingAdapter);
        facing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                map.put("pose_facing", facingList[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, levelList);
        level.setAdapter(levelAdapter);
        level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                map.put("difficulty_level", levelList[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        imageSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    map.put("pose_time", time.getText().toString());
                    map.put("rha", rha.getText().toString());
                    map.put("lha", lha.getText().toString());
                    map.put("rka", rka.getText().toString());
                    map.put("lka", lka.getText().toString());
                    map.put("rsa", rsa.getText().toString());
                    map.put("lsa", lsa.getText().toString());
                    map.put("rea", rea.getText().toString());
                    map.put("lea", lea.getText().toString());
                    map.put("pose_description", desc.getText().toString());
                    map.put("pose_procedure", procedure.getText().toString());


                    if (mImageUri != null) {
                        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("Pose_Data").child(name.getText().toString())
                                .child("example." + MimeTypeMap.getSingleton().getExtensionFromMimeType(PoseUploadActivity.this.getContentResolver().getType(mImageUri)));

                        StorageTask<?> uploadTask = fileRef.putFile(mImageUri);
                        uploadTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw Objects.requireNonNull(task.getException());
                                }

                                return  fileRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                map.put("pose_image", downloadUri.toString());
                                FirebaseDatabase.getInstance().getReference().child("Pose_Data").child(name.getText().toString()).setValue(map);
                            } else {
                                Toast.makeText(getApplicationContext(), "Upload failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(PoseUploadActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Glide.with(this).load(mImageUri).into(pose_image);
        }
    }
}