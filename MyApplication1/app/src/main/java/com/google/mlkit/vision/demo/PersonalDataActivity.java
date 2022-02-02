package com.google.mlkit.vision.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.mlkit.vision.demo.java.LivePreviewActivity;

public class PersonalDataActivity extends AppCompatActivity {

    private CardView gender,meal,age,height,weight;
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_personal_data);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("FirstTimeLoginCheck", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("check", false)) {
            Intent intent = new Intent(PersonalDataActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("check", true);

        Button done = findViewById(R.id.done_pd);
        gender = findViewById(R.id.cv1);
        age = findViewById(R.id.cv2);
        weight = findViewById(R.id.cv3);
        height = findViewById(R.id.cv4);
        meal = findViewById(R.id.cv5);

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalDataActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.customview_gender, viewGroup, false);
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();

            }
        });

        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalDataActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.customview_age, viewGroup, false);
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalDataActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.customview_weight, viewGroup, false);
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalDataActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.customview_height, viewGroup, false);
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalDataActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.customview_meal, viewGroup, false);
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.apply();
                Intent intent = new Intent(PersonalDataActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
