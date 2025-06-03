package com.example.hanoicraftcorner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hanoicraftcorner.Model.OtpManager;
import com.example.hanoicraftcorner.Service.GmailSender;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button btnContinue, btnBack;
    private EditText editEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnContinue = findViewById(R.id.btn_continue);
        editEmail = findViewById(R.id.edit_email);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                if (!isValidEmail(email)) {
                    runOnUiThread(() -> {
                        Toast.makeText(ForgotPasswordActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                new Thread(() -> {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    OtpManager otpManager = new OtpManager();
                    long expireMs = 60 * 1000;
                    String otp = otpManager.generateOtp();
                    String myEmail = BuildConfig.SMTP_EMAIL;
                    String myPassword = BuildConfig.SMTP_APP_PASSWORD;
                    Map<String, Object> data = new HashMap<>();
                    GmailSender sender = new GmailSender(myEmail, myPassword);
                    data.put("otp", otp);
                    data.put("createdAt", FieldValue.serverTimestamp());
                    data.put("expiresAt", new Timestamp(new Date(System.currentTimeMillis() + expireMs)));
                    db.collection("forgot_password_otps")
                            .document(email)
                            .set(data)
                            .addOnSuccessListener(aVoid -> {
                                sender.sendMailAsync(
                                        "Quên mật khẩu",
                                        "Mã OTP của bạn là: " + otp,
                                        email,
                                        new GmailSender.OnMailSentListener() {
                                            @Override
                                            public void onSuccess() {
                                                Intent verifyIntent = new Intent(ForgotPasswordActivity.this, ForgotPasswordVerifyActivity.class);
                                                verifyIntent.putExtra("email", email);
                                                startActivity(verifyIntent);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                runOnUiThread(() -> {
                                                    Toast.makeText(getApplicationContext(), "Lỗi gửi email: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                });
                                            }
                                        }
                                );
                            })
                            .addOnFailureListener(e -> {
                                Log.e("OTP", "Lỗi khi lưu OTP: " + e.getMessage());
                            });
                }).start();
            }
        });
    }

    public boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}