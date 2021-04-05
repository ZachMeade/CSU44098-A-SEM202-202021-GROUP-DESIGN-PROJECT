package com.example.beaumontocrapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_STRING = "com.android.beaumontocrapp.EXTRA_STRING";

    private Button captureButton, howToButton, analyticsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureButton = (Button) findViewById(R.id.captureButton);
        howToButton = (Button) findViewById(R.id.howToButton);
        analyticsButton = (Button) findViewById(R.id.analyticsButton);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCameraActivity();
            }
        });

        howToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHowTo();
            }
        });

    }

    private void openHowTo() {
        Intent intent = new Intent(this, InfoDisplay.class);
        startActivity(intent);
    }

    private void doCameraActivity() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
}