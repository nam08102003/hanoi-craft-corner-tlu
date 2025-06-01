package com.example.hanoicraftcorner;


import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.util.Log;

public class Register extends AppCompatActivity {
    ImageButton backButton;
    EditText usernameEditText,
             emailEditText,passwordEditText;
    Button registerButton;
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

        // Add this code inside onCreate after setContentView(...)
//        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
//        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                    User user = userSnapshot.getValue(User.class);
//                    Log.d("FIREBASE_USER", "Username: " + user.username + ", Email: " + user.email);
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w("FIREBASE_USER", "loadUser:onCancelled", databaseError.toException());
//            }
//        });


        backButton = findViewById(R.id.imageButton);
        usernameEditText = findViewById(R.id.inpUsername);
        emailEditText = findViewById(R.id.inpEmail);
        passwordEditText = findViewById(R.id.inpPassword);
        registerButton = findViewById(R.id.registerButton);

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
            if (password.isEmpty()){
                passwordEditText.setError("Không được để trống mật khẩu");
                return;
            }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            FirebaseDatabase.getInstance().getReference("users").child(userId)
                                .setValue(new User(username, email, password))
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        Log.d(TAG, "User registered successfully");
                                        Toast.makeText(this, "Registration successful.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.w(TAG, "createUserWithEmail:failure", dbTask.getException());
                                    }
                                });
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        });
    }
}