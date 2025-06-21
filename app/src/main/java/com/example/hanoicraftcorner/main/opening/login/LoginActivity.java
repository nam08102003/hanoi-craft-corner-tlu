package com.example.hanoicraftcorner.main.opening.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.main.artisan.MainBoardArtisan;
import com.example.hanoicraftcorner.main.forgotpassword.ForgotPasswordActivity;
import com.example.hanoicraftcorner.main.opening.register.Register;
import com.example.hanoicraftcorner.main.opening.register.RegisterArtisan;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    Button logIn, regisArtisan;
    EditText email, password;
    TextView signUp,forgotPassword;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        logIn = findViewById(R.id.btn_login);
        regisArtisan = findViewById(R.id.btn_regis_artisan);
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        signUp = findViewById(R.id.sign_up);
        forgotPassword = findViewById(R.id.forgot_password);
        auth = FirebaseAuth.getInstance();

        // Prefill email if available from Intent extra
        String prefillEmail = getIntent().getStringExtra("email");
        if (prefillEmail != null) {
            email.setText(prefillEmail);
        }

        logIn.setOnClickListener(v -> logInUser());
        regisArtisan.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterArtisan.class);
            startActivity(intent);
        });
        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, Register.class);
            startActivity(intent);
        });
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void showError(EditText field, String message) {
        field.setError(message);
        field.requestFocus();
    }

    private void logInUser() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (emailText.isEmpty()) {
            showError(email, "Vui lòng nhập email");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            showError(email, "Email không hợp lệ");
            return;
        }
        if (passwordText.isEmpty()) {
            showError(password, "Vui lòng nhập mật khẩu");
            return;
        }
        if (passwordText.length() < 6) {
            showError(password, "Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }
        auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
                        db.collection("users")
                                .whereEqualTo("Email", emailText)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        com.google.firebase.firestore.DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                                        String profileType = userDoc.getString("Profiletype");
                                        if (profileType == null) profileType = "user";
                                        Intent intent;
                                        if ("Seller".equals(profileType)) {
                                            intent = new Intent(LoginActivity.this, MainBoardArtisan.class);
                                            intent.putExtra("email", emailText);
                                        } else {
                                            intent = new Intent(LoginActivity.this, com.example.hanoicraftcorner.main.MainActivity.class);
                                            intent.putExtra("email", emailText);
                                        }
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        showError(password, "Không tìm thấy tài khoản trong hệ thống.");
                                    }
                                })
                                .addOnFailureListener(e -> showError(password, "Lỗi truy cập dữ liệu người dùng."));
                    } else {
                        if (task.getException() != null &&
                                "FirebaseAuthInvalidCredentialsException".equals(task.getException().getClass().getSimpleName())) {
                            showError(password, "Mật khẩu không đúng. Vui lòng nhập lại.");
                        } else if (task.getException() != null &&
                                "FirebaseAuthInvalidUserException".equals(task.getException().getClass().getSimpleName())) {
                            showError(email, "Email không tồn tại trong hệ thống.");
                            new android.app.AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Tài khoản không tồn tại")
                                    .setMessage("Bạn chưa có tài khoản? Đăng ký người dùng hoặc đăng ký nghệ nhân để tiếp tục.")
                                    .setPositiveButton("Đăng ký người dùng", (dialog, which) -> {
                                        Intent registerIntent = new Intent(LoginActivity.this, Register.class);
                                        registerIntent.putExtra("email", email.getText().toString());
                                        startActivity(registerIntent);
                                    })
                                    .setNegativeButton("Đăng ký nghệ nhân", (dialog, which) -> {
                                        Intent artisanRegisterIntent = new Intent(LoginActivity.this, RegisterArtisan.class);
                                        artisanRegisterIntent.putExtra("email", email.getText().toString());
                                        startActivity(artisanRegisterIntent);
                                    })
                                    .setNeutralButton("Để sau", null)
                                    .show();
                        }
                    }
                });
    }

}
