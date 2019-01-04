package com.jay.rabbit.ui.activity.signinscreen;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jay.rabbit.R;
import com.jay.rabbit.ui.activity.mainscreen.MainScreenActivity;
import com.jay.rabbit.animation.Animation;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewAccountFragment extends Fragment {


    private Bitmap bitmap;
    private String TAG = "TAG_LOG";

    public NewAccountFragment() {
        // Required empty public constructor
    }


    private CircleImageView profileImage;
    private ImageView logoImage;
    private EditText emailEditText;
    private EditText nameEditText;
    private EditText passwordEditText;
    private Button acceptBtn;
    private Button cancelBtn;
    private RelativeLayout parentLayout;
    private TextView emailErrorTextView;
    private TextView passwordErrorTextView;
    private TextView nickNameErrorTextView;

    private FirebaseAuth mAuth;

    private AppCompatActivity activity;

    private Context context;

    private static int PICK_IMAGE_REQUEST = 1;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private DatabaseReference dbReference;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_account, container, false);

        profileImage = view.findViewById(R.id.avatar_image);
        emailEditText = view.findViewById(R.id.edit_email);
        nameEditText = view.findViewById(R.id.edit_nick_name);
        passwordEditText = view.findViewById(R.id.edit_password);
        acceptBtn = view.findViewById(R.id.accept_button);
        cancelBtn = view.findViewById(R.id.cancel_button);
        parentLayout = view.findViewById(R.id.parent_layout);
        emailErrorTextView = view.findViewById(R.id.text_view_email_error);
        passwordErrorTextView = view.findViewById(R.id.text_view_password_error);
        nickNameErrorTextView = view.findViewById(R.id.text_view_nick_name_error);

        activity = (AppCompatActivity) getActivity();

        Animation.initDrawableRes(context);

        mAuth = FirebaseAuth.getInstance();

        acceptBtn.setOnClickListener(v -> createNewUser());

        cancelBtn.setOnClickListener(v -> cancel());

        onEmailEditListener();

        onNickNameEditListener();

        onPasswordEditListener();

        onProfileImageClick();

        emailEditText.setText("sokirko0601@gmail.com");
        nameEditText.setText("Jay");
        passwordEditText.setText("123456");

        preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);

        return view;
    }


    private void cancel() {

        Animation.onButtonPressed(cancelBtn);

        FragmentManager manager = getFragmentManager();
        if (manager != null) {
            FragmentTransaction transaction = manager.beginTransaction();
            SignInFragment fragment = new SignInFragment();
            transaction.setCustomAnimations(R.anim.appear, R.anim.disappear);
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        }
    }

    /**
     * If the user does has not an account, then he is registered
     */
    private void createNewUser() {

        Animation.onButtonPressed(acceptBtn);

        String email = emailEditText.getText().toString();
        String nickName = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        //Display error if the email field is empty.
        if (email.isEmpty()) {

            emailErrorTextView.setText(getResources().getString(R.string.enter_an_email));
            emailErrorTextView.setVisibility(View.VISIBLE);
            emailEditText.setBackground(getResources().getDrawable(R.drawable.shape_botton_left_corner_0_error));

        } else if (nickName.isEmpty()) {

            nickNameErrorTextView.setText(getString(R.string.enter_your_nick_name));
            nickNameErrorTextView.setVisibility(View.VISIBLE);
            nameEditText.setBackground(getResources().getDrawable(R.drawable.shape_botton_left_corner_0_error));

        } else if (password.length() < 6) {

            passwordErrorTextView.setText(getResources().getString(R.string.enter_your_password));
            passwordErrorTextView.setVisibility(View.VISIBLE);
            passwordEditText.setBackground(getResources().getDrawable(R.drawable.shape_botton_left_corner_0_error));

        } else {
            createNewAccount(email, password);
        }
    }

    /**
     * Create a new user
     *
     * @param email    email of a new user
     * @param password password of a new user
     */
    public void createNewAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        String userID = null;
                        if (user != null) {
                            userID = user.getUid();
                            dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                        }

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userID);
                        hashMap.put("userName", nameEditText.getText().toString());
                        hashMap.put("userImage", "default");
                        hashMap.put("status", "offline");
                        hashMap.put("search", nameEditText.getText().toString().toLowerCase());

                        dbReference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {

                                Intent intent = new Intent(context, MainScreenActivity.class);

                                if (isAdded()) {
                                    startActivity(intent);
                                }
                            }
                        });

                    } else {
                        // If sign in fails, display a message to the user.
                        if (task.getException() != null) {
                            Snackbar.make(parentLayout, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void onProfileImageClick() {

        profileImage.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_profile_foto)),
                    PICK_IMAGE_REQUEST);
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        bitmap = MediaStore
                                .Images
                                .Media
                                .getBitmap(Objects.requireNonNull(getActivity())
                                        .getContentResolver(), data.getData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (bitmap != null) {
            profileImage.setImageBitmap(bitmap);

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bs);

            String encoded = Base64.encodeToString(bs.toByteArray(), Base64.DEFAULT);

            editor = preferences.edit();
            editor.putString("user_image", encoded);
            editor.apply();
        }
    }

    /**
     * Hide the error when the user starts typing the email address
     */
    @SuppressLint("ClickableViewAccessibility")
    private void onEmailEditListener() {

        emailEditText.setOnTouchListener((v, event) -> {
            emailEditText.setBackground(getResources().getDrawable(R.drawable.shape_bottom_lef_corner_0));
            emailErrorTextView.setVisibility(View.INVISIBLE);
            return false;
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void onNickNameEditListener() {

        nickNameErrorTextView.setOnTouchListener((v, event) -> {
            nameEditText.setBackground(getResources().getDrawable(R.drawable.shape_bottom_lef_corner_0));
            nickNameErrorTextView.setVisibility(View.INVISIBLE);
            return false;
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void onPasswordEditListener() {

        passwordEditText.setOnTouchListener((v, event) -> {
            passwordEditText.setBackground(getResources().getDrawable(R.drawable.shape_bottom_lef_corner_0));
            passwordErrorTextView.setVisibility(View.INVISIBLE);
            return false;
        });
    }
}
