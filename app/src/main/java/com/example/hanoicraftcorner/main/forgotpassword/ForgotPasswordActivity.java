package com.example.hanoicraftcorner.main.forgotpassword;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hanoicraftcorner.BuildConfig;
import com.example.hanoicraftcorner.model.OtpManager;
import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.service.GmailSender;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class ForgotPasswordActivity extends AppCompatActivity {

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

        Button btnContinue = findViewById(R.id.btn_continue);
        EditText editEmail = findViewById(R.id.edit_email);

        btnContinue.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            if (!isValidEmail(email)) {
                editEmail.setError("Email không hợp lệ");
                return;
            }
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            if (result != null && result.getSignInMethods() != null && !result.getSignInMethods().isEmpty()) {
                                sendOtp(email, editEmail);
                            } else {
                                editEmail.setError("Email không tồn tại");
                            }
                        } else {
                            editEmail.setError("Lỗi kiểm tra email: " + (task.getException() != null ? task.getException().getMessage() : ""));
                        }
                    });
        });
    }

    private void sendOtp(String email, EditText editEmail) {
        OtpManager otpManager = new OtpManager();
        String otp = otpManager.generateOtp();
        String myEmail = BuildConfig.SMTP_EMAIL;
        String myPassword = BuildConfig.SMTP_APP_PASSWORD;
        GmailSender sender = new GmailSender(myEmail, myPassword);
        sender.sendMailAsync(
                "Quên mật khẩu",
                "Mã OTP của bạn là: " + otp,
                email,
                new GmailSender.OnMailSentListener() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            Intent verifyIntent = new Intent(ForgotPasswordActivity.this, ForgotPasswordVerifyActivity.class);
                            verifyIntent.putExtra("email", email);
                            verifyIntent.putExtra("otp", otp);
                            startActivity(verifyIntent);
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> editEmail.setError("Lỗi gửi email: " + e.getMessage()));
                    }
                }
        );
    }

    public boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}