package com.example.idene.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.helper.UserFirebase;
import com.example.idene.whatsapp.model.Message;

import java.util.List;

//COMPLETED

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<Message> messages;
    private Context context;
    private static final int TYPE_SENDER = 0;
    private static final int TYPE_RECIPIENT = 1;

    public MessagesAdapter(List<Message> list, Context c) {
        this.messages = list;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = null;
        if(i == TYPE_SENDER){

            item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_message_sender,viewGroup,false);

        }else if (i == TYPE_RECIPIENT){
            item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_message_recipient,viewGroup,false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Message message = messages.get(i);//retrieves message for each list item
        String msg = message.getMessage();
        String image = message.getImage();

        if (image != null){
            Uri uri = Uri.parse(image);
            Glide.with(context).load(uri).into(myViewHolder.image);

            String name = message.getName();
            if ( !name.isEmpty()){//if the name is not empty
                myViewHolder.name.setText(name);
            }else {
                myViewHolder.name.setVisibility(View.GONE);
            }

            //Hide text
            myViewHolder.message.setVisibility(View.GONE);

        }else{
            myViewHolder.message.setText(msg);

            String name = message.getName();
            if ( !name.isEmpty()){//if the name is not empty
                myViewHolder.name.setText(name);
            }else {
                myViewHolder.name.setVisibility(View.GONE);
            }

            //Hide image
            myViewHolder.image.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messages.get(position);//retrieves message for each list item

        String idUser = UserFirebase.getIdentifierUser();

        if (idUser.equals(message.getIdUser())){
            return TYPE_SENDER;
        }
        return TYPE_RECIPIENT;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView message;
        TextView name;
        ImageView image;

        public MyViewHolder (View itemView){
            super(itemView);

            message = itemView.findViewById(R.id.textMessageText);
            image = itemView.findViewById(R.id.imageMessagePhotograph);
            name = itemView.findViewById(R.id.textNameDisplay);

        }
    }
}
