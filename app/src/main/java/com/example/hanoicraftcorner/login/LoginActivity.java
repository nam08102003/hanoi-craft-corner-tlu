package com.example.hanoicraftcorner.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hanoicraftcorner.R;
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
    }

    private void logInUser() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (emailText.isEmpty()) {
            email.setError("Please enter your email");
            email.requestFocus();
            return;
        }
        if (passwordText.isEmpty()) {
            password.setError("Please enter your password");
            password.requestFocus();
            return;
        }
        if (passwordText.length() < 6) {
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return;
        }
        auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        } else {
                            password.setError("Login failed. Please check your credentials.");
                            password.requestFocus();
                        }
                    }
        });

    }

}
