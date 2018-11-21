package com.jay.rabbit.ui.activity.signinscreen;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.jay.rabbit.R;
import com.jay.rabbit.firebase.GoogleSignIn;
import com.jay.rabbit.ui.animation.Animation;
import com.jay.rabbit.ui.activity.mainscreen.MainScreenActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = "LOG_TAG";

    public SignInFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;
    private GoogleSignIn googleAuthorization;
    private GoogleApiClient googleApiClient;

    private RelativeLayout parentLayout;

    private Context context;

    private AppCompatActivity activity;

    private static final int GOOGLE_SING_IN = 1;

    private EditText emailEditText;
    private EditText passwordEditText;
    private LinearLayout registrationLayout;
    private Button signIpBtn;
    private ImageView logoImView;
    private LinearLayout signInLayout;
    private Button forgotPasswordBtn;
    private Button createAccountBtn;
    private TextView emailErrorTextView;
    private TextView passwordErrorTextView;
    private ImageView googleLoginBtn;
    private RelativeLayout parentView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        parentLayout = view.findViewById(R.id.parent_layout);

        initializeViews(view);

        activity = (AppCompatActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();

        googleAuthorization = new GoogleSignIn(activity, mAuth);
        authStateListener = googleAuthorization.getAuthStateListener();
        googleApiClient = googleAuthorization.getGoogleApiClient();

        animationOnFragmentStart();

        onEmailEditTextClickListener();
        onPasswordEditTextClickListener();

        signIpBtn.setOnClickListener(this);
        googleLoginBtn.setOnClickListener(this);

//        new Handler().postDelayed(() -> {
//
//            FragmentManager fragmentManager = activity.getSupportFragmentManager();
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            NewAccountFragment accountFragment = new NewAccountFragment();
//
//            transaction.add(R.id.fragment_container, accountFragment)
//            .setCustomAnimations(R.anim.appear, R.anim.disappear, R.anim.disappear, R.anim.appear);
//            transaction.commit();
//        },3000);

        Animation.initBackground(context);

        return view;
    }



    private void initializeViews(View view) {

        emailEditText = view.findViewById(R.id.edit_text_login);
        passwordEditText = view.findViewById(R.id.edit_text_password);
        registrationLayout = view.findViewById(R.id.registration_form_layout);
        signIpBtn = view.findViewById(R.id.button_sing_in);
        logoImView = view.findViewById(R.id.image_view_logo);
        signInLayout = view.findViewById(R.id.layout_sign_in);
        forgotPasswordBtn = view.findViewById(R.id.button_forgot_password);
        createAccountBtn = view.findViewById(R.id.button_new_account);
        googleLoginBtn = view.findViewById(R.id.google_login);
        parentView = view.findViewById(R.id.parent_view);
        emailErrorTextView = view.findViewById(R.id.text_view_email_error);
        emailErrorTextView.setVisibility(View.INVISIBLE);

        passwordErrorTextView = view.findViewById(R.id.text_view_password_error);
        passwordErrorTextView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }


    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(authStateListener);
    }


    @Override
    public void onPause() {
        super.onPause();

        googleApiClient.stopAutoManage(activity);
        googleApiClient.disconnect();
    }

    /**
     * Start animation on activity created
     */
    public void animationOnFragmentStart() {

        final float x = getResources().getDimension(R.dimen.dim_100);
        final float y = getResources().getDimension(R.dimen.dim_130);

        new Handler().postDelayed(() ->
                        logoImView.animate().setDuration(500).translationX(-x).translationY(-y).start(),
                1000);

        new Handler().postDelayed(() -> {

            registrationLayout.animate().setDuration(1000).alpha(1).start();
            signIpBtn.animate().setDuration(1000).alpha(1).start();
            signInLayout.animate().setDuration(1000).alpha(1).start();
            forgotPasswordBtn.animate().setDuration(1000).alpha(1).start();
            createAccountBtn.animate().setDuration(1000).alpha(1).start();
        }, 1500);
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



    /**
     * If the user already has an account, then he is authorized
     */
    public void onSignIn() {

        Animation.startProgressAnimation(logoImView);
        Animation.onButtonPressed(signIpBtn);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty()) {
            emailErrorTextView.setText(getResources().getString(R.string.enter_an_email));
            emailErrorTextView.setVisibility(View.VISIBLE);
            emailEditText.setBackground(getResources().getDrawable(R.drawable.shape_botton_left_corner_0_error));
            Animation.stopProgressAnimation(logoImView);

        } else if (password.length() < 6) {
            passwordErrorTextView.setText(getResources().getString(R.string.enter_your_password));
            passwordErrorTextView.setVisibility(View.VISIBLE);
            passwordEditText.setBackground(getResources().getDrawable(R.drawable.shape_botton_left_corner_0_error));
            Animation.stopProgressAnimation(logoImView);

        } else {
            signIn(email, password);
        }
    }


    public void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {

                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        startActivity(new Intent(context, MainScreenActivity.class));

                    } else {
                        // If sign in fails, display a message to the user.
                        if (task.getException() != null) {
                            Snackbar.make(parentView, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                    Animation.stopProgressAnimation(logoImView);
                });
    }


    /**
     * Sign in with google.
     */
    @SuppressLint("ClickableViewAccessibility")
    public void onGoogleSignIn() {

        Animation.onImagePressed(googleLoginBtn);
        Animation.startProgressAnimation(logoImView);
        try {
            googleSignIn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
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
                Toast.makeText(context, "Google Sign In failed", Toast.LENGTH_SHORT).show();
            }
            Animation.stopProgressAnimation(logoImView);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //sign in button
            case R.id.button_sing_in:
                onSignIn();
                break;

            //google sign in button
            case R.id.google_login:
                onGoogleSignIn();
                break;
        }
    }
}
