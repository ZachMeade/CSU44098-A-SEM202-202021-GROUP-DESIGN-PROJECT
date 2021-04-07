package com.example.beaumontocrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SearchDatabase extends AppCompatActivity {

    TextView numberDisplay, databaseDisplay;
    Button returnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_database);

        Intent intent = getIntent();
        String episodeNumber = intent.getStringExtra(MainActivity.EXTRA_STRING);

        databaseDisplay = (TextView) findViewById(R.id.databaseDisplay);
        numberDisplay = (TextView) findViewById(R.id.numberDisplay);
        numberDisplay.setText(episodeNumber);

        boolean isInDatabase = true;
        /*

        For query to database, should return boolean isInDatabase or notInDatabase

        */
        if(isInDatabase){
            databaseDisplay.setText("Bad");
            databaseDisplay.setTextColor(Color.parseColor("#BF6767"));
        }
        else{
            databaseDisplay.setText("Good");
            databaseDisplay.setTextColor(Color.parseColor("#93DF96"));
        }

        returnHome = (Button) findViewById(R.id.returnHome);

        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnHome();
            }
        });
    }

    private void returnHome() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
}