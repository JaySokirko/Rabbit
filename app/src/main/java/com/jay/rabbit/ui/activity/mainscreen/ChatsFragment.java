package com.jay.rabbit.ui.activity.mainscreen;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jay.rabbit.R;
import com.jay.rabbit.adapter.UserAdapter;
import com.jay.rabbit.model.Chat;
import com.jay.rabbit.model.ChatList;
import com.jay.rabbit.model.User;
import com.jay.rabbit.notifications.Token;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {


    private RecyclerView recyclerView;
    private ArrayList<User> users;
    private ArrayList<ChatList> usersList;
    private UserAdapter userAdapter;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String TAG = "LOG_TAG";

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersList = new ArrayList<>();
        users = new ArrayList<>();

        addUserToChatList();

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }



    private void addUserToChatList() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usersList.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    ChatList chatList = snapshot.getValue(ChatList.class);
                    usersList.add(chatList);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }



    private void chatList() {

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                users.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);

                    for (ChatList chatList : usersList){

                        if (user != null && user.getId().equals(chatList.getId())) {
                            users.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), users, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void updateToken(String token){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        databaseReference.child(firebaseUser.getUid()).setValue(new Token(token));
    }

}
