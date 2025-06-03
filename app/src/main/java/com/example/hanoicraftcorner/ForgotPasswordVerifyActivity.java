package com.example.hanoicraftcorner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPasswordVerifyActivity extends AppCompatActivity {

    private EditText editOtp1, editOtp2, editOtp3, editOtp4, editOtp5, editOtp6;

    private Button btnSubmit;
    FirebaseFirestore db;

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

        db = FirebaseFirestore.getInstance();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp1 = editOtp1.getText().toString().trim();
                String otp2 = editOtp2.getText().toString().trim();
                String otp3 = editOtp3.getText().toString().trim();
                String otp4 = editOtp4.getText().toString().trim();
                String otp5 = editOtp5.getText().toString().trim();
                String otp6 = editOtp6.getText().toString().trim();

                if (otp1.isEmpty() || otp2.isEmpty() || otp3.isEmpty() ||
                        otp4.isEmpty() || otp5.isEmpty() || otp6.isEmpty()) {
                    Toast.makeText(ForgotPasswordVerifyActivity.this, "Vui lòng nhập đầy đủ mã OTP", Toast.LENGTH_SHORT).show();
                    return;
                }
                String otpMerge = otp1 + otp2 + otp3 + otp4 + otp5 + otp6;

                Intent intent = getIntent();
                String email = intent.getStringExtra("email");

                db.collection("forgot_password_otps").document(email).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (!documentSnapshot.exists()) {
                                Toast.makeText(ForgotPasswordVerifyActivity.this, "Mã OTP không chính xác", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String savedOtp = documentSnapshot.getString("otp");
                            Timestamp expiresAt = documentSnapshot.getTimestamp("expiresAt");

                            if (savedOtp == null || expiresAt == null) {
                                Toast.makeText(ForgotPasswordVerifyActivity.this, "Mã OTP không chính xác", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            long now = System.currentTimeMillis();
                            if (now > expiresAt.toDate().getTime()) {
                                Toast.makeText(ForgotPasswordVerifyActivity.this, "Mã OTP không chính xác", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (savedOtp.equals(otpMerge)) {
                                db.collection("forgot_password_otps").document(email).delete();
                                Intent resetPasswordActivity = new Intent(ForgotPasswordVerifyActivity.this, ResetPasswordActivity.class);
                                resetPasswordActivity.putExtra("email", email);
                                startActivity(resetPasswordActivity);
                            } else {
                                Toast.makeText(ForgotPasswordVerifyActivity.this, "Mã OTP không chính xác", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ForgotPasswordVerifyActivity.this, "Có lỗi. Vui lòng thử lại sau ít phút", Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }
}