package com.example.beaumontocrapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

public class CameraActivity extends AppCompatActivity {

    static final int EPISODE_LENGTH = 7;

    ImageView imgView;
    TextView txtView;
    Button correct, incorrect;
    String resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_display);

        imgView = findViewById(R.id.imageDisplay);
        txtView = findViewById(R.id.imageText);
        correct = findViewById(R.id.correctButton);
        incorrect = findViewById(R.id.incorrectButton);



        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 101);

        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDatabase();
            }
        });

        incorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNumber();
            }
        });
    }

    private void searchDatabase() {
        Intent intent = new Intent(this, SearchDatabase.class);
        intent.putExtra(MainActivity.EXTRA_STRING, resultText);
        startActivity(intent);
    }

    private void changeNumber() {
        Intent intent = new Intent(this, ChangeNumber.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle bundle = data.getExtras();

        Bitmap bitmap = (Bitmap) bundle.get("data");

        imgView.setImageBitmap(bitmap);

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        TextRecognizer recognizer = TextRecognition.getClient();
        
        Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text visionText) {
                resultText = "Error could not find episode ID code, please retake the photo.";
                Boolean allNumber = true;
                for(Text.TextBlock block : visionText.getTextBlocks()){
                    //String blockText = block.getText();
                    //Point[] blockCornerPoints = block.getCornerPoints();
                    //Rect blockFrame = block.getBoundingBox();
                    for(Text.Line line : block.getLines()){
                        //String lineText = line.getText();
                        //Point[] lineCornerPoints = line.getCornerPoints();
                        //Rect lineFrame = line.getBoundingBox();
                        for(Text.Element element : line.getElements()){
                            String elementText = element.getText();
                            //Point[] elementCornerPoints = element.getCornerPoints();
                            //Rect elementFrame = element.getBoundingBox();
                            if(elementText.length() == EPISODE_LENGTH){
                                char[] elementChars = elementText.toCharArray();
                                for(int i = 0; i < elementChars.length; i++){
                                    if(elementChars[i] < '0' || elementChars[i] > '9'){
                                        allNumber = false;
                                    }
                                }
                                if(allNumber == true){
                                    resultText = elementText;
                                }
                            }
                        }
                    }
                }
                txtView.setText(resultText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
