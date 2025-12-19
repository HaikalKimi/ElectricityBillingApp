package com.example.electricitybillingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView githubLink = findViewById(R.id.tvGithub);
        githubLink.setOnClickListener(v -> {
            // Requirement: Clickable URL
            // Note: Once you create your GitHub repo, update this link!
            String url = "https://github.com/HaikalHakimi/ElectricityBillEstimator";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }
}