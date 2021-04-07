package com.example.beaumontocrapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class ChangeNumber extends AppCompatActivity {

    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_number);

        confirm = (Button) findViewById(R.id.confirmNumber);
        
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDatabase();
            }
        });
    }

    private void searchDatabase() {
        EditText editText = (EditText) findViewById(R.id.newNumberInput);
        String episodeNumber = editText.getText().toString();

        Intent intent = new Intent(this, SearchDatabase.class);
        intent.putExtra(MainActivity.EXTRA_STRING, episodeNumber);
        startActivity(intent);
    }


}