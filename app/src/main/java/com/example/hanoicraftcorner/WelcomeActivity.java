package com.example.hanoicraftcorner;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {
    Button startButton;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    }


