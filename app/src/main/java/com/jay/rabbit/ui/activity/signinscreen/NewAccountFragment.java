package com.jay.rabbit.ui.activity.signinscreen;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.jay.rabbit.R;
import com.jay.rabbit.ui.animation.Animation;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewAccountFragment extends Fragment {


    public NewAccountFragment() {
        // Required empty public constructor
    }


    private ImageView avatarImage;
    private ImageView logoImage;
    private EditText emailEditText;
    private EditText nameEditText;
    private EditText passwordEditText;
    private Button acceptBtn;
    private RelativeLayout parentLayout;

    private FirebaseAuth mAuth;

    private AppCompatActivity activity;

    private Context context;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_account, container, false);

        avatarImage = view.findViewById(R.id.avatar_image);
        emailEditText = view.findViewById(R.id.edit_email);
        nameEditText = view.findViewById(R.id.edit_nick_name);
        passwordEditText = view.findViewById(R.id.edit_password);
        acceptBtn = view.findViewById(R.id.accept_button);
        logoImage = view.findViewById(R.id.logo);
        parentLayout = view.findViewById(R.id.parent_layout);

        Animation.initBackground(context);

        mAuth = FirebaseAuth.getInstance();

        activity = (AppCompatActivity) getActivity();

        return view;
    }



    /**
     * If the user does has not an account, then he is registered
     *
     * @param view create new user button
     */
    public void onCreateNewUserClick(View view) {

        Animation.onButtonPressed(acceptBtn);

        String email = emailEditText.getText().toString();
        String nickName = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty()) {
//            emailErrorTextView.setText(getResources().getString(R.string.enter_an_email));
//            emailErrorTextView.setVisibility(View.VISIBLE);
            emailEditText.setBackground(getResources().getDrawable(R.drawable.shape_botton_left_corner_0_error));

        } else if (password.length() < 6) {
//            passwordErrorTextView.setText(getResources().getString(R.string.enter_your_password));
//            passwordErrorTextView.setVisibility(View.VISIBLE);
            passwordEditText.setBackground(getResources().getDrawable(R.drawable.shape_botton_left_corner_0_error));

        } else if (nickName.isEmpty()){

            //todo error

        } else {
            createNewAccount(email, password);
        }
    }


    /**
     * Create new user
     *
     * @param email    email of a new user
     * @param password password of a new user
     */
    public void createNewAccount(String email, String password) {

        Animation.startProgressAnimation(logoImage);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        //todo
                    } else {
                        // If sign in fails, display a message to the user.
                        if (task.getException() != null) {
                            Snackbar.make(parentLayout, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                    Animation.stopProgressAnimation(logoImage);
                });
    }

}
