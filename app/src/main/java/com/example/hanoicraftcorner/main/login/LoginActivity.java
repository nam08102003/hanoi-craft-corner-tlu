package com.example.hanoicraftcorner.main.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hanoicraftcorner.R;
import com.example.hanoicraftcorner.main.MainActivity;
import com.example.hanoicraftcorner.main.forgotpassword.ForgotPasswordActivity;
import com.example.hanoicraftcorner.main.register.Register;
import com.example.hanoicraftcorner.main.register.RegisterArtisan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInUser();
            }
        });
        regisArtisan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterArtisan.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Register.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void logInUser() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (emailText.isEmpty()) {
            email.setError("Vui lòng nhập email");
            email.requestFocus();
            return;
        }
        // Kiểm tra đúng cú pháp email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Email không hợp lệ");
            email.requestFocus();
            return;
        }
        if (passwordText.isEmpty()) {
            password.setError("Vui lòng nhập mật khẩu");
            password.requestFocus();
            return;
        }
        if (passwordText.length() < 6) {
            password.setError("Mật khẩu phải có ít nhất 6 ký tự");
            password.requestFocus();
            return;
        }
        auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sau khi đăng nhập thành công, lấy profiletype từ Firestore
                            com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
                            db.collection("Users")
                                    .whereEqualTo("email", emailText)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            com.google.firebase.firestore.DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                                            String profileType = userDoc.getString("profiletype");
                                            if (profileType == null) profileType = "user";
                                            Intent intent;
                                            switch (profileType) {
                                                case "Seller":
                                                    intent = new Intent(LoginActivity.this, com.example.hanoicraftcorner.main.MainBoardArtisan.class);
                                                    intent.putExtra("email", emailText);
                                                    break;
//                                                case "admin":
//                                                    intent = new Intent(LoginActivity.this, com.example.hanoicraftcorner.main.admin.AdminMainActivity.class);
//                                                    intent.putExtra("email", emailText);
//                                                    break;
                                                default:
                                                    intent = new Intent(LoginActivity.this, com.example.hanoicraftcorner.main.MainActivity.class);
                                                    intent.putExtra("email", emailText);
                                                    break;
                                            }
                                            startActivity(intent);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            finish();
                                        } else {
                                            password.setError("Không tìm thấy tài khoản trong hệ thống.");
                                            password.requestFocus();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        password.setError("Lỗi truy cập dữ liệu người dùng.");
                                        password.requestFocus();
                                    });
                        } else {
                            if (task.getException() != null &&
                                task.getException().getClass().getSimpleName().equals("FirebaseAuthInvalidCredentialsException")) {
                                password.setError("Mật khẩu không đúng. Vui lòng nhập lại.");
                            } else if (task.getException() != null &&
                                task.getException().getClass().getSimpleName().equals("FirebaseAuthInvalidUserException")) {
                                email.setError("Email không tồn tại trong hệ thống.");
                                // Gợi ý đăng ký hoặc đăng ký nghệ nhân
                                new android.app.AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Tài khoản không tồn tại")
                                    .setMessage("Bạn chưa có tài khoản? Đăng ký người dùng hoặc đăng ký nghệ nhân để tiếp tục.")
                                    .setPositiveButton("Đăng ký người dùng", (dialog, which) -> {
                                        Intent registerIntent = new Intent(LoginActivity.this, com.example.hanoicraftcorner.main.register.Register.class);
                                        registerIntent.putExtra("email", email.getText().toString());
                                        startActivity(registerIntent);
                                    })
                                    .setNegativeButton("Đăng ký nghệ nhân", (dialog, which) -> {
                                        Intent artisanRegisterIntent = new Intent(LoginActivity.this, com.example.hanoicraftcorner.main.register.RegisterArtisan.class);
                                        artisanRegisterIntent.putExtra("email", email.getText().toString());
                                        startActivity(artisanRegisterIntent);
                                    })
                                    .setNeutralButton("Để sau", null)
                                    .show();
                            } else {
                                password.setError("Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.");
                            }
                            password.requestFocus();
                        }
                    }
                });
    }

}
