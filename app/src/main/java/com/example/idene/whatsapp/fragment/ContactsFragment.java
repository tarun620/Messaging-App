package com.example.idene.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.activity.ChatActivity;
import com.example.idene.whatsapp.activity.GroupActivity;
import com.example.idene.whatsapp.adapter.ContactAdapter;
import com.example.idene.whatsapp.config.ConfigurationFirebase;
import com.example.idene.whatsapp.helper.RecyclerItemClickListener;
import com.example.idene.whatsapp.helper.UserFirebase;
import com.example.idene.whatsapp.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

//COMPLETED

public class ContactsFragment extends Fragment {


    private RecyclerView recyclerViewListContacts;
    private ContactAdapter adapter;
    private ArrayList<User> listContacts = new ArrayList<>();
    private DatabaseReference userRef;
    private ValueEventListener valueEventListenerContacts;
    private FirebaseUser currentUser;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        //Initial settings
        recyclerViewListContacts = view.findViewById(R.id.recyclerViewListContacts);
        userRef = ConfigurationFirebase.getFirebaseDatabase().child("users");
        currentUser = UserFirebase.getCurrentUser();


        //Configure adapter
        adapter = new ContactAdapter(listContacts,getActivity());

        //Configure recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());//
        recyclerViewListContacts.setLayoutManager(layoutManager);
        recyclerViewListContacts.setHasFixedSize(true);
        recyclerViewListContacts.setAdapter(adapter);

        //configure click event on recyclerview
        recyclerViewListContacts.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewListContacts,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {//configure click event on recyclerview

                                List<User> UpdatedUserList = adapter.getContacts();

                                User userSelected = UpdatedUserList.get(position);//retrieves position and saves in selected custom
                                boolean header = userSelected.getEmail().isEmpty();

                                if (header){
                                    Intent i = new Intent(getActivity(), GroupActivity.class);
                                    startActivity(i);
                                }else {
                                    Intent i = new Intent(getActivity(),ChatActivity.class);
                                    i.putExtra("chatContact",userSelected);
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
                )
        );



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recoverContacts();
    }

    @Override
    public void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerContacts);
    }

    public void recoverContacts(){

        //clears the contact list
        listContacts.clear();

//       / * define user with empty email
//                * in case of empty email the user will be used as
//         * header, showing new group * /
        User itemGroup = new User();
        itemGroup.setName("New Group");
        itemGroup.setEmail("");
        listContacts.add(itemGroup);

        valueEventListenerContacts = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data: dataSnapshot.getChildren()){//browse all users

                    User user = data.getValue(User.class);

                    String CurrentUsername = currentUser.getEmail();
                    if (!CurrentUsername.equals(user.getEmail())){
                        listContacts.add(user);
                    }
                }

                adapter.notifyDataSetChanged();//notify that data has been changed

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void SearchContacts(String text){
        //Log.d("search",text);
        List<User> ListContactsSearch = new ArrayList<>();
        for (User user: listContacts) {//list of users

            String name = user.getName().toLowerCase();
            if (name.contains(text)) {
                ListContactsSearch.add(user);
            }
        }

        adapter = new ContactAdapter(ListContactsSearch,getActivity());
        recyclerViewListContacts.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void rechargeContacts(){
        adapter = new ContactAdapter(listContacts,getActivity());
        recyclerViewListContacts.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
