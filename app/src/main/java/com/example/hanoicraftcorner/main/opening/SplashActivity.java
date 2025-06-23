package com.example.hanoicraftcorner.main.opening;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;


import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.service.TypeWriterTextView;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);

            TypeWriterTextView splashText = findViewById(R.id.splashText);
        splashText.setCharacterDelay(70);
        splashText.animateText(getString(R.string.app_name));

        // Chờ hiệu ứng chữ xong rồi chuyển màn hình
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, getString(R.string.app_name).length() * 70L + 300);
    }
}
