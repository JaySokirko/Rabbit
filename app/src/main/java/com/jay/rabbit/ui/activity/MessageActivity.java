package com.jay.rabbit.ui.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jay.rabbit.R;
import com.jay.rabbit.adapter.MessageAdapter;
import com.jay.rabbit.model.Chat;
import com.jay.rabbit.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {


    private CircleImageView profileImageIV;
    private TextView userNameTV;
    private ImageButton sendBtn;
    private EditText enterMessageET;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private String userId;

    private MessageAdapter messageAdapter;
    private ArrayList<Chat> chatList;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        setUpToolBar();

        profileImageIV = findViewById(R.id.profile_image);
        userNameTV = findViewById(R.id.user_name);
        sendBtn = findViewById(R.id.send_button);
        enterMessageET = findViewById(R.id.text_to_send);

        userId = getIntent().getStringExtra("userId");

        setUpSelectedUser();

        onSendMessageClick();

        setUpRecyclerView();

    }


    private void setUpRecyclerView() {

        recyclerView = findViewById(R.id.recycler_messages);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

    }


    private void onSendMessageClick() {

        sendBtn.setOnClickListener(v -> {

            String message = enterMessageET.getText().toString();

            if (message.equals("")) {
                message = " ";
            }
            sendMessage(firebaseUser.getUid(), userId, message);
        });
    }


    private void sendMessage(String sender, String receiver, String message) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        databaseReference.child("Chats").push().setValue(hashMap);
    }


    private void setUpSelectedUser() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    userNameTV.setText(user.getUserName());

                    if (user.getUserImage().equals("default")) {
                        profileImageIV.setImageResource(R.drawable.logo_rabbit);

                    } else {
                        Picasso.get().load(user.getUserImage()).into(profileImageIV);
                    }
                }

                if (user != null) {
                    readMessage(firebaseUser.getUid(), userId, user.getUserImage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void setUpToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }


    private void readMessage(String myId, String userId, String imageUrl) {

        chatList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat != null && chat.getReceiver().equals(myId)
                            && chat.getSender().equals(userId) || chat.getReceiver().equals(userId)
                            && chat.getSender().equals(myId)) {

                        chatList.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, chatList, imageUrl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
