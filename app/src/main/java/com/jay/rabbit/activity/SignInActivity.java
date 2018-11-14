package com.jay.rabbit.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jay.rabbit.R;
import com.jay.rabbit.firebase.GoogleSignIn;


public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "LOG_TAG";
    private static final int GOOGLE_SING_IN = 1;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;

    private AnimationDrawable backgroundAnimation;
    private Drawable accentRectangleBackground;
    private Drawable normalRectangleBackground;
    private Drawable accentCircleBackground;
    private Drawable normalCircleBackground;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private CallbackManager callbackManager;

    private EditText emailEditText;
    private EditText passwordEditText;
    private LinearLayout registrationLayout;
    private Button singUpBtn;
    private ImageView logoImView;
    private LinearLayout signInLayout;
    private Button forgotPasswordBtn;
    private Button createAccountBtn;
    private TextView emailErrorTextView;
    private TextView passwordErrorTextView;
    private ImageView googleLoginBtn;
    private RelativeLayout parentLayout;
    private ImageView faceBookLoginBtn;

    private GoogleSignIn googleAuthorization;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeViews();

        RelativeLayout parentLayout = findViewById(R.id.parent_view);

        backgroundAnimation = (AnimationDrawable) parentLayout.getBackground();
        backgroundAnimation.setExitFadeDuration(4000);

        accentRectangleBackground = getResources().getDrawable(R.drawable.shape_rounded_rectangle_accent);
        normalRectangleBackground = getResources().getDrawable(R.drawable.shape_rounded_rectangle_white);
        accentCircleBackground = getResources().getDrawable(R.drawable.shape_circle_accent);
        normalCircleBackground = getResources().getDrawable(R.drawable.shape_circle_white);

        preferences = this.getSharedPreferences("Settings", MODE_PRIVATE);
        editor = preferences.edit();


        mAuth = FirebaseAuth.getInstance();
        googleAuthorization = new GoogleSignIn(this, mAuth);
        authStateListener = googleAuthorization.getAuthStateListener();

        animationOnActivityStart();

        onEmailEditTextClickListener();

        onPasswordEditTextClickListener();

        emailEditText.setText("sokirko0601@gmail.com");


    }

    public void faceBookAuthorization(){
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        editor.putString("user name", user.getDisplayName());
                        editor.putString("user photo", user.getPhotoUrl().toString());
                        startActivity(new Intent(SignInActivity.this, AuthorizedUserActivity.class));

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(authStateListener);
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

        stopProgressAnimation();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        forgotPasswordBtn = findViewById(R.id.button_forgot_password);
        createAccountBtn = findViewById(R.id.button_new_account);
        googleLoginBtn = findViewById(R.id.google_login);
        parentLayout = findViewById(R.id.parent_view);
        faceBookLoginBtn = findViewById(R.id.facebook_button);
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
            forgotPasswordBtn.animate().setDuration(1000).alpha(1).start();
            createAccountBtn.animate().setDuration(1000).alpha(1).start();
        }, 1500);
    }


    /**
     * If the user already has an account, then he is authorized
     *
     * @param view sign in button
     */
    public void onSignInClick(View view) {

        singUpBtn.setBackground(accentRectangleBackground);
    }

    /**
     * Sign in with google.
     *
     * @param view google sign in button
     */
    @SuppressLint("ClickableViewAccessibility")
    public void onGoogleSignIn(View view) {

        googleLoginBtn.setBackground(accentCircleBackground);
        startProgressAnimation();

        try {
            googleSignIn();
        }catch (Exception e){
            e.printStackTrace();
        }

        new Handler().postDelayed(() -> googleLoginBtn.setBackground(normalCircleBackground), 500);
    }
    

    /**
     * Sign in with facebook.
     *
     * @param view facebook sign in button
     */
    public void onFaceBookSignIn(View view) {
        faceBookAuthorization();
        Log.d(TAG, "onFaceBookSignIn: ");
    }

    /**
     * If the user does has not an account, then he is registered
     *
     * @param view create new user button
     */
    public void onCreateNewUserClick(View view) {

        createAccountBtn.setBackground(accentRectangleBackground);

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

        new Handler().postDelayed(() -> createAccountBtn.setBackground(normalRectangleBackground), 500);
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


    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleAuthorization.getGoogleApiClient());
        startActivityForResult(signInIntent, GOOGLE_SING_IN);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SING_IN) {
            Task<GoogleSignInAccount> task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with FireBase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                googleAuthorization.fireBaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google Sign In failed", Toast.LENGTH_SHORT).show();
            }
        }

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
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


    /**
     * Create new user
     *
     * @param email    email of new user
     * @param password password if new user
     */
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
