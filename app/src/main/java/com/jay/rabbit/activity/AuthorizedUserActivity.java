package com.jay.rabbit.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.jay.rabbit.R;
import com.squareup.picasso.Picasso;

public class AuthorizedUserActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private SharedPreferences preferences;
    private TextView userNameTextView;
    private ImageView userPhotoImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorized_user);

        preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String userName = preferences.getString("user name", "user name");

        userNameTextView = findViewById(R.id.textView2);
        userNameTextView.setText(userName);

        userPhotoImageView = findViewById(R.id.user_photo);
        Uri uri = Uri.parse(preferences.getString("user photo", "def"));
        Picasso.get().load(uri).into(userPhotoImageView);

        auth = FirebaseAuth.getInstance();
        findViewById(R.id.logout).setOnClickListener(v -> {

            auth.signOut();
            this.finish();
            startActivity(new Intent(this, SignInActivity.class));
        });
    }
}
