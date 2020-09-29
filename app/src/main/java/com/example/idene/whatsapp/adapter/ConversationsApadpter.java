package com.example.idene.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.model.Conversation;
import com.example.idene.whatsapp.model.Group;
import com.example.idene.whatsapp.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//COMPLETED

public class ConversationsApadpter extends RecyclerView.Adapter<ConversationsApadpter.MyViewHolder> {

    private List<Conversation> conversations;
    private Context context;

    public ConversationsApadpter(List<Conversation> list, Context c) {
        this.conversations = list;
        this.context = c;
    }

    public List<Conversation> getConversations(){
        return this.conversations;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemList = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_contacts,viewGroup,false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Conversation conversation = conversations.get(i);//i = position

        myViewHolder.lastMessage.setText(conversation.getLastMessage());

        if (conversation.getIsGroup().equals("true")){//check if it's a group

            Group group = conversation.getGroup();
            myViewHolder.name.setText(group.getName());

            if (group.getPhotograph() != null){ //recover user photo
                Uri uri = Uri.parse( group.getPhotograph());
                Glide.with(context).load(uri).into(myViewHolder.photo); //if you hear a photo use the glide library if you don't use the default image
            }else{
                myViewHolder.photo.setImageResource(R.drawable.standard);//if there is no photo, the default photo is configured
            }

        }else{

            User user = conversation.getUserExhibition();//recover user
            if (user != null){
                myViewHolder.name.setText(user.getName());//recover user name

                if (user.getPhotograph() != null){ //recover user photo
                    Uri uri = Uri.parse( user.getPhotograph());
                    Glide.with(context).load(uri).into(myViewHolder.photo); //if you hear a photo use the glide library if you don't use the default image
                }else{
                    myViewHolder.photo.setImageResource(R.drawable.standard);//if there is no photo, the default photo is configured
                }
            }

        }


    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView photo;
        TextView name, lastMessage;

        public MyViewHolder (View itemView){
            super(itemView);

            photo = itemView.findViewById(R.id.imageViewPhotoContact);
            name = itemView.findViewById(R.id.textNameContact);
            lastMessage = itemView.findViewById(R.id.textEmailContact);
        }
    }
}
