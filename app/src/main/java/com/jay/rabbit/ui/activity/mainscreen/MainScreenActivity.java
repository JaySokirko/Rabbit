package com.jay.rabbit.ui.activity.mainscreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.jay.rabbit.R;
import com.jay.rabbit.ui.activity.signinscreen.AuthorizationActivity;

public class MainScreenActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private SharedPreferences preferences;
    private TextView userNameTextView;
    private ImageView userPhotoImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);

        preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String userName = preferences.getString("user name", "user name");


        Uri uri = Uri.parse(preferences.getString("user photo", "def"));

        auth = FirebaseAuth.getInstance();

    }

    public void signOut(View view){

        auth.signOut();
        startActivity(new Intent(this, AuthorizationActivity.class));
    }
}
