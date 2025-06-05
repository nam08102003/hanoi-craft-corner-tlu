package com.example.hanoicraftcorner.resetpassword;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hanoicraftcorner.R;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText editNewPassword = findViewById(R.id.edit_new_password);
        EditText editConfirmPassword = findViewById(R.id.edit_confirm_password);
        Button btnSubmit = findViewById(R.id.btn_submit);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        btnSubmit.setOnClickListener(v -> {
            editNewPassword.setError(null);
            editConfirmPassword.setError(null);
            String newPassword = editNewPassword.getText().toString().trim();
            String confirmPassword = editConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                if (newPassword.isEmpty()) editNewPassword.setError("Vui lòng nhập mật khẩu mới");
                if (confirmPassword.isEmpty()) editConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                editConfirmPassword.setError("Mật khẩu xác nhận không khớp");
                return;
            }

            if (email == null || email.isEmpty()) {
                editNewPassword.setError("Không tìm thấy email để đặt lại mật khẩu.");
                return;
            }

            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, newPassword)
                .addOnCompleteListener(signInTask -> {
                    if (signInTask.isSuccessful()) {
                        if (auth.getCurrentUser() != null) {
                            auth.getCurrentUser().updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Intent resetPasswordSuccessIntent = new Intent(ResetPasswordActivity.this, ResetPasswordSuccessActivity.class);
                                        startActivity(resetPasswordSuccessIntent);
                                    } else {
                                        editNewPassword.setError("Lỗi cập nhật mật khẩu. Vui lòng thử lại.");
                                    }
                                });
                        } else {
                            editNewPassword.setError("Không tìm thấy tài khoản để cập nhật mật khẩu.");
                        }
                    } else {
                        editNewPassword.setError("Lỗi xác thực tài khoản. Vui lòng thử lại.");
                    }
                });
        });
    }
}