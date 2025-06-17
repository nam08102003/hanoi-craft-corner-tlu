package com.example.hanoicraftcorner.main.forgotpassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.main.resetpassword.ResetPasswordActivity;

public class ForgotPasswordVerifyActivity extends AppCompatActivity {

    private EditText editOtp1, editOtp2, editOtp3, editOtp4, editOtp5, editOtp6;

    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_verify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editOtp1 = findViewById(R.id.otp1);
        editOtp2 = findViewById(R.id.otp2);
        editOtp3 = findViewById(R.id.otp3);
        editOtp4 = findViewById(R.id.otp4);
        editOtp5 = findViewById(R.id.otp5);
        editOtp6 = findViewById(R.id.otp6);
        btnSubmit = findViewById(R.id.btn_submit);

        // Tự động focus sang ô tiếp theo khi nhập xong 1 số
        setOtpAutoFocus();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa lỗi cũ
                editOtp6.setError(null);
                String otp1 = editOtp1.getText().toString().trim();
                String otp2 = editOtp2.getText().toString().trim();
                String otp3 = editOtp3.getText().toString().trim();
                String otp4 = editOtp4.getText().toString().trim();
                String otp5 = editOtp5.getText().toString().trim();
                String otp6 = editOtp6.getText().toString().trim();

                if (otp1.isEmpty() || otp2.isEmpty() || otp3.isEmpty() || otp4.isEmpty() || otp5.isEmpty() || otp6.isEmpty()) {
                    editOtp6.setError("Vui lòng nhập đầy đủ mã OTP");
                    return;
                }

                String otpMerge = otp1 + otp2 + otp3 + otp4 + otp5 + otp6;
                Intent intent = getIntent();
                String sentOtp = intent.getStringExtra("otp");
                if (sentOtp == null) {
                    editOtp6.setError("Không tìm thấy mã OTP. Vui lòng thử lại.");
                    return;
                }
                if (sentOtp.equals(otpMerge)) {
                    Intent resetPasswordActivity = new Intent(ForgotPasswordVerifyActivity.this, ResetPasswordActivity.class);
                    startActivity(resetPasswordActivity);
                } else {
                    editOtp6.setError("Mã OTP không chính xác");
                }
            }
        });

    }

    // Hàm tự động focus sang ô tiếp theo khi nhập xong 1 số
    private void setOtpAutoFocus() {
        editOtp1.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                if (s.length() == 1) editOtp2.requestFocus();
            }
        });
        editOtp2.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                if (s.length() == 1) editOtp3.requestFocus();
            }
        });
        editOtp3.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                if (s.length() == 1) editOtp4.requestFocus();
            }
        });
        editOtp4.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                if (s.length() == 1) editOtp5.requestFocus();
            }
        });
        editOtp5.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                if (s.length() == 1) editOtp6.requestFocus();
            }
        });
    }
}