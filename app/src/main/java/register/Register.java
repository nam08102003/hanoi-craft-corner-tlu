package register;


import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hanoicraftcorner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {
    ImageButton backButton;
    EditText usernameEditText,
             emailEditText,passwordEditText;
    Button registerButton;
    TextView ClickToLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        backButton = findViewById(R.id.imageButton);
        usernameEditText = findViewById(R.id.inpUsername);
        emailEditText = findViewById(R.id.inpEmail);
        passwordEditText = findViewById(R.id.inpPassword);
        registerButton = findViewById(R.id.registerButton);
        ClickToLogin = findViewById(R.id.ClickableText);

        backButton.setOnClickListener(v -> {
//            Update the code to handle back navigation
        });
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty()){
                usernameEditText.setError("Không được để trống tên người dùng");
                return;
            }
            if (email.isEmpty()){
                emailEditText.setError("Không được để trống email");
                return;
            }
            if (!email.contains("@") || !email.contains(".")) {
                emailEditText.setError("Email không hợp lệ");
                return;
            }

            if (password.isEmpty()){
                passwordEditText.setError("Không được để trống mật khẩu");
                return;
            }
            if (password.length() < 6) {
                passwordEditText.setError("Mật khẩu phải có ít nhất 6 ký tự");
                return;
            }
            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(authTask -> {
                        if (authTask.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("username", username);
                                userMap.put("email", email);
                                userMap.put("ProfileType", "user");

                                db.collection("users").document(userId)
                                        .set(userMap)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                runOnUiThread(() -> Toast.makeText(this, "Registration successful.",
                                                        Toast.LENGTH_SHORT).show());
                                            } else {
                                                Log.w(TAG, "createUserWithEmail:failure", dbTask.getException());
                                            }
                                        });
                            }
                            } else {
                                Exception exception = authTask.getException();
                                String errorMessage;
                                if (exception != null) {
                                    String msg = exception.getMessage();
                                    Log.w(TAG, "createUserWithEmail:failure", exception);
                                    if (msg != null && msg.contains("email address is already in use")) {
                                        runOnUiThread(() -> emailEditText.setError("Email đã được sử dụng"));
                                        return;
                                    } else {
                                        errorMessage = msg;
                                    }
                                } else {
                                    errorMessage = "Registration failed. Please try again.";
                                }
                                runOnUiThread(() -> Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show());
                            }
            });
        });
    }
    }
