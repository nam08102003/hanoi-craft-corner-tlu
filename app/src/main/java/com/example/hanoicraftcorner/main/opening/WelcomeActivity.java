package com.example.hanoicraftcorner.main.opening;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.view.View;
import android.widget.ProgressBar;

import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.main.opening.login.LoginActivity;

public class WelcomeActivity extends AppCompatActivity {
    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final View welcomeText = findViewById(R.id.welcomeText);
        final View welcomeImage = findViewById(R.id.welcomeImage);
        final View startButton = findViewById(R.id.startButton);
        final ProgressBar progressBar = findViewById(R.id.progressBar);

        // Ẩn ban đầu
        welcomeText.setVisibility(View.INVISIBLE);
        welcomeImage.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);

        // Tiến trình load thực tế
        final int[] progress = {0};
        final int step = 25; // 4 bước: login, register, register_artisan, forgot_password
        progressBar.setProgress(0);

        // Load từng layout, mỗi lần xong tăng bar
        android.view.LayoutInflater inflater = getLayoutInflater();
        progressBar.postDelayed(() -> {
            inflater.inflate(R.layout.activity_login, null);
            progress[0] += step;
            progressBar.setProgress(progress[0]);
        }, 100);
        progressBar.postDelayed(() -> {
            inflater.inflate(R.layout.activity_register, null);
            progress[0] += step;
            progressBar.setProgress(progress[0]);
        }, 200);
        progressBar.postDelayed(() -> {
            inflater.inflate(R.layout.activity_register_artisan, null);
            progress[0] += step;
            progressBar.setProgress(progress[0]);
        }, 300);
        progressBar.postDelayed(() -> {
            inflater.inflate(R.layout.activity_forgot_password, null);
            progress[0] += step;
            progressBar.setProgress(progress[0]);
            // Khi bar đầy, ẩn bar, chạy animation
            progressBar.postDelayed(() -> {
                progressBar.setVisibility(View.GONE);
                // Hiện lần lượt từng thành phần với hiệu ứng mượt hơn bằng ViewPropertyAnimator
                welcomeText.setVisibility(View.VISIBLE);
                welcomeText.setAlpha(0f);
                welcomeText.setTranslationX(200f);
                welcomeText.animate()
                        .translationX(0f)
                        .alpha(1f)
                        .setDuration(500)
                        .setStartDelay(0)
                        .start();
                welcomeText.postDelayed(() -> {
                    welcomeImage.setVisibility(View.VISIBLE);
                    welcomeImage.setAlpha(0f);
                    welcomeImage.setTranslationX(200f);
                    welcomeImage.animate()
                            .translationX(0f)
                            .alpha(1f)
                            .setDuration(500)
                            .setStartDelay(0)
                            .start();
                    welcomeImage.postDelayed(() -> {
                        startButton.setVisibility(View.VISIBLE);
                        startButton.setAlpha(0f);
                        startButton.setTranslationX(200f);
                        startButton.animate()
                                .translationX(0f)
                                .alpha(1f)
                                .setDuration(500)
                                .setStartDelay(0)
                                .start();
                    }, 500);
                }, 500);
            }, 200);
        }, 400);

        startButton.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }
}
