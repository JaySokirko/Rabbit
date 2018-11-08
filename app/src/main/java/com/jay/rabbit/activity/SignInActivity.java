package com.jay.rabbit.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jay.rabbit.R;


public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "LOG_TAG";
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;
    private AnimationDrawable backgroundAnimation;


    private EditText emailEditText;
    private EditText passwordEditText;
    private LinearLayout registrationLayout;
    private Button singUpBtn;
    private ImageView logoImView;
    private LinearLayout signInLayout;
    private Button forgotPassswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailEditText = findViewById(R.id.edit_text_login);
        passwordEditText = findViewById(R.id.edit_text_password);
        registrationLayout = findViewById(R.id.registration_form_layout);
        singUpBtn = findViewById(R.id.button_sing_up);
        logoImView = findViewById(R.id.image_view_logo);
        signInLayout = findViewById(R.id.layout_sign_in);
        forgotPassswordBtn = findViewById(R.id.button_forgot_password);
        RelativeLayout parentLayout = findViewById(R.id.parent_view);

        mAuth = FirebaseAuth.getInstance();

        backgroundAnimation = (AnimationDrawable) parentLayout.getBackground();
        backgroundAnimation.setExitFadeDuration(4000);


        somemethod();

        animationOnActivityStart();

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (backgroundAnimation != null && !backgroundAnimation.isRunning())
            backgroundAnimation.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (backgroundAnimation != null && backgroundAnimation.isRunning())
            backgroundAnimation.stop();
    }


    /**
     * Start animation on activity created
     */
    public void animationOnActivityStart() {

        final float x = getResources().getDimension(R.dimen.dim_100);
        final float y = getResources().getDimension(R.dimen.dim_100);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                logoImView.animate().setDuration(500).translationX(-x).translationY(-y).start();
            }
        },1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                registrationLayout.animate().setDuration(1000).alpha(1).start();
                singUpBtn.animate().setDuration(1000).alpha(1).start();
                signInLayout.animate().setDuration(1000).alpha(1).start();
                forgotPassswordBtn.animate().setDuration(1000).alpha(1).start();
            }
        }, 1500);
    }



    public void onSignInClick(View view){

        singUpBtn.setBackground(getResources().getDrawable(R.drawable.shape_rounded_rectangle_accent));

    }



    public void somemethod() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser != null) {


                } else {

                }
            }
        };
    }

    public void onRegistrationBtnClick(View view) {
        registration(emailEditText.getText().toString(), passwordEditText.getText().toString());
    }


    public void onAuthenticationBtnClick(View view) {
        authentication(emailEditText.getText().toString(), passwordEditText.getText().toString());
    }


    public void authentication(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignInActivity.this, "successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignInActivity.this, AuthorizedUserActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "unsuccessfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void registration(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignInActivity.this, "successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignInActivity.this, RegistratedUserActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "unsuccessfully", Toast.LENGTH_SHORT).show();
                        }

                        Log.d(TAG, "onComplete: " + task.getException());
                    }

                });
    }


}
