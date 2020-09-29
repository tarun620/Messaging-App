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

    public class GroupSelectedAdapter extends RecyclerView.Adapter<GroupSelectedAdapter.MyVieHolder> {

        //GrupoSelecionadoAdapter--GroupSelectedAdapter


    private List<User> contactsSelected;
    //contatosSelecionados-contactsSelected
    private Context context;

    public GroupSelectedAdapter(List<User> listOfContacts, Context c) {
        this.contactsSelected = listOfContacts;
        this.context = c;
    }

    @NonNull
    @Override
    public GroupSelectedAdapter.MyVieHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemList = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_group_selected,viewGroup,false);

        return new GroupSelectedAdapter.MyVieHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupSelectedAdapter.MyVieHolder myVieHolder, int i) {

        User user = contactsSelected.get(i);
        myVieHolder.name.setText(user.getName());

        //configurar a foto--set up a photo
        if ( user.getPhotograph() != null){
            Uri uri = Uri.parse( user.getPhotograph());
            Glide.with(context).load(uri).into(myVieHolder.photograph);
            //se ouver uma foto uso a biblioteca glide se nao usa a imagem padrao--if you hear a photo use the glide library if you don't use the default image
        }else{

            myVieHolder.photograph.setImageResource(R.drawable.standard);
        }

    }

    @Override
    public int getItemCount() {
        return contactsSelected.size();//retorna o tamanho da lista--return size of the list
    }

    public class MyVieHolder extends RecyclerView.ViewHolder{

        CircleImageView photograph;
        TextView name;

        public MyVieHolder(@NonNull View itemView) {
            super(itemView);

                photograph = itemView.findViewById(R.id.imageViewPhotoMemberSelected);
            name = itemView.findViewById(R.id.textMemberNameSelected);

        }
    }
}
