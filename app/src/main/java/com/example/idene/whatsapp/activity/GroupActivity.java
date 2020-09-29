package com.example.idene.whatsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.adapter.ContactAdapter;
import com.example.idene.whatsapp.adapter.GroupSelectedAdapter;
import com.example.idene.whatsapp.config.ConfigurationFirebase;
import com.example.idene.whatsapp.helper.RecyclerItemClickListener;
import com.example.idene.whatsapp.helper.UserFirebase;
import com.example.idene.whatsapp.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//COMPLETED

public class GroupActivity extends AppCompatActivity {

    private RecyclerView recyclerMembersSelected, recyclerMembers;
    private ContactAdapter contactAdapter;
    private GroupSelectedAdapter groupSelectedAdapter;
    private List<User> listMember = new ArrayList<>();
    private List<User> listMemberSelected = new ArrayList<>();
    private ValueEventListener valueEventListenerMember;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private Toolbar toolbar;
    private FloatingActionButton fabAdvanceRegister;

    public void updateMembersToolbar(){

        int totalSelected = listMemberSelected.size();//know how many items we have in the list mebrosSelected
        int total = listMember.size() + totalSelected;//know how many items we have on the members list

        toolbar.setSubtitle(totalSelected + " out of " + total + " selected" +
                "");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New Group");

        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//button back

        //initial settings
        recyclerMembers = findViewById(R.id.recyclerMembers);
        recyclerMembersSelected = findViewById(R.id.recyclerMembersSelected);
        fabAdvanceRegister = findViewById(R.id.fabAdvanceRegister);

        userRef = ConfigurationFirebase.getFirebaseDatabase().child("users");
        currentUser = UserFirebase.getCurrentUser();

        //configure adapter
        contactAdapter = new ContactAdapter(listMember,getApplicationContext());

        //configure recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMembers.setLayoutManager(layoutManager);
        recyclerMembers.setHasFixedSize(true);
        recyclerMembers.setAdapter(contactAdapter);



        //click event
        recyclerMembers.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerMembers,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        User userSelected = listMember.get(position);

                        //remove selected user from list
                        listMember.remove(userSelected);
                        contactAdapter.notifyDataSetChanged();

                        //add user to the new shortlist
                        listMemberSelected.add(userSelected);
                        groupSelectedAdapter.notifyDataSetChanged();

                        updateMembersToolbar();

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

        //configurar recyclerview para os membros selecionados
        groupSelectedAdapter = new GroupSelectedAdapter(listMemberSelected,getApplicationContext());

        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false//ordem de exibicacao dos itens
        );
        recyclerMembersSelected.setLayoutManager(layoutManagerHorizontal);
        recyclerMembersSelected.setHasFixedSize(true);
        recyclerMembersSelected.setAdapter(groupSelectedAdapter);


        recyclerMembersSelected.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerMembersSelected,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                User usuarioSelecionado = listMemberSelected.get(position);

                                //Remover da listagem de membros selecionados
                                listMemberSelected.remove(usuarioSelecionado);
                                groupSelectedAdapter.notifyDataSetChanged();

                                //adicionar a listagem de membros
                                listMember.add(usuarioSelecionado);
                                contactAdapter.notifyDataSetChanged();

                                updateMembersToolbar();

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


        //configure floating action button
        fabAdvanceRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GroupActivity.this, RegisterActivityGroup.class);
                i.putExtra("members",(Serializable) listMemberSelected);
                startActivity(i);
            }
        });

    }

    public void recoverContacts(){

        valueEventListenerMember = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot data: dataSnapshot.getChildren()){//browse all users

                    User user = data.getValue(User.class);

                    String emailCurrentUser = currentUser.getEmail();
                    if (!emailCurrentUser.equals(user.getEmail())){
                        listMember.add(user);
                    }
                }

                contactAdapter.notifyDataSetChanged();//notify that data has been changed
                updateMembersToolbar();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        recoverContacts();
    }

    @Override
    public void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerMember);
    }


}