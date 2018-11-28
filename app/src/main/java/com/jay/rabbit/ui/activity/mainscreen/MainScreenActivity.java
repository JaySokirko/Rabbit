package com.jay.rabbit.ui.activity.mainscreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jay.rabbit.R;
import com.jay.rabbit.ui.activity.signinscreen.AuthorizationActivity;

import java.util.Iterator;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainScreenActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private SharedPreferences preferences;
    private TextView userNameTextView;
    private CircleImageView userPhotoImageView;
    private Bitmap bitmap;
    private String TAG = "LOG_TAG";
    private DatabaseReference databaseReference;

    private EditText enterMessageET;
    private Button sendMessageBtn;
    private TextView receiveMessageTV;
    private StringBuilder s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);

        preferences = getSharedPreferences("Settings", MODE_PRIVATE);

        String userName = preferences.getString("user_name", "User Name");

        userNameTextView = findViewById(R.id.nick_name);
        userNameTextView.setText(userName);

        userPhotoImageView = findViewById(R.id.profile_image);

        auth = FirebaseAuth.getInstance();

        setUserImage();


        enterMessageET = findViewById(R.id.test_edit);
        sendMessageBtn = findViewById(R.id.test_send);
        receiveMessageTV = findViewById(R.id.test_message);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.d(TAG, "onCreate: " + currentUser.getUid());

        sendMessageBtn.setOnClickListener(v -> databaseReference
                .child(currentUser.getUid())
                .child("Message")
                .push()
                .setValue(enterMessageET.getText().toString()));

        s = new StringBuilder();


      databaseReference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              Iterator iterator = dataSnapshot.getChildren().iterator();

              while (iterator.hasNext()){

                  s.append(((DataSnapshot)iterator.next()).getValue());
                  Log.d(TAG, "onDataChange: " + s);
              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
    }


    public void signOut(View view) {

        auth.signOut();
        startActivity(new Intent(this, AuthorizationActivity.class));
    }


    private void setUserImage() {

        String encoded = preferences.getString("user_image", "");

        byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
        userPhotoImageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

//        Uri uri = Uri.parse(preferences.getString("user photo", "def"));
//        Picasso.get().load(bitmap).into(userPhotoImageView);
    }
}
