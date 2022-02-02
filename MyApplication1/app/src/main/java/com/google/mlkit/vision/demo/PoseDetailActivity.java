package com.google.mlkit.vision.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.mlkit.vision.demo.java.LivePreviewActivity;

public class PoseDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("name"));
        System.out.println(getIntent().getStringExtra("name"));
        setSupportActionBar(toolbar);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getLifecycle());

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        TabLayout tabs = findViewById(R.id.tabs);
        String tabText[] = {"Tutorial", "Benefits", "Progress"};

        new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> tab.setText(tabText[position])
        ).attach();

        FloatingActionButton efab = findViewById(R.id.fab);
        efab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PoseDetailActivity.this, LivePreviewActivity.class);

                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("rha", getIntent().getDoubleExtra("rha", 0));
                intent.putExtra("lha", getIntent().getDoubleExtra("lha", 0));
                intent.putExtra("rka", getIntent().getDoubleExtra("rka", 0));
                intent.putExtra("lka", getIntent().getDoubleExtra("lka", 0));
                intent.putExtra("rsa", getIntent().getDoubleExtra("rsa", 0));
                intent.putExtra("lsa", getIntent().getDoubleExtra("lsa", 0));
                intent.putExtra("rea", getIntent().getDoubleExtra("rea", 0));
                intent.putExtra("lea", getIntent().getDoubleExtra("lea", 0));

                startActivity(intent);
            }
        });
    }
}
