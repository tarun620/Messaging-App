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
import com.example.idene.whatsapp.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//COMPLETED

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyVieHolder> {

    private List<User> contacts;
    private Context context;

    public ContactAdapter(List<User> listContacts, Context c) {
        this.contacts = listContacts;
        this.context = c;
    }

    public List<User> getContacts(){
        return this.contacts;
    }

    @NonNull
    @Override
    public MyVieHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_contacts,viewGroup,false);

        return new MyVieHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVieHolder myVieHolder, int i) {

        User user = contacts.get(i);
        boolean header = user.getEmail().isEmpty();//when empty and header

        myVieHolder.name.setText(user.getName());
        myVieHolder.email.setText(user.getEmail());
        //set up the photo
        if ( user.getPhotograph() != null){
            Uri uri = Uri.parse( user.getPhotograph());
            Glide.with(context).load(uri).into(myVieHolder.photo); //if you hear a photo use the glide library if you don't use the default image
        }else{
            if (header){
                myVieHolder.photo.setImageResource(R.drawable.icone_grupo);
                myVieHolder.email.setVisibility(View.GONE);
            }else {
                myVieHolder.photo.setImageResource(R.drawable.standard);
            }
        }


    }

    @Override
    public int getItemCount() {
        return contacts.size();//returns the list size
    }

    public class MyVieHolder extends RecyclerView.ViewHolder{

        CircleImageView photo;
        TextView name,email;

        public MyVieHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.imageViewPhotoContact);
            name = itemView.findViewById(R.id.textNameContact);
            email = itemView.findViewById(R.id.textEmailContact);


        }
    }

}
