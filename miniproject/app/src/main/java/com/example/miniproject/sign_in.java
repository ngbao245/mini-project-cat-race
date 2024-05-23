package com.example.miniproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class sign_in extends AppCompatActivity implements View.OnClickListener {
    // Views
    private EditText eUserName;
    private EditText ePassword;
    private AppCompatButton btnSignIn;

    // Notify
    private final String REQUIRE = "Require";

    // Predefined users
    private User[] users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Reference from layout
        eUserName = findViewById(R.id.editTextTextEmailAddress);
        ePassword = findViewById(R.id.editTextTextPassword);
        btnSignIn = findViewById(R.id.buttonSignIn);

        // Register event
        btnSignIn.setOnClickListener(this);

        // Initialize predefined users
        users = new User[] {
                new User("admin", "123"),
                new User("guest", "123"),
                new User("user", "user"),
        };
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(eUserName.getText().toString())) {
            eUserName.setError(REQUIRE);
            return false;
        }

        if (TextUtils.isEmpty(ePassword.getText().toString())) {
            ePassword.setError(REQUIRE);
            return false;
        }
        return true;
    }

    private void signIn() {
        if (!checkInput()) {
            return;
        }

        String email = eUserName.getText().toString();
        String password = ePassword.getText().toString();

        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                Intent intent = new Intent(this, Tutorial.class);
                startActivity(intent);
                finish();
                return;
            }
        }

        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSignIn) {
            signIn();
        }
    }
}
