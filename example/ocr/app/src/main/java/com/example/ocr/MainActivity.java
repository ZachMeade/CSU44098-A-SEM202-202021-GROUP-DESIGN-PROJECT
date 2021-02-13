package com.example.ocr;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.Manifest;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.PackageManager;

import android.view.SurfaceView;
import android.view.SurfaceHolder;

import android.util.SparseArray;

import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.io.IOException;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    final int CAMERA_PERM = 2;

    private TextRecognizer mTextRecognizer;
    private CameraSource mCameraSource;
    private SurfaceView mSurfaceView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        mTextView = (TextView) findViewById(R.id.textView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    setContentView(R.layout.camera);
                    mSurfaceView = (SurfaceView) findViewById((R.id.surfaceView));
                    mTextView = (TextView) findViewById(R.id.textView);
                    start_camera();
                } else {
                    get_camera_permission();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraSource.release();
    }

    private void start_camera(){
        mTextRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(mTextRecognizer.isOperational()){
            mCameraSource = new CameraSource.Builder(getApplicationContext(),mTextRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(15.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        try {
                            mCameraSource.start(mSurfaceView.getHolder());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        get_camera_permission();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

                mTextRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                    @Override
                    public void release() {
                    }

                    @Override
                    public void receiveDetections(Detector.Detections<TextBlock> detections) {
                        SparseArray<TextBlock> items = detections.getDetectedItems();
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < items.size(); ++i) {
                            TextBlock item = items.valueAt(i);
                            if (item != null && item.getValue() != null) {
                                stringBuilder.append(item.getValue() + " ");
                            }
                        }

                        final String fullText = stringBuilder.toString();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                mTextView.setText(fullText);
                            }
                        });

                    }
                });



        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults){


        if (requestCode == CAMERA_PERM && grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            start_camera();
            return;
        }

    }

    private void get_camera_permission(){
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, CAMERA_PERM);

            return;
        }
    }
}