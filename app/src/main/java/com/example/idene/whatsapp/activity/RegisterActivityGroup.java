package com.example.idene.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.adapter.GroupSelectedAdapter;
import com.example.idene.whatsapp.config.ConfigurationFirebase;
import com.example.idene.whatsapp.helper.UserFirebase;
import com.example.idene.whatsapp.model.Group;
import com.example.idene.whatsapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

//COMPLETED

public class RegisterActivityGroup extends AppCompatActivity {

    //CadastroGrupoActivity--RegisterActivityGroup

    private List<User> listMemberSelected = new ArrayList<>();
    //listaMembroSelecionados--listMemberSelected
    private TextView textTotalParticipants;
    private GroupSelectedAdapter groupSelectedAdapter;
    private RecyclerView recyclerMembersSelected;
    private ImageView imageGroup;
    private static final  int SELECTION_GALLERY =200;
    private StorageReference storageReference;
    private Group group;
    private FloatingActionButton fabSaveGroup;
    private EditText editGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New Group");
        toolbar.setSubtitle("Set a name");
        setSupportActionBar(toolbar);

        //initial settings
        textTotalParticipants = findViewById(R.id.textTotalParticipants);
        recyclerMembersSelected = findViewById(R.id.recyclerMembersGroup);
        imageGroup = findViewById(R.id.imageGroup);
        fabSaveGroup = findViewById(R.id.fabSaveGroup);
        editGroupName = findViewById(R.id.editGroupName);

        group = new Group();

        storageReference = ConfigurationFirebase.getFirebaseStorage();

        //configure click-to-image event
        imageGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null){
                    //check if intent was able to resolve the request to open the photo gallery if the user has a camera
                    startActivityForResult(i, SELECTION_GALLERY);
                }
            }
        });



        // retrieve past member list
        if (getIntent().getExtras() != null){
            List<User> members = (List<User>) getIntent().getExtras().getSerializable("members");
            listMemberSelected.addAll(members);

            textTotalParticipants.setText("Participants: " + listMemberSelected.size());

        }

        // configure recyclerview
        groupSelectedAdapter = new GroupSelectedAdapter(listMemberSelected,getApplicationContext());

        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false// item display order
        );
        recyclerMembersSelected.setLayoutManager(layoutManagerHorizontal);
        recyclerMembersSelected.setHasFixedSize(true);
        recyclerMembersSelected.setAdapter(groupSelectedAdapter);

        // configure floating action button
        fabSaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String groupName = editGroupName.getText().toString();

                // add the list of members to the user who is logged in
                listMemberSelected.add(UserFirebase.getDataUserLogged());
                group.setMembers(listMemberSelected);

                group.setName(groupName);
                group.toSave();

                Intent i = new Intent(RegisterActivityGroup.this,ChatActivity.class);
                i.putExtra("chatGrupo", group);
                startActivity(i);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap image =null;

            try {

                Uri localImageSelects = data.getData();
                image = MediaStore.Images.Media.getBitmap(getContentResolver(),localImageSelects);

                // save image
                if (image != null){
                    imageGroup.setImageBitmap( image );// configure image
                    // save to firebase
                    // retrieve image data for firebase
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG,70,byteArrayOutputStream);
                    byte[] information = byteArrayOutputStream.toByteArray();

                    // Save image to firebase
                    StorageReference imagemRef = storageReference
                            .child("images")
                            .child("groups")
                            .child(group.getId()+ ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(information);
                    uploadTask.addOnFailureListener(new OnFailureListener() {//Mensagem falha
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivityGroup.this,"Error when uploading the image!",Toast.LENGTH_SHORT).show();


                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//Mensagem sucesso
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(RegisterActivityGroup.this,"Image uploaded successfully!",Toast.LENGTH_SHORT).show();

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri url) {
                                    group.setPhotograph(url.toString());
                                }
                            });
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
