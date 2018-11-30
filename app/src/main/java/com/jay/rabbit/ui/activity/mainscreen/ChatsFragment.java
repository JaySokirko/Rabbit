package com.jay.rabbit.ui.activity.mainscreen;


import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.jay.rabbit.R;
import com.jay.rabbit.adapter.UserAdapter;
import com.jay.rabbit.model.Chat;
import com.jay.rabbit.model.User;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {


    private RecyclerView recyclerView;
    private ArrayList<User> users;
    private ArrayList<String> usersList;
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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);


                    if (chat != null && chat.getSender().equals(firebaseUser.getUid())) {
                        usersList.add(chat.getReceiver());
                    }

                    if (chat != null && chat.getReceiver().equals(firebaseUser.getUid())) {
                        usersList.add(chat.getSender());
                    }
                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return view;
    }

    private void readChats() {

        users = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                users.clear();

                //display one user from chats
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);

                    for (String id: usersList){

                        if (user != null && user.getId().equals(id)) {

                            if (users.size() != 0) {

                                for (User u: users){

                                    if (!user.getId().equals(u.getId())){
                                        users.add(u);
                                    }
                                }
                            } else {
                                users.add(user);
                            }
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), users);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
