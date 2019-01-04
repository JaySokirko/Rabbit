package com.jay.rabbit.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jay.rabbit.R;
import com.jay.rabbit.adapter.MessageAdapter;
import com.jay.rabbit.model.Chat;
import com.jay.rabbit.model.User;
import com.jay.rabbit.notifications.ApiService;
import com.jay.rabbit.notifications.Client;
import com.jay.rabbit.notifications.Data;
import com.jay.rabbit.notifications.MyResponse;
import com.jay.rabbit.notifications.Sender;
import com.jay.rabbit.notifications.Token;
import com.jay.rabbit.ui.activity.mainscreen.MainScreenActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private ValueEventListener seenMessageListener;

    private ApiService apiService;

    private boolean notify = false;


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

        apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);

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

            notify = true;

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
        hashMap.put("isseen", false);

        databaseReference.child("Chats").push().setValue(hashMap);

        enterMessageET.setText("");

        //add user to chat fragment
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        final String msg = message;
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    if (notify) {
                        sendNotification(receiver, user.getUserName(), msg);
                    }
                    notify = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }



    private void sendNotification(String receiver, String userName, String message) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Token token = snapshot.getValue(Token.class);

                    Data data = new Data(firebaseUser.getUid(), R.drawable.logo_rabbit,
                            userName + ": " + message, "New message", userId);

                    if (token != null) {

                        Sender sender = new Sender(data, token.getToken());
                        apiService.sendNotification(sender)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(@NonNull Call<MyResponse> call,
                                                           @NonNull Response<MyResponse> response) {

                                        if (response.code() == 200){

                                            if (response.body() != null
                                                    && response.body().success != 1) {

                                                Toast.makeText(MessageActivity.this,
                                                        "Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<MyResponse> call,
                                                          @NonNull Throwable t) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


    private void seenMessage(String userid) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        seenMessageListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat != null) {

                        if (chat.getReceiver().equals(firebaseUser.getUid())
                                && chat.getSender().equals(userid)) {

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isseen", true);

                            snapshot.getRef().updateChildren(hashMap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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

         seenMessage(userId);
    }


    private void setUpToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v ->

                startActivity(new Intent(MessageActivity.this, MainScreenActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
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


    private void status(String status){

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        databaseReference.updateChildren(hashMap);
    }


    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }


    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(seenMessageListener);
        status("offline");
    }
}
