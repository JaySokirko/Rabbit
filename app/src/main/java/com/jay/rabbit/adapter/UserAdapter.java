package com.jay.rabbit.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jay.rabbit.R;
import com.jay.rabbit.model.Chat;
import com.jay.rabbit.model.User;
import com.jay.rabbit.ui.activity.MessageActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> users;
    private boolean isOnline;

    private String theLastMessage;

    public UserAdapter(Context context, ArrayList<User> users, boolean isOnline) {
        this.context = context;
        this.users = users;
        this.isOnline = isOnline;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_users, parent, false);

        return new UserAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = users.get(position);

        holder.userName.setText(user.getUserName());

        if (user.getUserImage().equals("default")) {
            holder.profilePhoto.setImageResource(R.drawable.logo_rabbit);
        } else {
            Picasso.get().load(user.getUserImage()).into(holder.profilePhoto);
        }


        if (isOnline){
            lastMessage(user.getId(), holder.lastMessage);
        }

        if (isOnline) {

            if (user.getStatus().equals("online")) {

                holder.circleOnline.setVisibility(View.VISIBLE);
                holder.circleOffline.setVisibility(View.GONE);
            } else {
                holder.circleOnline.setVisibility(View.GONE);
                holder.circleOffline.setVisibility(View.VISIBLE);
            }
        } else {
            holder.circleOnline.setVisibility(View.GONE);
            holder.circleOffline.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(v ->
                context.startActivity(new Intent(context, MessageActivity.class)
                        .putExtra("userId", user.getId())));
    }


    @Override
    public int getItemCount() {
        return users.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private ImageView profilePhoto;
        private ImageView circleOnline;
        private ImageView circleOffline;
        private TextView lastMessage;

        ViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.profile_name);
            profilePhoto = itemView.findViewById(R.id.profile_image);
            circleOnline = itemView.findViewById(R.id.status_circle_online);
            circleOffline = itemView.findViewById(R.id.status_circle_offline);
            lastMessage = itemView.findViewById(R.id.last_message);
        }
    }



    private void lastMessage(String userId, TextView lastMsg) {

        theLastMessage = "default";

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);

                    if (firebaseUser != null && chat != null
                            && (chat.getReceiver().equals(firebaseUser.getUid())
                            && chat.getSender().equals(userId)
                            || chat.getReceiver().equals(userId)
                            && chat.getSender().equals(firebaseUser.getUid()))) {

                        theLastMessage = chat.getMessage();
                    }
                }
                switch (theLastMessage) {

                    case "default":
                        lastMsg.setText(context.getResources().getString(R.string.no_messages));
                        break;

                    default:
                        lastMsg.setText(theLastMessage);
                        break;
                }
                theLastMessage = "default";
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
