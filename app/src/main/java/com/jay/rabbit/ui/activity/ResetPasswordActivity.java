package com.jay.rabbit.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.jay.rabbit.R;
import com.jay.rabbit.ui.activity.signinscreen.AuthorizationActivity;

public class ResetPasswordActivity extends AppCompatActivity {


    private EditText sendMailET;
    private Button resetPasswordBtn;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        sendMailET = findViewById(R.id.email_edit);
        resetPasswordBtn = findViewById(R.id.reset_password_button);

        auth = FirebaseAuth.getInstance();

        resetPasswordBtn.setOnClickListener(v -> {

            String email = sendMailET.getText().toString();

            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {

                if (task.isSuccessful()){
                    Toast.makeText(ResetPasswordActivity.this, "check your email"
                            ,Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ResetPasswordActivity.this, AuthorizationActivity.class));
                } else {
                    if (task.getException() != null) {
                        Toast.makeText(ResetPasswordActivity.this, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}
