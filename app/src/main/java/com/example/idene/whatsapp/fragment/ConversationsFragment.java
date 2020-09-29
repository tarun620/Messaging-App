package com.example.idene.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.activity.ChatActivity;
import com.example.idene.whatsapp.adapter.ConversationsApadpter;
import com.example.idene.whatsapp.config.ConfigurationFirebase;
import com.example.idene.whatsapp.model.Conversation;
import com.example.idene.whatsapp.helper.RecyclerItemClickListener;
import com.example.idene.whatsapp.helper.UserFirebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

//COMPLETED

public class ConversationsFragment extends Fragment {

    private RecyclerView recyclerViewConversation;
    private List<Conversation> listConversation = new ArrayList<>();
    private ConversationsApadpter adpter;
    private DatabaseReference database;
    private DatabaseReference conversationRef;
    private ChildEventListener childEventListenerConversation;



    public ConversationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*/ Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversas, container, false);*/
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        recyclerViewConversation = view.findViewById(R.id.recyclerListConversation);

        //configure adapter
        adpter = new ConversationsApadpter(listConversation,getActivity());



        //configure recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewConversation.setLayoutManager(layoutManager);
        recyclerViewConversation.setHasFixedSize(true);
        recyclerViewConversation.setAdapter(adpter);

        //set up click event
        recyclerViewConversation.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewConversation,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        List<Conversation> listConversationUpdated = adpter.getConversations();//retrieve selected conversation from adapter conversations that will always have an updated list
                        Conversation conversationSelected = listConversationUpdated.get(position);//retrieve the conversation list position

                        if (conversationSelected.getIsGroup().equals("true")){
                            //chat group
                            Intent i = new Intent(getActivity(),ChatActivity.class);
                            i.putExtra("chatGroup",conversationSelected.getGroup());
                            startActivity(i);
                        }else {
                            //contact chat
                            Intent i = new Intent(getActivity(),ChatActivity.class);
                            i.putExtra("chatContact",conversationSelected.getUserExhibition());
                            startActivity(i);
                        }

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));


        //set up conversation ref
        String idUserIdentifier = UserFirebase.getIdentifierUser();//returns user ID in base 64
         database = ConfigurationFirebase.getFirebaseDatabase();
         conversationRef = database.child("conversations")
                        .child(idUserIdentifier);

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveConversations();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversationRef.removeEventListener(childEventListenerConversation);
    }

    public void SearchConversations(String text){
        //Log.d("search",text);
        List<Conversation> listConversationsSearch = new ArrayList<>();
        for (Conversation conversation : listConversation){

            if (conversation.getUserExhibition() != null ){
                String name = conversation.getUserExhibition().getName().toLowerCase();
                String lastMsg = conversation.getLastMessage().toLowerCase();

                if (name.contains(text) || lastMsg.contains(text)){
                    listConversationsSearch.add(conversation);
                }
            }else{
                String name = conversation.getGroup().getName().toLowerCase();
                String lastMsg = conversation.getLastMessage().toLowerCase();

                if (name.contains(text) || lastMsg.contains(text)){
                    listConversationsSearch.add(conversation);
                }
            }

        }

        adpter = new ConversationsApadpter(listConversationsSearch,getActivity());
        recyclerViewConversation.setAdapter(adpter);
        adpter.notifyDataSetChanged();
    }

    public void rechargeConversation(){
        adpter = new ConversationsApadpter(listConversation,getActivity());
        recyclerViewConversation.setAdapter(adpter);
        adpter.notifyDataSetChanged();
    }

    public void retrieveConversations(){

        childEventListenerConversation = conversationRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //recover conversations
                Conversation conversation = dataSnapshot.getValue(Conversation.class);
                listConversation.add(conversation);
                adpter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
