package com.jay.rabbit.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    private Drawable accentBackground;
    private Drawable normalBackground;

    private EditText emailEditText;
    private EditText passwordEditText;
    private LinearLayout registrationLayout;
    private Button singUpBtn;
    private ImageView logoImView;
    private LinearLayout signInLayout;
    private Button forgotPasswordBan;
    private Button createAccountBtn;
    private TextView emailErrorTextView;
    private TextView passwordErrorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeViews();

        RelativeLayout parentLayout = findViewById(R.id.parent_view);

        mAuth = FirebaseAuth.getInstance();

        backgroundAnimation = (AnimationDrawable) parentLayout.getBackground();
        backgroundAnimation.setExitFadeDuration(4000);

        accentBackground = getResources().getDrawable(R.drawable.shape_rounded_rectangle_accent);
        normalBackground = getResources().getDrawable(R.drawable.shape_rounded_rectangle_white);

        somemethod();

        animationOnActivityStart();

        onEmailEditTextClickListener();

        onPasswordEditTextClickListener();

        emailEditText.setText("a@gmail.com");
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
     * Initialization all views
     */
    private void initializeViews() {

        emailEditText = findViewById(R.id.edit_text_login);
        passwordEditText = findViewById(R.id.edit_text_password);
        registrationLayout = findViewById(R.id.registration_form_layout);
        singUpBtn = findViewById(R.id.button_sing_up);
        logoImView = findViewById(R.id.image_view_logo);
        signInLayout = findViewById(R.id.layout_sign_in);
        forgotPasswordBan = findViewById(R.id.button_forgot_password);
        createAccountBtn = findViewById(R.id.button_new_account);

        emailErrorTextView = findViewById(R.id.text_view_email_error);
        emailErrorTextView.setVisibility(View.INVISIBLE);

        passwordErrorTextView = findViewById(R.id.text_view_password_error);
        passwordErrorTextView.setVisibility(View.INVISIBLE);
    }


    /**
     * Start animation on activity created
     */
    public void animationOnActivityStart() {

        final float x = getResources().getDimension(R.dimen.dim_100);
        final float y = getResources().getDimension(R.dimen.dim_130);

        new Handler().postDelayed(() ->
                        logoImView.animate().setDuration(500).translationX(-x).translationY(-y).start(),
                1000);

        new Handler().postDelayed(() -> {

            registrationLayout.animate().setDuration(1000).alpha(1).start();
            singUpBtn.animate().setDuration(1000).alpha(1).start();
            signInLayout.animate().setDuration(1000).alpha(1).start();
            forgotPasswordBan.animate().setDuration(1000).alpha(1).start();
            createAccountBtn.animate().setDuration(1000).alpha(1).start();
        }, 1500);
    }


    /**
     * If the user already has an account, then he is authorized
     *
     * @param view sign in button
     */

    public void onSignInClick(View view) {

        singUpBtn.setBackground(accentBackground);

    }


    /**
     * If the user does has not an account, then he is registered
     *
     * @param view create new user button
     */
    public void onCreateNewUserClick(View view) {

        createAccountBtn.setBackground(accentBackground);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty()) {
            emailErrorTextView.setText(getResources().getString(R.string.enter_an_email));
            emailErrorTextView.setVisibility(View.VISIBLE);
            emailEditText.setBackground(getResources().getDrawable(R.drawable.shape_botton_left_corner_0_error));

        } else if (password.length() < 6) {
           passwordErrorTextView.setText(getResources().getString(R.string.enter_your_password));
           passwordErrorTextView.setVisibility(View.VISIBLE);
           passwordEditText.setBackground(getResources().getDrawable(R.drawable.shape_botton_left_corner_0_error));

        } else {
            registration(email, password);
        }

        new Handler().postDelayed(() -> createAccountBtn.setBackground(normalBackground), 500);
    }


    /**
     * Email edit text click listener.
     * If the user starts typing, the error will disappear.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void onEmailEditTextClickListener() {

        emailEditText.setOnTouchListener((v, event) -> {
            emailEditText.setBackground(getResources().getDrawable(R.drawable.shape_bottom_lef_corner_0));
            emailErrorTextView.setVisibility(View.INVISIBLE);
            return false;
        });

    }


    /**
     * Password edit text click listener.
     * If the user starts typing, the error will disappear.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void onPasswordEditTextClickListener() {

        passwordEditText.setOnTouchListener((v, event) -> {

            passwordEditText.setBackground(getResources().getDrawable(R.drawable.shape_bottom_lef_corner_0));
            passwordErrorTextView.setVisibility(View.INVISIBLE);
            return false;
        });
    }

    public void somemethod() {
        authStateListener = firebaseAuth -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {


            } else {

            }
        };
    }


    public void authentication(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(SignInActivity.this, "successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignInActivity.this, AuthorizedUserActivity.class));

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignInActivity.this, "unsuccessfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void registration(String email, String password) {

        startProgressAnimation();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        startActivity(new Intent(SignInActivity.this, RegistratedUserActivity.class));
                    } else {
                        // If sign in fails, display a message to the user.

                        Toast.makeText(SignInActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }

                    Log.d(TAG, "onComplete: " + task.getException());
                    stopProgressAnimation();
                });
    }


    private void startProgressAnimation() {

        logoImView.setImageResource(R.drawable.logo_animated_progress);
        Drawable drawable = logoImView.getDrawable();
        ((Animatable) drawable).start();

    }


    private void stopProgressAnimation() {

        logoImView.setImageResource(R.drawable.logo_rabbit);

    }

}
