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

import com.jay.rabbit.R;
import com.jay.rabbit.model.User;
import com.jay.rabbit.ui.activity.MessageActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> users;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_users, parent,false);

        return new UserAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = users.get(position);

        holder.userName.setText(user.getUserName());

        if (user.getUserImage().equals("default")){
            holder.profilePhoto.setImageResource(R.drawable.logo_rabbit);
        }else {
            Picasso.get().load(user.getUserImage()).into(holder.profilePhoto);
        }

        holder.itemView.setOnClickListener(v ->
                context.startActivity(new Intent(context, MessageActivity.class)
                .putExtra("userId", user.getId())));
    }


    @Override
    public int getItemCount() {
        return users.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView userName;
        private ImageView profilePhoto;

        ViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.profile_name);
            profilePhoto = itemView.findViewById(R.id.profile_image);
        }
    }
}
