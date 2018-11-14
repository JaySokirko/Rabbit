package com.jay.rabbit.firebase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.jay.rabbit.R;
import com.jay.rabbit.activity.AuthorizedUserActivity;

public class GoogleSignIn {

    private AppCompatActivity activity;
    private FirebaseAuth mAuth;

    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public GoogleSignIn(AppCompatActivity activity, FirebaseAuth mAuth) {
        this.activity = activity;
        this.mAuth = mAuth;

        SharedPreferences preferences = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }



    public FirebaseAuth.AuthStateListener getAuthStateListener(){
        return fireBaseAuth -> {

            if (fireBaseAuth.getCurrentUser() != null) {

                activity.finish();
                activity.startActivity(new Intent(activity, AuthorizedUserActivity.class));
            }
        };
    }


    public GoogleApiClient getGoogleApiClient() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        return new GoogleApiClient.Builder(activity.getApplicationContext())
                .enableAutoManage(activity, connectionResult -> {

                    Toast.makeText(activity, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    public void fireBaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {

                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            editor.putString("user name", user.getDisplayName());
                            editor.putString("user photo", user.getPhotoUrl().toString());

                        }else {
                            editor.putString("user name", "user name");
                        }
                        editor.apply();

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(activity, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
