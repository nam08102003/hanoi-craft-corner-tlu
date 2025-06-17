package com.example.hanoicraftcorner.main.opening;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.view.View;
import android.widget.Button;

import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.main.login.LoginActivity;

public class WelcomeActivity extends AppCompatActivity {
    Button startButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final View welcomeText = findViewById(R.id.welcomeText);
        final View welcomeImage = findViewById(R.id.welcomeImage);
        final View startButton = findViewById(R.id.startButton);

        // Ẩn ban đầu
        welcomeText.setVisibility(View.INVISIBLE);
        welcomeImage.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.INVISIBLE);

        // Hiện lần lượt từng thành phần với hiệu ứng mượt hơn bằng ViewPropertyAnimator
        welcomeText.postDelayed(() -> {
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

        // XÓA preload LoginActivity dưới nền để tránh nhảy thẳng sang login
        // ...KHÔNG preload, chỉ chuyển khi ấn nút...
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        // Trong lúc chạy animation, load layout LoginActivity vào background (không start activity)
        // Việc inflate layout chỉ nên thực hiện trên UI thread để tránh lỗi trên một số thiết bị
        welcomeText.post(() -> {
            android.view.LayoutInflater inflater = getLayoutInflater();
            inflater.inflate(R.layout.activity_login, null);
        });
    }
}
