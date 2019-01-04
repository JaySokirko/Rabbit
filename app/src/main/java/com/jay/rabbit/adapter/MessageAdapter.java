package com.jay.rabbit.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jay.rabbit.R;
import com.jay.rabbit.model.Chat;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    public static final int MESSAGE_TYPE_LEFT = 0;
    public static final int MESSAGE_TYPE_RIGHT = 1;

    private Context context;
    private ArrayList<Chat> chat;
    private String imageUrl;

    private FirebaseUser firebaseUser;

    public MessageAdapter(Context context, ArrayList<Chat> chats, String imageUrl) {
        this.context = context;
        this.chat = chats;
        this.imageUrl = imageUrl;
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MESSAGE_TYPE_RIGHT) {

            View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_chat_item_right,
                    parent, false);
            return new MessageAdapter.ViewHolder(view);

        } else {

            View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_chat_item_left,
                    parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat ch = chat.get(position);

        holder.showMessage.setText(ch.getMessage());

        if (imageUrl.equals("default")){
            holder.profilePhoto.setImageResource(R.drawable.logo_rabbit);

        } else {
            Picasso.get().load(imageUrl).into(holder.profilePhoto);
        }

        if (position == chat.size() - 1){

            if (ch.isIsseen()){
                holder.seenMessage.setText(context.getResources().getString(R.string.seen));

            } else {
                holder.seenMessage.setText(context.getResources().getString(R.string.delivered));
            }
        } else {
            holder.seenMessage.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return chat.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView showMessage;
        private ImageView profilePhoto;
        private TextView seenMessage;

        ViewHolder(View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.show_message);
            profilePhoto = itemView.findViewById(R.id.profile_image);
            seenMessage = itemView.findViewById(R.id.message_seen);
        }
    }


    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chat.get(position).getSender().equals(firebaseUser.getUid())){
            return MESSAGE_TYPE_RIGHT;

        }else {
            return MESSAGE_TYPE_LEFT;
        }
    }
}
